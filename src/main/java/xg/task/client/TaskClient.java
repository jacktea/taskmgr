package xg.task.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TaskClient {
	
	static class TCResult{
		private boolean success = true;
		private String msg;
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		@Override
		public String toString() {
			return "TCResult [success=" + success + ", msg=" + msg + "]";
		}

	}

	/**
	 * 建立远程连接并发送命令
	 * 
	 * @param command
	 * @param server
	 * @return 
	 */
	private static TCResult doSend(String command, String server) {
		Socket socket = null;
		TCResult ret = new TCResult();
		try {
			socket = new Socket(server, 50200);
			OutputStream os = socket.getOutputStream();
			BufferedOutputStream out = new BufferedOutputStream(os);
			out.write(command.getBytes());
			out.write('\r');
			out.flush();
			String tmp = read(socket.getInputStream());
			String[] arr = tmp.split("\\|");
			ret.setSuccess(arr[0].equals("success"));
			if(arr.length==2){
				ret.setMsg(arr[1]);
			}
		} catch (UnknownHostException e) {
			ret.setSuccess(false);
			ret.setMsg(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			ret.setSuccess(false);
			ret.setMsg(e.getMessage());
			e.printStackTrace();
		} finally {
			if (socket != null && !socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
					ret.setSuccess(false);
					ret.setMsg(e.getMessage());
				}
			}
		}
		return ret;
	}

	/**
	 * 读取输入流
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static String read(InputStream in) throws IOException {
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
	
	public static TCResult retry(String server,String date){
		return doSend("retry "+date, server);
	}
	
	public static TCResult execute(String server,int granularity){
		return doSend("execute "+granularity, server);
	}
	
	public static TCResult execute(String server,String date,int granularity){
		return doSend("execute "+date+" "+granularity, server);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//System.out.println(execute("172.16.16.20",1));
		//System.out.println(retry("172.16.16.179","20130507"));
		System.out.println(retry("172.16.16.119","20130505"));
	}
}
