package com.sunyard.task.audit;

import xg.task.ProcessTask;

public class AuditTask extends ProcessTask {
	
	private String dataBatch;
	
	private Integer taskType;

	public AuditTask() {
		super();
	}

	public AuditTask(String taskId, String... pTaskId) {
		super(taskId, pTaskId);
	}

	public AuditTask(String taskId) {
		super(taskId);
	}

	public String getDataBatch() {
		return dataBatch;
	}

	public void setDataBatch(String dataBatch) {
		this.dataBatch = dataBatch;
	}

	public Integer getTaskType() {
		return taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}
	

}
