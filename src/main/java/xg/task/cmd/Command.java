package xg.task.cmd;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Command {
	
	final Logger log = LoggerFactory.getLogger(Command.class);
	
	final Logger errorlog = LoggerFactory.getLogger("task-error");
	/**
	 * 执行
	 * @return
	 */
	public ExecResult exec();
	
	public Command create(Map<String,Object> config);
	
}
