package xg.task.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamGobbler implements Callable<String> {
	
	final Logger log = LoggerFactory.getLogger("task");
	
	final Logger errorlog = LoggerFactory.getLogger("task-error");
	
	InputStream is;
	Charset charset;
	int maxlen;

	public StreamGobbler(InputStream is,Charset charset,int maxlen) {
		this.is = is;
		this.charset = charset;
		this.maxlen = maxlen;
	}

	@Override
	public String call() throws Exception {
		return parseInputStreamToString(is, charset);
	}
	
	private String parseInputStreamToString(InputStream in,Charset charset){
		log.debug("exec detail:");
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		InputStreamReader isReader = null;
		try {
			isReader = new InputStreamReader(in,charset);
			reader = new BufferedReader(isReader);
			String line = null;
			int len = 0;
			while((line=reader.readLine())!=null){
				log.info(line);
				len += line.length()+1;
				if(len<maxlen)
					sb.append(line+"\n");
			}
			reader.close();
			isReader.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			errorlog.error("Stream to String:",e);
		} finally{
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
	}
}
