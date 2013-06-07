package xg.task;

import xg.task.cmd.Command;
import xg.task.cmd.ExecResult;

public class ProcessTask extends CommonTask {

	private Command beforeCommand;
	
	private Command command;
	
	private Command afterSuccess;
	
	private Command afterError;
	
	private Command finalCommand;
	
	
	public ProcessTask(){		
	}
		
	public ProcessTask(String taskId) {
		super(taskId);
	}
	
	public ProcessTask(String taskId, String... pTaskId) {
		super(taskId, pTaskId);
	}

	private ExecResult execCommand(Command command){
		ExecResult r = new ExecResult(true);
		if(null!=command){
			r = command.exec();
		}
		return r;
	}

	@Override
	public boolean beforeExecute(Task task) {
		return execCommand(beforeCommand).isSuccess();
	}

	@Override
	public TaskProcessStatus doExecute(Task task) {
		ExecResult r = execCommand(command);
		if(r.isSuccess()){
			return new TaskProcessStatus(TaskStatus.success, r.getDetail());
		}else{
			return new TaskProcessStatus(TaskStatus.error, r.getDetail());
		}
	}

	@Override
	public void afterExecute(Task task, TaskProcessStatus procStatus) {
		
		TaskStatus status = procStatus.getStatus();
		switch (status) {
		case success:		
			execCommand(afterSuccess);
			break;
		case error:		
			execCommand(afterError);
			break;
		default:
			break;
		}
		execCommand(finalCommand);
	}	


	public Command getBeforeCommand() {
		return beforeCommand;
	}

	public void setBeforeCommand(Command beforeCommand) {
		this.beforeCommand = beforeCommand;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public Command getAfterSuccess() {
		return afterSuccess;
	}

	public void setAfterSuccess(Command afterSuccess) {
		this.afterSuccess = afterSuccess;
	}

	public Command getAfterError() {
		return afterError;
	}

	public void setAfterError(Command afterError) {
		this.afterError = afterError;
	}

	public Command getFinalCommand() {
		return finalCommand;
	}

	public void setFinalCommand(Command finalCommand) {
		this.finalCommand = finalCommand;
	}



}
