package xg.task;

public interface TaskService<T extends CommonTask> {

	/**
	 * 为任务的任务更新一些必要的属性
	 * @param task
	 */
	public void loadTask(T task);
	/**
	 * 持久化任务
	 * @param task
	 */
	public void updateTask(T task);
	
}
