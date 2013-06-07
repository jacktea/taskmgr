package xg.task;

import java.util.Collection;
import java.util.LinkedList;

public class TaskFactory {
	/**
	 * 待处理队列
	 */
	private LinkedList<Task> prepare = new LinkedList<Task>();
	/**
	 * 处理中队列
	 */
	private LinkedList<Task> process = new LinkedList<Task>();
	/**
	 * 已成功队列
	 */
	private LinkedList<Task> success = new LinkedList<Task>();
	/**
	 * 错误队列
	 */
	private LinkedList<Task> error = new LinkedList<Task>();
	
//	private Object lock = new Object();
	
	/**
	 * 处理任务,由执行线程执行,过程如下:<br/>
	 * 从"待处理队列"中取出一个任务,<br/>
	 * 判断任务是否已经准备好,<br/>
	 * 如果准备好,更改任务状态,放入"处理中队列",并执行任务<br/>
	 * 如果是错误的,放入"错误队列"<br/>
	 * 否则放回"待处理队列"<br/>
	 */
/*	public void process(){
		Task task = null;
		synchronized (this) {
			if(processFlag()){
				task = prepare.poll();
				if(task!=null){
					if(task.isSuccess()){
						success.offer(task);
						task = null;
					}else if(task.isError()){
						error.offer(task);
						task = null;
					}else if(task.isReady()){
						process.offer(task);
						task.updateStatus(TaskStatus.processing,"任务:"+task.desc()+" 进入执行队列");				
					}else{
						prepare.offer(task);
						task = null;
					}
				}
			}
		}
		if(task!=null){
			task.execute();
		}
	}*/
	/**
	 * 获取可执行的任务
	 * 从"待处理队列"中取出一个任务,<br/>
	 * 如果是成功的,放入"成功队列"<br/>
	 * 如果是错误的,放入"错误队列"<br/>
	 * 如果是处理中的,放入"处理队列"<br/>
	 * 否则放回"待处理队列"<br/>
	 * @return
	 */
	public Task getTask(){
		Task task = null;
		synchronized (this) {
			if(processFlag()){
				task = prepare.poll();
				if(task != null){
					if(task.isSuccess()){
						success.offer(task);	
						task = null;
					} else if(task.isError()){
						error.offer(task);
						task = null;
					} else if(task.isProcessing()){
						process.offer(task);
						task = null;
					} else if(task.isReady()){
						return task;
						//process.offer(task);
						//task.updateStatus(TaskStatus.processing,"任务:"+task.desc()+" 进入执行队列");				
					} else{
						prepare.offer(task);
						task = null;
					}
				}
			}
		}
		return task;
	}
	
	/**
	 * 状态监控线程执行,过程如下:<br/>
	 * 从"处理中队列"取出一个任务,<br/>
	 * 判断任务状态<br/>
	 * 如果执行成功,放入成功队列<br/>
	 * 如果执行失败,放入错误队列<br/>
	 * 否则放回"处理中队列"<br/>
	 */
	public synchronized void transfer(){
		if(monitorFlag()){
			Task task = process.poll();
			if(task!=null){
				if(task.isSuccess()){
					success.offer(task);
				}else if(task.isError()){
					error.offer(task);
				}else{
					process.offer(task);
				}
			}
		}
		System.out.println("prepare:"+prepare.size()+"\nprocess:"+process.size()+"\nsuccess:"+success.size()+"\nerror:"+error.size());
	}
	
	public synchronized boolean monitorFlag(){
		return prepare.size()!=0||process.size()!=0;
	}
	
	
	public synchronized boolean processFlag(){
		return prepare.size()!=0;
	}
	
	/**
	 * 准备要执行的任务
	 * @param source
	 */
	public void prepareData(Collection<? extends Task> source){
		prepare.clear();
		process.clear();
		error.clear();
		success.clear();
		prepare.addAll(source);
	}
	
	

}
