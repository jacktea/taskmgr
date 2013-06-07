package xg.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;




public class CommonTask extends ExecutionTask {
	
	/**
	 * 任务ID
	 */
	private String taskId;
	/**
	 * 父任务ID
	 */
	private String[] pTaskId;
	
	/**
	 * 当前任务和所依赖任务的关系
	 */
	private String taskRelation;
	
	private TaskExpression exp;

	/**
	 * 开始时间
	 */
	private Date startTime;
	/**
	 * 结束时间
	 */
	private Date endTime;
	/**
	 * 执行时间
	 */
	private Integer totalTime = 0 ;
	/**
	 * 任务类型
	 */
//	private Integer taskType;
	/**
	 * 执行过程进行
	 */
	private String processDesc = "";
	/**
	 * 错误发生时间
	 */
	private Date errorTime;
	/**
	 * 错误信息
	 */
	private String errorDesc = "";
	/**
	 * 执行代码
	 */
//	private String commond;
	
//	private String charsetName = "GBK";
	/**
	 * 处理信息长度
	 */
//	private int bufflen = 4000;
	
	@SuppressWarnings({"rawtypes"})
	private TaskService service;
	
	public CommonTask(){
		
	}
	
	public CommonTask(String taskId) {
		super();
		this.taskId = taskId;
	}

	public CommonTask(String taskId, String... pTaskId) {
		super();
		this.taskId = taskId;
		this.pTaskId = pTaskId;
	}

	private List<CommonTask> deps = new ArrayList<CommonTask>();
	
	/**
	 * 执行任务之前的动作
	 * @param task	要执行的任务
	 * @return
	 */
	public boolean beforeExecute(Task task){
		return true;
	}
	/**
	 * 执行任务
	 * @param task	要执行的任务
	 * @return	任务执行结果
	 */
	public TaskProcessStatus doExecute(Task task){
		return null;
	}
	/**
	 * 任务执行完成后的后续动作
	 * @param task			要执行的任务
	 * @param procStatus	任务执行结果
	 */
	public void afterExecute(Task task,TaskProcessStatus procStatus){
		
	}

	@Override
	public void execute() {
		
		//更改状态为开始执行
		updateStatus(TaskStatus.processing, "任务:"+desc()+" 开始执行");		
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			errorlog.error("thread sleep Interrupted",e1);
		}
		
		synchronized (this) {
			startTime = new Date();
		}
		
		TaskProcessStatus procStatus = new TaskProcessStatus(TaskStatus.processing,"");
		
		if(beforeExecute(this)){
			procStatus = doExecute(this);
		}
		
		afterExecute(this, procStatus);
		
		if(null==procStatus){
			
			procStatus = new TaskProcessStatus(TaskStatus.error,desc()+"doExecute方法未返回执行结果状态");
		
		}
		
