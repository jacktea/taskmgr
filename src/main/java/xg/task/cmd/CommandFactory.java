package xg.task.cmd;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {

	private static Map<String, Class<? extends Command>> 
		clazzMap = new HashMap<String, Class<? extends Command>>();

	static {
		clazzMap.put("system", SystemCommand.class);
		clazzMap.put("sql", SqlCommand.class);
		clazzMap.put("set", CommandSet.class);
	}
	
	public static void regist(String type,Class<? extends Command> clazz){
		clazzMap.put(type, clazz);
	}
	
	public static Command create(String type,Map<String,Object> config){
		Class<? extends Command> clazz = clazzMap.get(type);
		if(null!=clazz){
			try {
				return clazz.newInstance().create(config);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
