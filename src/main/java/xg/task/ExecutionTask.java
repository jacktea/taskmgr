package xg.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExecutionTask implements Task {
	
	final Logger log = LoggerFactory.getLogger(Task.class);
	
	final Logger errorlog = LoggerFactory.getLogger("task-error");
	
	protected TaskStatus status = TaskStatus.unready;
	

	@Override
	public synchronized void updateStatus(TaskStatus status,String detail) {
		this.status = status;
		if(status==TaskStatus.error){
			errorlog.error(desc()+":execute failure with detail: "+detail);
		}else{
			log.info(desc()+":status change to " + status +" with detail :" + detail);
		}
		//System.out.println(detail);
	}
	/**
	 * 判断任务是否已经准备好可以执行,判断条件:
	 * 所有被依赖的任务已经成功完成
	 */
	@Override
	public synchronized boolean isReady() {
		if(status == TaskStatus.ready) 
			return true;
		boolean ready = checkReady(getDeps());
		
		if(ready){
			updateStatus(TaskStatus.ready,desc()+":所有依赖的任务都已经成功完成,已经准备好,可以执行");
		}
		
		return status == TaskStatus.ready;
	}
	
	protected synchronized boolean checkReady(List<Task> deps){
		boolean ready = true;
		for(Task task : deps){
			ready = ready && task.isSuccess();
		}
		return ready;
	}

	@Override
	public synchronized boolean isSuccess() {
		return status == TaskStatus.success;
	}
	/**
	 * 是否错误,判断条件
	 * 只要有一个被依赖的任务执行出错就错
	 */
	@Override
	public synchronized boolean isError() {
		if(status == TaskStatus.error) 
			return true;
		boolean error = false;
		for(Task task : getDeps()){
			error = error || task.isError();
			if(error){
				updateStatus(TaskStatus.error,desc()+":由于依赖任务:"+task.desc()+" 执行出错,导致该任务无法正确执行");
				break;
			}
		}
		return status == TaskStatus.error;
	}

	public synchronized TaskStatus getStatus() {
		return status;
	}
	@Override
	public synchronized boolean isProcessing() {
		return status == TaskStatus.processing;
	}
	
	public synchronized void setStatus(TaskStatus status) {
		this.status = status;
	}
	public abstract List<Task> getDeps();
	

}
