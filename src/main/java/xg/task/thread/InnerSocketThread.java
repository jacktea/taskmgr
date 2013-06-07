
package xg.task.thread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import xg.main.Manager;

import com.sunyard.task.audit.AuditTaskService;

/**
 * 对外提供Socket开关
 * 
 */
public class InnerSocketThread extends Thread {
	/**
	 * server
	 */
	private ServerSocket socket;
	
	private AuditTaskService ts;	
	
	final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	
	public AuditTaskService getTs() {
		return ts;
	}

	public void setTs(AuditTaskService ts) {
		this.ts = ts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			socket = new ServerSocket(Manager.PORT);
			while (true) {
				Socket child = socket.accept();
				child.setSoTimeout(5000);

				String command = read(child.getInputStream());			
				String[] args = StringUtils.split(command);
				
				if(args.length<2){
					writeMsg(child.getOutputStream(), "failure|commond error!Usage: <retry|execute> <20110101|1> ");
				}else if(args.length==2 && args[0].equals("retry")){
					try {
						Date date = format.parse(args[1]);
						ts.retry(date);
						writeMsg(child.getOutputStream(), "success|execute :\""+command+"\" ");
					} catch (Exception e) {
						writeMsg(child.getOutputStream(), "failure|execute :\""+command+"\" "+e.getMessage());
					}					
				}else if(args.length==2 && args[0].equals("execute")){
					try {
						ts.execute(Integer.parseInt(args[1]));
						writeMsg(child.getOutputStream(), "success|execute :\""+command+"\" ");
					} catch (NumberFormatException e) {
						writeMsg(child.getOutputStream(), "failure|execute :\""+command+"\" "+e.getMessage());
					}					
				}else if(args.length==3 && args[0].equals("execute")){
					try {
						Date date = format.parse(args[1]);
						ts.execute(date,Integer.parseInt(args[2]));
						writeMsg(child.getOutputStream(), "success|execute :\""+command+"\" ");
					} catch (Exception e) {
						writeMsg(child.getOutputStream(), "failure|execute :\""+command+"\" "+e.getMessage());
					}					
				}else{
					writeMsg(child.getOutputStream(), "failure|no command :\""+command+"\" ");
				}				
				child.close();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读取输入流
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private String read(InputStream in) throws IOException {
		BufferedInputStream bin = new BufferedInputStream(in);
		StringBuffer sb = new StringBuffer();
		while (true) {
			char c = (char) bin.read();
			if (c == '\r') {
				break;
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	private void writeMsg(OutputStream os,String msg)throws IOException{
		BufferedOutputStream out = new BufferedOutputStream(os);
		out.write(msg.getBytes());
		out.write('\r');
		out.flush();
	}
	
	public static void main(String[] args) {
		new InnerSocketThread().start();
	}

}
