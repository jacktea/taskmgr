package xg.task;
/**
 * 处理信息包装类,包装了处理中的状态信息
 * @author xiaogang
 *
 */
public class TaskProcessStatus {
	
	private TaskStatus status;
	
	private String detail;

	public TaskProcessStatus(TaskStatus status, String detail) {
		super();
		this.status = status;
		this.detail = detail;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

}
