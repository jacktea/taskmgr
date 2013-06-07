package xg.task.cmd;

public class ExecResult {
	
	private boolean success;
	
	private String detail;
	

	public ExecResult(boolean success) {
		super();
		this.success = success;
	}

	public ExecResult(boolean success, String detail) {
		super();
		this.success = success;
		this.detail = detail;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	public static void main(String[] args) {
		
	}

}
