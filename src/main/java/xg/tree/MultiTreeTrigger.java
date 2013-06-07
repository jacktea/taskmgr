package xg.tree;


/**
 * 遍历树型结构时,遍历到指定节点会执行traverse方法,它可以包含其他多个触发器
 * @author xiaogang
 *
 * @param <T>
 */
public interface MultiTreeTrigger<T> extends TreeTrigger<T> {

	/**
	 * 添加一个触发器
	 * 
	 * @param trigger
	 */
	public void addTrigger(TreeTrigger<T> trigger);
	
}
