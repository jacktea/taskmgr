package com.sunyard.task.audit;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import xg.task.TaskUtil;


public class AuditTaskService {

	private AuditDBService dbService;
	
	private Integer threadSize = 10;
	
	public AuditDBService getDbService() {
		return dbService;
	}

	public void setDbService(AuditDBService dbService) {
		this.dbService = dbService;
	}
	
	

	public Integer getThreadSize() {
		return threadSize;
	}

	public void setThreadSize(Integer threadSize) {
		this.threadSize = threadSize;
	}
	
	public void execute(Integer granularity){
		//System.out.println(granularity);
		execute(new Date(), granularity);
	}

	/**
	 * 按照指定类型，指定天数　执行任务
	 * 验证该天的前一天是否已经完成，如果完成则执行，如果没有则执行前一天
	 * @param day			指定日期
	 * @param granularity	类型
	 */
	public void execute(Date day,Integer granularity){
		Date yestoday = DateUtils.addDays(day, -1);
		if(dbService.validate(yestoday)){
			TaskUtil.execute(dbService.getTaskSet(day,granularity),threadSize);
		}else{
			TaskUtil.execute(dbService.retryTaskSet(yestoday),threadSize);
		}
	}
	/**
	 * 重新执行某天的任务<br/>
	 * <b>只执行那些失败的</b>
	 * @param day	指定的日期
	 */
	public void retry(Date day){
		TaskUtil.execute(dbService.retryTaskSet(day),threadSize);
	}
	
}
