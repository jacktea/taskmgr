package xg.task.cmd;


public enum ExecMode {
	
	BREAK("break"),CONTINUE("continue");
	
	private String value;
	
	private ExecMode(String value){
		this.value = value;
	}
	
	public static ExecMode getInstance(String value){
		for(ExecMode mode : ExecMode.values()){
			if(value.equals(mode.getValue())){
				return mode;
			}
		}
		return null;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}
