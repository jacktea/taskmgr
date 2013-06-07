package xg.task;

public enum TaskStatus {
	
	unready("未准备好",-1),
	ready("准备就绪",0),
	processing("处理中",1),
	success("成功",2),
	error("失败",3);
	
	private String text;
	
	private int value;
	
	private TaskStatus(String text,int value){
		this.text = text;
		this.value = value;
	}

	public static TaskStatus getInstance(int value){
		for(TaskStatus status : TaskStatus.values()){
			if(status.getValue()==value){
				return status;
			}
		}
		return null;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
