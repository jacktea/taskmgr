package xg.config;

import java.util.List;
import java.util.Map;

import xg.task.ProcessTask;


public interface JobConfig {

	/**
	 * 获取所有的任务
	 * @return
	 */
	public List<ProcessTask> allTask();
	/**
	 * 任务之间的关系
	 * @return
	 */
	public Map<String,String> taskRelationship(); 
	
}
