package xg.task;

public interface Task {
	
/*	enum TaskStatus{
		unready,ready,processing,success,error
	}*/
	/**
	 * 更新状态
	 * @param status	状态
	 * @param detail	状态变更时附加的描述
	 */
	public void updateStatus(TaskStatus status,String detail);
	/**
	 * 是否已经准备好了
	 * @return
	 */
	public  boolean isReady();
	/**
	 * 是否执行成功
	 * @return
	 */
	public boolean isSuccess();
	/**
	 * 是否执行错误
	 * @return
	 */
	public boolean isError();
	/**
	 * 是否正在处理中
	 * @return
	 */
	public boolean isProcessing();
	/**
	 * 执行
	 */
	public void execute();
	/**
	 * 任务描述
	 * @return
	 */
	public String desc();
	/**
	 * 获取任务ID
	 * @return
	 */
	public String getTaskId();
	
	

}
