package xg.task;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskUtil {
	/**
	 * 运行主线程
	 * @author xiaogang
	 *
	 */
	static class RunThread extends Thread{

		private TaskFactory tf;
		
		private ExecutorService pool;
		
		
		public RunThread(TaskFactory tf, ExecutorService pool) {
			super();
			this.tf = tf;
			this.pool = pool;
		}

		@Override
		public void run() {
			while(tf.processFlag()){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Task task = tf.getTask();
				if(null!=task){
					pool.execute(new ExecuteRunable(task));
				}
			}
			pool.shutdown();
		}
		
	}
	/**
	 * 执行线程
	 * @author xiaogang
	 *
	 */
	static class ExecuteRunable implements Runnable{
		
		private Task task;

		@Override
		public void run() {
			task.execute();
		}

		public ExecuteRunable(Task task) {
			super();
			this.task = task;
		}
		
	}
	/**
	 * 监控线程
	 * @author xiaogang
	 *
	 */
	static class MonitorThread extends Thread{
		
		private TaskFactory tf;
		
		public MonitorThread(TaskFactory tf) {
			super();
			this.tf = tf;
		}

		@Override
		public void run() {
			while(tf.monitorFlag()){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				tf.transfer();
			}
		}
		
	}
	
	private static AtomicBoolean switchMonitor = new AtomicBoolean(true);


	public static boolean getSwitchMonitor() {
		return switchMonitor.get();
	}


	public static void setSwitchMonitor(boolean switchMonitor) {
		TaskUtil.switchMonitor.getAndSet(switchMonitor);
	}





	public static void execute(Set<? extends Task> tasks,int threadSize){
		System.out.println("threadSize:"+threadSize);
		int runThreadSize = threadSize;
		
		TaskFactory tf = new TaskFactory();
		
		tf.prepareData(tasks);
		
		ExecutorService pool = Executors.newFixedThreadPool(runThreadSize);
		
		new RunThread(tf, pool).start();
		
		new MonitorThread(tf).start();
		
		
	}

}
