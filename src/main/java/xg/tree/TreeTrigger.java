package xg.tree;

import java.util.Iterator;
/**
 * 遍历树型结构时,遍历到指定节点会执行traverse方法
 * @author xiaogang
 *
 * @param <T>
 */
public interface TreeTrigger<T> {
	
	/**
	 * 执行触发器
	 * @param parent	父节点
	 * @param current	当前节点
	 * @param iter		叠代器
	 * @return			false 终止遍历行为
	 */
	public boolean traverse(T parent,T current,Iterator<T> iter);
	
}