		updateStatus(procStatus.getStatus(), procStatus.getDetail());
		
	}

	@Override
	@SuppressWarnings({"unchecked","rawtypes"})
	public synchronized List<Task> getDeps() {
		return (List)deps;
	}

	
	@Override
	public synchronized String desc() {
		return taskId;
	}

	@Override
	public synchronized void updateStatus(TaskStatus status, String detail) {
		super.updateStatus(status, detail);
		switch(status){
		case unready:
			break;
		case ready:
			break;
		case processing:
			break;
		case error:
			errorDesc = detail;
			errorTime = new Date();
			break;
		case success:
			processDesc = detail;
			endTime = new Date();
			totalTime = (int)(endTime.getTime()-startTime.getTime());
			break;
		}
		getService().updateTask(this);
	}
	
	public synchronized String getTaskId() {
		return taskId;
	}

	public synchronized void setTaskId(String taskId) {
		this.taskId = taskId;
	}



	public synchronized Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public synchronized Date getEndTime() {
		return endTime;
	}

	public synchronized void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public synchronized void setErrorTime(Date errorTime) {
		this.errorTime = errorTime;
	}

	public synchronized Integer getTotalTime() {
		return totalTime;
	}

	public synchronized void setTotalTime(Integer totalTime) {
		this.totalTime = totalTime;
	}

	public synchronized String getProcessDesc() {
		return processDesc;
	}

	public synchronized void setProcessDesc(String processDesc) {
		this.processDesc = processDesc;
	}

	public synchronized String getErrorDesc() {
		return errorDesc;
	}

	public synchronized void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public synchronized Date getErrorTime() {
		return errorTime;
	}

	public synchronized void setDeps(List<CommonTask> deps) {
		this.deps = deps;
	}
	
	public synchronized String[] getpTaskId() {
		return pTaskId;
	}

	public synchronized void setpTaskId(String[] pTaskId) {
		this.pTaskId = pTaskId;
	}

	public synchronized String getTaskRelation() {
		return taskRelation;
	}

	public synchronized void setTaskRelation(String taskRelation) {
		this.taskRelation = taskRelation;
	}

	/**
	 * 添加一个依赖,如果有重复,不进行重复添加
	 * @param task
	 */
	public synchronized void addDep(CommonTask task){
		boolean flag = true;
		for(CommonTask t : deps){
			if(t.getTaskId().equals(task.getTaskId())){
				flag = false;
				break;
			}
		}
		if(flag) deps.add(task);
	}
	
	@SuppressWarnings({"unchecked"})
	public <T extends CommonTask> TaskService<T> getService() {
		return service;
	}

	public <T extends CommonTask> void setService(TaskService<T> service) {
		this.service = service;
	}

	/**
	 * 把自己和自己的子元素都放入Set中
	 * @return
	 */
/*	public <T  extends CommonTask> Set<T> toSet(){
		final Set<T> set = new HashSet<T>();
		set.add((T)this);
		TaskTreeIter<T> iter = new TaskTreeIter<T>();
		TreeUtil.traverse((T)this, iter, new TreeTrigger<T>() {
			@Override
			public boolean traverse(T parent, T current,
					Iterator<T> iter) {
				set.add(current);
				return true;
			}
		});
		return set;
	}*/
	
	@Override
	protected synchronized boolean checkReady(List<Task> deps) {
		if(null==taskRelation||"".equals(taskRelation)){
			return super.checkReady(deps);
		}else{
			if(null==exp){
				exp = new TaskExpression(taskRelation);
			}
			for(Task task : deps){
				exp.addVar(task.getTaskId(), task.isSuccess());
			}
			return exp.eval();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CommonTask other = (CommonTask) obj;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		return true;
	}

	
	@Override
	public String toString() {
		return "{ taskId : " + taskId + ", deps : " + deps + "}";
	}
	
/*	private String parseInputStreamToString(InputStream in,Charset charset){
		log.debug("exec detail:");
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		InputStreamReader isReader = null;
		try {
			isReader = new InputStreamReader(in,charset);
			reader = new BufferedReader(isReader);
			String line = null;
			while((line=reader.readLine())!=null){
				log.debug(line);
				sb.append(line+"\n");
			}
			reader.close();
			isReader.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			errorlog.error("Stream to String:",e);
		}finally{
			try {
				if(reader!=null){
					reader.close();
				}
				if(isReader!=null){
					isReader.close();
				}
				if(in!=null){
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				errorlog.error("Stream to String:",e);
			}
		}
		return sb.toString();
	}*/

	public static void main(String[] args) throws Exception{
		
//		Process proc = Runtime.getRuntime().exec("ping 172.16.0.5");
		
/*		InputStream in = proc.getInputStream();
		String detail = parseInputStreamToString(in,Charset.forName("GBK"));
		int ret = proc.waitFor();
		if(0==ret){
			System.out.println("success:");
			System.out.println(detail);
		}else{
			InputStream errorIn = proc.getErrorStream();
			String error = parseInputStreamToString(errorIn,Charset.forName("GBK"));
			System.out.println("error:");
			if(error.equals("")){
				System.out.println(detail);
			}else{
				System.out.println(error);
			}
		}*/
	}

}
