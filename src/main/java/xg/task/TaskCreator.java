package xg.task;

public interface TaskCreator<T extends CommonTask> {
	
	public T call(String taskId,String[] taskPids,String relation);

}
