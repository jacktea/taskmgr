package xg.task.cmd;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.FutureTask;

import xg.task.thread.StreamGobbler1;


public class SystemCommand implements Command {

	private String commond;

	private String charsetName = "gbk";

	private int bufflen = 4000;

	public SystemCommand() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SystemCommand(String commond) {
		super();
		this.commond = commond;
	}

	public SystemCommand(String commond, String charsetName, int bufflen) {
		super();
		this.commond = commond;
		this.charsetName = charsetName;
		this.bufflen = bufflen;
	}

	@Override
	public ExecResult exec() {
		
		ExecResult r = new ExecResult(true);

		Charset charset = Charset.forName(charsetName);
		try {

			Thread.sleep(500);
			// 加锁获取当前信息

			log.debug("startExec \"{}\" ",commond);

			Process proc = Runtime.getRuntime().exec(commond);

			FutureTask<String> detailTask = new FutureTask<String>(
					new StreamGobbler1(proc.getInputStream(), charset, bufflen));
			FutureTask<String> errorTask = new FutureTask<String>(
					new StreamGobbler1(proc.getErrorStream(), charset, bufflen));
			new Thread(detailTask).start();
			new Thread(errorTask).start();
			int ret = proc.waitFor();
			String detail = detailTask.get();
			String error = errorTask.get();

			detail = null == detail ? "" : detail;
			error = null == error ? "" : error;
			if (0 == ret) {
				log.info("execute commond \"{}\" success.",commond);
				r.setSuccess(true);
				r.setDetail(detail);
			} else {
				error = error.equals("") ? detail : error;
				errorlog.error("execute commond \"{}\" failure:{}",commond,error);
				r.setSuccess(true);
				r.setDetail(error);
			}

		} catch (Exception e) {
			e.printStackTrace();
			errorlog.error("execute commond \"{}\" failure:{}",commond, e.getMessage());
			r.setSuccess(false);
			r.setDetail(e.getMessage());
		}
		return r;
	}

	@Override
	public Command create(Map<String, Object> config) {
		if(null!=config){
			this.commond = (String)config.get("commond");
			this.charsetName= config.get("charsetName")==null ? this.charsetName : (String)config.get("charsetName");
			this.bufflen = config.get("bufflen")==null ? this.bufflen : (Integer)config.get("bufflen");
		}
		return this;		
	}

	public String getCommond() {
		return commond;
	}

	public void setCommond(String commond) {
		this.commond = commond;
	}

	public String getCharsetName() {
		return charsetName;
	}

	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	public int getBufflen() {
		return bufflen;
	}

	public void setBufflen(int bufflen) {
		this.bufflen = bufflen;
	}

}
