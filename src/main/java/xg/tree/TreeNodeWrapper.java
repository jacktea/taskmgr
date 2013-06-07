package xg.tree;

import java.util.Collection;
import java.util.Iterator;

public interface TreeNodeWrapper<T> {
	

	/**
	 * 给当前节点添加子节点
	 * 
	 * @param children
	 */
	public void addChildren(Collection<TreeNodeWrapper<T>> children);

	/**
	 * 获取当前节点子节点的叠代器
	 * 
	 * @return 返回子节点的叠代器
	 */
	public Iterator<TreeNodeWrapper<T>> childrenIter();

	/**
	 * 获取当前节点的ID
	 * 
	 * @return 返回当前节点的ID
	 */
	public Object getTid();

	/**
	 * 获取当前节点的父ID
	 * 
	 * @return 返回父节点的ID
	 */
	public Object getPid();

	/**
	 * 获取父节点
	 * 
	 * @return 返回父节点
	 */
	public TreeNodeWrapper<T> getParent();
	
	/**
	 * 获取真实的节点 
	 * @return
	 */
	public T getActualNode();
	
	
	
}
