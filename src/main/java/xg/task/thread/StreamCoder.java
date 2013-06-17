package xg.task.thread;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamCoder extends Thread {
	
	final Logger errorlog = LoggerFactory.getLogger("task-error");
	
	FutureTask<String> task;

	public StreamCoder(InputStream is,Charset charset,int maxlen) {
		task = new FutureTask<String>(
				new StreamGobbler(is, charset, maxlen));
	}

	@Override
	public void run() {
		task.run();
	}
	
	public String get(){
		try {
			return task.get();
		} catch (InterruptedException e) {
			errorlog.error("",e);
		} catch (ExecutionException e) {
			errorlog.error("",e);
		}
		return "";
	}
	
}
