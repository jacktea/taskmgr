package xg.task.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 命令集合，把多个命令放到一块执行
 * @author xiaogang
 *
 */
public class CommandSet implements Command {	
	/**
	 * 命令集合
	 */
	private List<Command> commands;
	/**
	 * 集合中命令出错时执行的模式<br/>
	 * BREAK 	中止执行剩下的命令<br/>
	 * CONTINUE 继续执行剩下的命令<br/>
	 */
	private ExecMode mode = ExecMode.BREAK;

	public CommandSet() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CommandSet(List<Command> commands) {
		super();
		this.commands = commands;
	}

	public CommandSet(List<Command> commands, ExecMode mode) {
		super();
		this.commands = commands;
		this.mode = mode;
	}


	@Override
	public ExecResult exec() {
		ExecResult r = new ExecResult(true);
		if(null!=commands){
			for(Command command : commands){
				ExecResult er = command.exec();
				boolean success = r.isSuccess()&&er.isSuccess();
				r.setSuccess(success);
				r.setDetail(r.getDetail()+"##"+er.getDetail());
				if(!success){
					if(mode==ExecMode.BREAK){
						break;
					}else if(mode==ExecMode.CONTINUE){
						continue;
					}
				}
			}
		}
		return r;
	}
	/**
	 * 添加命令
	 * @param command
	 */
	public void addCommand(Command command){
		if(null==this.commands){
			this.commands = new ArrayList<Command>();
		}
		this.commands.add(command);
	}

	@Override
	public Command create(Map<String, Object> config) {
		if(null!=config){
			this.mode = null==config.get("mode")	||
						null==ExecMode.getInstance((String)config.get("mode")) ? 
						this.mode : ExecMode.getInstance((String)config.get("mode"));
			@SuppressWarnings({"unchecked","rawtypes"})
			List<Command> commands = (List)config.get("commands");
			if(null!=commands){
				for(Command command : commands){
					addCommand(command);
				}
			
			}
		}
		return this;
	}

}
