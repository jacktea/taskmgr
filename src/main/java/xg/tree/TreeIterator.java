package xg.tree;

import java.util.Collection;
import java.util.Iterator;

/**
 * 叠代器（享元对象）,用于平级数据转层级
 * 
 * @author Zzg
 * 
 * @param <T>
 */
public interface TreeIterator<T> {
	/**
	 * 设置当前的处理节点
	 * 
	 * @param root
	 */
	public void setRoot(T root);

	/**
	 * 获取当前的节点
	 * 
	 * @return 返回当前正在处理的节点
	 */
	public T getRoot();

	/**
	 * 给当前节点添加子节点
	 * 
	 * @param children
	 */
	public void addChildren(Collection<T> children);

	/**
	 * 获取当前节点子节点的叠代器
	 * 
	 * @return 返回子节点的叠代器
	 */
	public Iterator<T> childrenIter();

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
	public T getParent();

}
