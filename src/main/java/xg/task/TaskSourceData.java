package xg.task;

import java.util.List;
import java.util.Map;

import xg.util.LinkedMultiValueMap;
import xg.util.MultiValueMap;


public class TaskSourceData {
	
	private List<Map<String,String>> source;

	public List<Map<String, String>> getSource() {
		return source;
	}

	public void setSource(List<Map<String, String>> source) {
		this.source = source;
	}
	
	public MultiValueMap<String, String> init(){
		MultiValueMap<String, String> pIdMap = new LinkedMultiValueMap<String, String>();
		if(null!=source){
			for(Map<String,String> m : source){
				String taskId= m.get("taskId");
				String taskRelation = m.get("taskRelation");
				if(null!=taskRelation){
					TaskExpression exp =new TaskExpression(taskRelation);
					for(String p : exp.varNames()){
						pIdMap.add(p, taskId);
					}
				}
			}
		}
		return pIdMap;
	}
	
	public ProcessTask loadTask(String taskId,String[] taskPid){
		return null;
	}

}
