package xg.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import xg.task.CheckCircleUtil;
import xg.task.CommonTask;
import xg.task.TaskCreator;
import xg.task.TaskExpression;
import xg.task.TaskStatus;
import xg.task.CheckCircleUtil.CircleResult;
import xg.task.CheckCircleUtil.Node;
import xg.tree.TreeIterator;
import xg.tree.TreeTrigger;
import xg.tree.TreeUtil;
import xg.util.LinkedMultiValueMap;
import xg.util.MultiValueMap;


public class TaskTree<T extends CommonTask> {
	
	public interface FilterCallBack<T extends CommonTask>{
		public void execute(T source,T target);
	}
	
	static class TaskTreeIter<T extends CommonTask> implements TreeIterator<T>{		
		
		T root;
		
		@Override
		public void setRoot(T root) {
			this.root = root;
		}
		@Override
		public T getRoot() {
			return root;
		}
		@Override
		@SuppressWarnings({"unchecked","rawtypes"})
		public void addChildren(Collection<T> children) {
			List<T> deps = (List)root.getDeps();
			for(T task : children){
				if(!deps.contains(task)){
					deps.add(task);
				}
			}
		}
		@Override
		@SuppressWarnings({"unchecked","rawtypes"})
		public Iterator<T> childrenIter() {
			return (Iterator)root.getDeps().iterator();
		}
		@Override
		public Object getTid() {
			return root.getTaskId();
		}
		@Override
		public Object getPid() {
			
			return root.getpTaskId();
			
		}
		@Override
		public T getParent() {
			return null;
		}
	}
	
	class TaskHolder {
		private T target;

		public TaskHolder() {
			super();
		}

		public TaskHolder(T target) {
			super();
			this.target = target;
		}

		public T getTarget() {
			return target;
		}

		public void setTarget(T target) {
			this.target = target;
		}
		
	}
	
	private T root;
	
	private TaskTree(){
		
	}
	
	public static <T extends CommonTask> TaskTree<T> instance(){
		return new TaskTree<T>();
	}
	
	
	/**
	 * 返回数据源中那些"根"节点ID<br/>
	 * 判断条件：节点ＩＤ没有在ＰＩＤ中出现过的节点<br/>
	 * 在createTree方法中会统一为这些节点创建统一的虚拟根节点＂root＂
	 * @param source
	 * @return
	 */
	private Set<String> rootKeySet(List<T> source){
		Set<String> set = new HashSet<String>();
		
		Set<String> keySet = new HashSet<String>();
		for(T task : source){
			keySet.addAll(Arrays.asList(task.getpTaskId()));
		}
		
		for(String pid : keySet){
			for(T c : source){
				if(c.getTaskId().equals(pid)){
					pid = null;
					break;
				}
			}
			if(null!=pid){
				set.add(pid);
			}
		}
		return set;
	}
	
	boolean isCircle(List<T> source){
		List<Node> data = new ArrayList<Node>();
		for(T ct : source){
			ct.getpTaskId();
			for(String p : ct.getpTaskId()){
				data.add(new Node(p, ct.getTaskId()));
			}
		}
		CircleResult cr = CheckCircleUtil.checkCircle(data);
		if(cr.isCircle()){
			throw new RuntimeException(cr.getCircleData());
		}
		return cr.isCircle();
	}
	
