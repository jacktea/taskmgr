package xg.task.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamGobbler extends Thread {
	
	final Logger log = LoggerFactory.getLogger("dbTask");
	
	final Logger errorlog = LoggerFactory.getLogger("dbTask-error");
	
	InputStream is;
	String type;
	Charset charset;
	Map<String,String> map;
	int maxlen;

	public StreamGobbler(InputStream is,Charset charset,String type,int maxlen,Map<String,String> map) {
		this.is = is;
		this.type = type;
		this.charset = charset;
		this.map = map;
		this.maxlen = maxlen;
	}

	public void run() {
		map.put(type, parseInputStreamToString(is, charset));
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
