package xg.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xg.util.CollectionUtil;
import xg.util.KeyFunction;
import xg.util.MultiValueMap;



public class CheckCircleUtil {
	
	public static class Node{
		private String id;
		private String pid;
		public Node(String pid,String id) {
			super();
			this.pid = pid;
			this.id = id;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getPid() {
			return pid;
		}
		public void setPid(String pid) {
			this.pid = pid;
		}
		@Override
		public String toString() {
			return id;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}		
	}
	
	public static class CircleResult{
		private boolean isCircle = false;
		private String circleData;
		public boolean isCircle() {
			return isCircle;
		}
		public void setCircle(boolean isCircle) {
			this.isCircle = isCircle;
		}
		public String getCircleData() {
			return circleData;
		}
		public void setCircleData(String circleData) {
			this.circleData = circleData;
		}
		
	}
	/**
	 * 检测节点是否出现循环依赖
	 * @param source
	 * @return
	 */
	public static CircleResult checkCircle(List<Node> source){
		CircleResult ret = new CircleResult();
		MultiValueMap<String, Node> pIdMap = CollectionUtil.index(source, new KeyFunction<String, Node>() {
			@Override
			public String getKey(Node input) {
				return input.getPid();
			}			
		});
		int size = pIdMap.size();
		loop : for(int i = 0 ; i < size ; i++){
			Iterator<String> iter = pIdMap.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				List<Node> nodes = pIdMap.get(key);				
				if(null!=nodes){
					List<Node> _tmp = new ArrayList<Node>();
					for(Node node : nodes){
						if(node.getId().equals(key)){
							ret.setCircle(true);
							ret.setCircleData(key+":"+nodes);
							break loop;
						}else{
							List<Node> tmp = pIdMap.get(node.getId());
							if(tmp!=null){
								_tmp.addAll(tmp);
							}
						}
					}
					for(Node node : _tmp){
						if(!nodes.contains(node)){
							nodes.add(node);
						}
					}
				}
			}
		}
		return ret;
	}
	
	static List<Node> getSource(){
		List<Node> source = new ArrayList<Node>();
		source.add(new Node("B","C"));
		source.add(new Node("D","F"));
		source.add(new Node("E","G"));
		source.add(new Node("A","B"));
		source.add(new Node("E","H"));
		source.add(new Node("A","C"));
		source.add(new Node("B","D"));
		source.add(new Node("D","E"));
		source.add(new Node("H","D"));
		
		return source;
	}
	

	
	public static void main(String[] args) {
		List<Node> source = getSource();
		CircleResult result = CheckCircleUtil.checkCircle(source);
		if(result.isCircle()){
			System.out.println(result.getCircleData());
		}
	}

}
