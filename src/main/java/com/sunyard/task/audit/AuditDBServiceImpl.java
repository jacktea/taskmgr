package com.sunyard.task.audit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import xg.task.TaskCreator;
import xg.task.TaskService;
import xg.task.TaskStatus;
import xg.task.TaskTree;
import xg.task.cmd.Command;
import xg.task.cmd.SystemCommand;


public class AuditDBServiceImpl implements TaskService<AuditTask>,AuditDBService{
	
	final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

	final SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private JdbcTemplate jdbcTemplate;
	
	private String charsetName;
	
	private Integer bufflen;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public String getCharsetName() {
		return charsetName;
	}
	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}
	
	public Integer getBufflen() {
		return bufflen;
	}
	public void setBufflen(Integer bufflen) {
		this.bufflen = bufflen;
	}
	
	public TaskTree<AuditTask> createTaskTree() {
		List<AuditTask> source = jdbcTemplate.query("select task_id,ttask_id from AD_DATA_DEPANDONTB",
				new RowMapper<AuditTask>() {
					@Override
					public AuditTask mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						AuditTask task = new AuditTask(rs.getString(2), rs.getString(1));
						return task;
					}
				});
		TaskTree<AuditTask> tree = TaskTree.instance();
		return tree.createTree(source, new TaskCreator<AuditTask>() {			
			@Override
			public AuditTask call(String taskId, String[] taskPids, String relation) {
				return new AuditTask(taskId,taskPids);
			}
		});
	}
	
	private Set<AuditTask> createTaskSet(TaskTree<AuditTask> tree,List<AuditTask> ori){
		Set<AuditTask> set = new HashSet<AuditTask>();
		for(AuditTask c : ori){
			set.addAll(tree.toSet(tree.filter(c)));
		}
		return set;
	}
	
	private Set<AuditTask> createRetryTaskSet(TaskTree<AuditTask> tree,List<AuditTask> ori){
		Set<AuditTask> set = new HashSet<AuditTask>();
		for(AuditTask c : ori){
			AuditTask root = tree.filter(c,new TaskTree.FilterCallBack<AuditTask>() {
				@Override
				public void execute(AuditTask source, AuditTask target) {
					TaskStatus status = source.getStatus();
					if(status==TaskStatus.success){
						target.setStatus(TaskStatus.success);
					}else{
						target.setStatus(TaskStatus.unready);
					}
				}
			});
			set.addAll(tree.toSet(root));
		}
		return set;
	}
	
	public Set<AuditTask> getTaskSet(Date day,Integer granularity){
		String dataBatch = format.format(day);
		//从任务表里取出任务信息
		List<AuditTask> ori = jdbcTemplate.query(
				"select id from AD_DTASK_MANGERTB where granularity = ? ",
				new Object[] { granularity }, new RowMapper<AuditTask>() {
					@Override
					public AuditTask mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						AuditTask task = new AuditTask(rs.getString(1));
						return task;
					}
				});
		//生成任务关系树
		TaskTree<AuditTask> totalTree = createTaskTree();
		//把任务树转化成任务链表
		Set<AuditTask> set = createTaskSet(totalTree,ori);
		//装载一些必要的属性
		for(AuditTask task : set){
			task.setDataBatch(dataBatch);
			task.setService(this);
			loadTask(task);
		}
		//持久化任务链表
		saveTaskList(set);		
		return set;
	}
	
	public Set<AuditTask> retryTaskSet(Date day){
		String dataBatch = format.format(day);
		List<AuditTask> ori = jdbcTemplate.query(
				"select task_id,state from AD_DATA_TASKTB where data_batch = ?",
				new Object[] { dataBatch }, new RowMapper<AuditTask>() {
					@Override
					public AuditTask mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						AuditTask task = new AuditTask(rs.getString(1));
						int status = rs.getInt(2);
						task.setStatus(TaskStatus.getInstance(status));
						return task;
					}
				});
		
		TaskTree<AuditTask> totalTree = createTaskTree();
		
		Set<AuditTask> set = createRetryTaskSet(totalTree,ori);
		
		for(AuditTask task : set){
			task.setDataBatch(dataBatch);
			task.setService(this);
			loadTask(task);
		}
		
		return set;
	}



	/**
	 * 装载一些必要的属性
	 * @param task
	 */
	public void loadTask(AuditTask task) {
		String commandStr = jdbcTemplate.queryForObject(
				"select exe_contents from AD_TABINFO_SCRIPTTB where task_id = ? ",
				String.class,task.getTaskId());
		
		/*
		Map<String,Object> config = new HashMap<String, Object>();
		config.put("charsetName", charsetName);
		config.put("bufflen", null==bufflen ? 4000 : bufflen);
		Command command = CommandFactory.create("system", config);
		*/
		
		Command command = new SystemCommand(commandStr,charsetName,null==bufflen ? 4000 : bufflen);
		task.setCommand(command);
		Integer taskType;
		try {
			taskType = jdbcTemplate.queryForObject(
					"select task_type from AD_DTASK_MANGERTB where id = ? ", Integer.class, task.getTaskId());
			task.setTaskType(taskType);
			
		} catch (DataAccessException e) {
		}
	}
	/**
	 * 保存任务列表
	 * @param set
	 */
	public void saveTaskList(Set<AuditTask> set){
		Integer id = jdbcTemplate.queryForObject("select max(ID) from AD_DATA_TASKTB", Integer.class);
		id = null==id ? 1 : id+1;
		for(AuditTask task : set){
			if(null!=task.getTaskType())
				jdbcTemplate.update(
					"insert into AD_DATA_TASKTB (ID,task_id,data_batch,state,task_type) values (?,?,?,?,?)",
							id++,task.getTaskId(),task.getDataBatch(),task.getStatus(),task.getTaskType());
			else
				jdbcTemplate.update(
					"insert into AD_DATA_TASKTB (ID,task_id,data_batch,state) values (?,?,?,?)",
							id++,task.getTaskId(), task.getDataBatch(),task.getStatus());
		}
	}
	/**
	 * 更新任务状态
	 * @param task
	 */
	public void updateTask(AuditTask task){
		
		Date _startTime = task.getStartTime();
		Date _endTime = task.getEndTime();
		Date _errorTime = task.getErrorTime();
		String startTime = "";
		String endTime = "";
		String errorTime = "";
		
		synchronized (this) {
			startTime = _startTime!=null ? formatTime.format(_startTime) : "";
			endTime = _endTime!=null ? formatTime.format(_endTime) : "";
			errorTime = _errorTime!=null ? formatTime.format(_errorTime) : "";
		}		
		
		jdbcTemplate
			.update("update AD_DATA_TASKTB set state = ? ,start_time = ? ,end_time = ? ,time_length = ? " +
					"where task_id = ? and data_batch = ? ",
						task.getStatus(),startTime,endTime,task.getTotalTime(),
						task.getTaskId(), task.getDataBatch());
		if(task.getStatus()==TaskStatus.error){
			synchronized (this) {
				Integer id = jdbcTemplate.queryForObject("select max(ID) from AD_DATA_LOGTB", Integer.class);
				id = null==id ? 1 : id+1;
				jdbcTemplate.update(
					"insert into AD_DATA_LOGTB (ID,task_id,CREATE_TIME,ERROR_DESC) values (?,?,?,?) ",
							id,task.getTaskId(), errorTime,
							task.getErrorDesc());
			}
		}
	}
	/**
	 * 判断指定日期跑批程序是否可以运行
	 * @param day	指定的日期
	 * @return	true 可以　false 不可以
	 */
	public boolean validate(Date day){
		String dateBatch = format.format(day);
		Long c = jdbcTemplate.queryForLong(
						"select count(*) from AD_DATA_TASKTB where data_batch = ? and state <> 2",
						dateBatch);
		return !(c!=null&&c>0);
	}

}