	public List<T> chgSource(List<T> source,TaskCreator<T> creator){
		MultiValueMap<String, String> pIdMap = new LinkedMultiValueMap<String, String>();
		for(T ct : source){
			if(null!=ct.getpTaskId()){
				for(String p : ct.getpTaskId()){
					pIdMap.add(ct.getTaskId(),p);
				}
			}
		}
		
		List<T> ret = new ArrayList<T>();
		Iterator<String> iter = pIdMap.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			T task = creator.call(key, pIdMap.get(key).toArray(new String[]{}), null);
			ret.add(task);
		}
		return ret;
	}

	/**
	 * 根据输入的平级数据结构构建出一棵树，动态创建虚拟的根结点("root")<br/>
	 * 执行步骤：<br/>
	 * 1.验证是否存在循环依赖<br/>
	 * 2.选出所有的根节点,并为他们创建虚拟根节点(root)<br/>
	 * 3.生成树
	 * @param source	源数据
	 * @param creator	任务实例生成器
	 * @return
	 */
	public TaskTree<T> createTree(List<T> source,TaskCreator<T> creator){
		isCircle(source);
		List<T> chgSource = chgSource(source,creator);		
		T root = creator.call("root", null, null);
		for(String tid : rootKeySet(chgSource)){
			if(!tid.equals("root")){				
				chgSource.add(creator.call(tid, new String[]{"root"}, null));
			}
		}
		MultiValueMap<Object, T> pIdMap = new LinkedMultiValueMap<Object, T>();
		for(T ct : chgSource){
			for(String key : ct.getpTaskId()){
				pIdMap.add(key, ct);
			}
		}
		TaskTreeIter<T> rootIter = new TaskTreeIter<T>();
		TreeUtil.listToTree(pIdMap, (T)root, rootIter);
		this.root = root;
		return this;
	}
	
	/**
	 * 创建任务树
	 * @param relationShip	任务之间的关系<br/>  
	 * 	key:任务号 value：和所依赖的任务的关系
	 * 例：<br/>
	 * {
	 *  A:B,
	 *  B:C,
	 *  D:E&&F,
	 *  F:H||C
	 * }
	 * @param creator	具体任务实例生成器
	 * @return
	 */
	public TaskTree<T> createTree(Map<String, String> relationShip,TaskCreator<T> creator){
		MultiValueMap<String, String> pIdMap = new LinkedMultiValueMap<String, String>();
		if(null!=relationShip){
			for(String taskId : relationShip.keySet()){
				String taskRelation = relationShip.get(taskId);
				if(null!=taskRelation){
					TaskExpression exp =new TaskExpression(taskRelation);
					for(String p : exp.varNames()){
						pIdMap.add(p, taskId);
					}
				}
			}
		}
		List<T> tasks = new ArrayList<T>();
		for(String taskId : pIdMap.keySet()){
			String taskRelation = relationShip.get(taskId);
			String[] taskPids = pIdMap.get(taskId).toArray(new String[]{});
			T task = creator.call(taskId, taskPids, taskRelation);
			if(null!=task){
				task.setTaskRelation(taskRelation);
				tasks.add(task);
			}
		}
		//验证是否存在循环依赖
		isCircle(tasks);		
		T root = creator.call("root", null, null);
		for(String tid : rootKeySet(tasks)){
			if(!tid.equals("root")){
				tasks.add(creator.call(tid, new String[]{"root"}, null));
			}
		}		
		MultiValueMap<Object, T> pIdCtMap = new LinkedMultiValueMap<Object, T>();
		for(T ct : tasks){
			for(String key : ct.getpTaskId()){
				pIdCtMap.add(key, ct);
			}
		}		
		TaskTreeIter<T> rootIter = new TaskTreeIter<T>();
		TreeUtil.listToTree(pIdCtMap, root, rootIter);
		this.root = root;
		return this;
		
		
		//return createTree(tasks);
	}
	
	
	
	
	/**
	 * 更改数据源<br/>
	 * CommonTask("ODS_TESTTAB2", "ODS_TESTTAB5")<br/>
	 * CommonTask("ODS_TESTTAB2", "ODS_TESTTAB6")<br/>
	 * 更改为 <br/>
	 * CommonTask("ODS_TESTTAB2","ODS_TESTTAB5","ODS_TESTTAB6")<br/>
	 * @param source
	 * @return
	 */
	public List<CommonTask> chgSource(List<? extends CommonTask> source){
		MultiValueMap<String, String> pIdMap = new LinkedMultiValueMap<String, String>();
		for(CommonTask ct : source){
			if(null!=ct.getpTaskId()){
				for(String p : ct.getpTaskId()){
					pIdMap.add(ct.getTaskId(),p);
				}
			}
		}
		
		List<CommonTask> ret = new ArrayList<CommonTask>();
		Iterator<String> iter = pIdMap.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			ret.add(new CommonTask(key,pIdMap.get(key).toArray(new String[]{})));
		}
		return ret;
	}
	
	/**
	 * 遍历这棵任务树，连同虚拟的根节点
	 * @param trigger	遍历时触发的回调
	 */
	public void traverseWithRoot(TreeTrigger<T> trigger){
		trigger.traverse(null, root, null);
		TreeUtil.traverse(root, new TaskTreeIter<T>(), trigger);
	}
	/**
	 * 遍历这棵任务树
	 * @param trigger	遍历时触发的回调
	 */
	public void traverse(TreeTrigger<T> trigger){
		TreeUtil.traverse(root, new TaskTreeIter<T>(), trigger);
	}
	/**
	 * 筛选节点,如果找到返回树中的节点，否则返回筛选对象
	 * @param task
	 * @return
	 */
	public T filter(final T task){
		return filter(task, new FilterCallBack<T>() {
			@Override
			public void execute(T source, T target) {
			}
		});
	}
	
	public T filter(final T task,final FilterCallBack<T> fc){
		final TaskHolder holder = new TaskHolder(task);
		traverse(new TreeTrigger<T>() {
			@Override
			public boolean traverse(T parent, T current,
					Iterator<T> iter) {
				if(current.equals(task)){
					fc.execute(task, current);
					holder.setTarget(current);
					return false;
				}
				return true;
			}			
		});
		return holder.getTarget();
	}
	
	/**
	 * 筛选节点,如果找到返回树中的节点，否则返回NULL
	 * @param task
	 * @return
	 */
	public T filter(final String taskId){
		final TaskHolder holder = new TaskHolder();
		traverse(new TreeTrigger<T>() {
			@Override
			public boolean traverse(T parent, T current,
					Iterator<T> iter) {
				if(current.getTaskId().equals(taskId)){
					holder.setTarget(current);
					return false;
				}
				return true;
			}			
		});
		return holder.getTarget();
	}
	/**
	 * 把传入参数本身及所有子节点放入set中
	 * @param root
	 * @return
	 */
	public Set<T> toSet(T root){
		final Set<T> set = new HashSet<T>();
		set.add(root);
		TaskTreeIter<T> iter = new TaskTreeIter<T>();
		TreeUtil.traverse(root, iter, new TreeTrigger<T>() {
			@Override
			public boolean traverse(T parent, T current,
					Iterator<T> iter) {
				set.add(current);
				return true;
			}
		});
		return set;
	}
	
	
	static TaskTree<CommonTask> test1(){
		Map<String,String> ship = new HashMap<String, String>();
		ship.put("ODS_TESTTAB5", "ODS_TESTTAB2");
		ship.put("ODS_TESTTAB6", "ODS_TESTTAB2&&ODS_TESTTAB5");
		ship.put("ODS_TESTTAB7", "ODS_TESTTAB1");
		ship.put("ODS_TESTTAB8", "ODS_TESTTAB1&&ODS_TESTTAB7");
		
		TaskTree<CommonTask> tree = TaskTree.instance();
		
		tree.createTree(ship, new TaskCreator<CommonTask>() {
			@Override
			public CommonTask call(String taskId, String[] taskPids, String relation) {
				return new CommonTask(taskId, taskPids);
			}
		});
		
		return tree;
	}
	
	static TaskTree<CommonTask> test2(){
		List<CommonTask> source = new ArrayList<CommonTask>();
		source.add(new CommonTask("ODS_TESTTAB2", "ODS_TESTTAB5"));
		source.add(new CommonTask("ODS_TESTTAB2", "ODS_TESTTAB6"));
		source.add(new CommonTask("ODS_TESTTAB5", "ODS_TESTTAB6"));
		source.add(new CommonTask("ODS_TESTTAB1", "ODS_TESTTAB7"));
		source.add(new CommonTask("ODS_TESTTAB1", "ODS_TESTTAB8"));
		source.add(new CommonTask("ODS_TESTTAB7", "ODS_TESTTAB8"));
		
		TaskTree<CommonTask> tree = TaskTree.instance();

		System.out.println(tree.isCircle(source));
		
		tree.createTree(source, new TaskCreator<CommonTask>() {
			@Override
			public CommonTask call(String taskId, String[] taskPids, String relation) {
				return new CommonTask(taskId, taskPids);
			}
		});
		
		return tree;
		
		
	}
	
	static TaskTree<CommonTask> test3(){
		List<CommonTask> source = new ArrayList<CommonTask>();
		source.add(new CommonTask("ODS_TESTTAB2", "ODS_TESTTAB5","ODS_TESTTAB6"));
		source.add(new CommonTask("ODS_TESTTAB5", "ODS_TESTTAB6"));
		source.add(new CommonTask("ODS_TESTTAB1", "ODS_TESTTAB7","ODS_TESTTAB8"));
		source.add(new CommonTask("ODS_TESTTAB7", "ODS_TESTTAB8"));
		
		TaskTree<CommonTask> tree = TaskTree.instance();

		System.out.println(tree.isCircle(source));
		
		tree.createTree(source, new TaskCreator<CommonTask>() {
			@Override
			public CommonTask call(String taskId, String[] taskPids, String relation) {
				return new CommonTask(taskId, taskPids);
			}
		});
		
		return tree;
		
	}
	
	static void test(TaskTree<CommonTask> tree){
		tree.traverse(new TreeTrigger<CommonTask>() {
			@Override
			public boolean traverse(CommonTask parent, CommonTask current,
					Iterator<CommonTask> iter) {
				StringBuilder sb = new StringBuilder();
				sb.append("tid="+current.getTaskId());
				if(null!=current.getpTaskId()){
					sb.append("pid=");
					for(String p : current.getpTaskId()){
						sb.append(p);
						sb.append(",");
					}
				}
				System.out.println(sb);
				return true;
			}			
		});
		CommonTask task = tree.filter("ODS_TESTTAB1");
		task.setStatus(TaskStatus.success);
		System.out.println(task.desc()+":"+task.isSuccess());
		task = tree.filter("ODS_TESTTAB7");
		System.out.println(task.desc()+":"+task.isReady());
		task.setStatus(TaskStatus.success);
		task = tree.filter("ODS_TESTTAB8");
		System.out.println(task.desc()+":"+task.isReady());
	}
	
	public static void main(String[] args) {
		
//		CommonTaskTree<CommonTask> tree = test1();
		TaskTree<CommonTask> tree = test2();
//		CommonTaskTree<CommonTask> tree = test3();
		test(tree);
		

		
/*		source.add(new CommonTask("H", "E"));
		source.add(new CommonTask("G", "E"));
		source.add(new CommonTask("E", "D"));
		source.add(new CommonTask("F", "D"));
		source.add(new CommonTask("C", "B"));
		source.add(new CommonTask("D", "B"));
		source.add(new CommonTask("B", "A"));
		source.add(new CommonTask("C", "A"));
		source.add(new CommonTask("J", "I"));
		source.add(new CommonTask("K", "I"));
		source.add(new CommonTask("L", "K"));
		source.add(new CommonTask("U", "M"));
		source.add(new CommonTask("V", "M"));
		source.add(new CommonTask("W", "N"));
		source.add(new CommonTask("Y", "X"));
		source.add(new CommonTask("Z", "X"));
		source.add(new CommonTask("M", "G"));
		
		CommonTaskTree tree = CommonTaskTree.instance();
		
		//System.out.println(tree.isCircle(source));
		
		tree.createTree(source);
		
		tree.traverse(new TreeTrigger<CommonTask>() {
			@Override
			public boolean traverse(CommonTask parent, CommonTask current,
					Iterator<CommonTask> iter) {
				System.out.println(current.getTaskId());
				return true;
			}			
		});
		
		
		
		System.out.println(tree.filter("M"));
		System.out.println(tree.filter("C"));
		System.out.println(tree.filter("X"));
		System.out.println(tree.filter("E"));
		System.out.println(tree.filter("O"));*/
		
		

	}

}
