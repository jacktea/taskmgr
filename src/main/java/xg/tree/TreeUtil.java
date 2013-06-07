package xg.tree;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import xg.util.Assert;
import xg.util.CollectionUtil;
import xg.util.KeyFunction;
import xg.util.MultiValueMap;



public abstract class TreeUtil {
	/***
	 * 树触发器的一个空实现
	 * @author xiaogang
	 *
	 * @param <T>
	 */
	private static class EmptyTreeTrigger<T> implements TreeTrigger<T> {
		@Override
		public boolean traverse(T parent, T current, Iterator<T> iter) {
			return true;
		}
	}
	
	public static <T> void listToTree(MultiValueMap<Object, T> pIdMap, T root,
			final TreeIterator<T> rootIter, final TreeTrigger<T> trigger) {
		rootIter.setRoot(root);
		Object pId = rootIter.getTid();
		if (null != pId) {
			List<T> children = pIdMap.get(pId);
			if (children != null)
				rootIter.addChildren(children);
		}
		Iterator<T> iter = rootIter.childrenIter();
		if (null != iter) {
			while (iter.hasNext()) {
				T current = iter.next();
				if(false==trigger.traverse(root, current, iter))
					break;;
				listToTree(pIdMap, current, rootIter, trigger);
			}
		}
	}
	
	public static <T> void listToTree(MultiValueMap<Object, T> pIdMap, T root,
			final TreeIterator<T> rootIter) {
		listToTree(pIdMap, root, rootIter,new EmptyTreeTrigger<T>());
	}

	/**
	 * 平级列表转化为层级的树状结构
	 * 
	 * @param <T>
	 * @param source
	 *            数据源
	 * @param root
	 *            根节点
	 * @param rootIter
	 *            叠代器
	 * @param trigger
	 *            遍历时触发器
	 * @return
	 */
	public static <T> T listToTree(Collection<T> source, T root,
			final TreeIterator<T> rootIter, final TreeTrigger<T> trigger) {
		Assert.notEmpty(source);
		MultiValueMap<Object, T> pIdMap = CollectionUtil.index(source,
				new KeyFunction<Object, T>() {
					@Override
					public Object getKey(T input) {
						rootIter.setRoot(input);
						return rootIter.getPid();
					}
				});
		listToTree(pIdMap, root, rootIter, trigger);
		return root;
	}

	/**
	 * 平级列表转化为层级的树状结构
	 * 
	 * @param <T>
	 * @param source
	 *            数据源
	 * @param root
	 *            根节点
	 * @param rootIter
	 *            叠代器
	 * @return
	 */
	public static <T> T listToTree(Collection<T> source, T root,
			final TreeIterator<T> rootIter) {
		return listToTree(source, root, rootIter, new EmptyTreeTrigger<T>());
	}

	/**
	 * 树遍历
	 * 
	 * @param rootIter
	 *            叠代器
	 * @param tt
	 *            遍历时触发器
	 */
	public static <T> void traverse(T root, final TreeIterator<T> rootIter,
			final TreeTrigger<T> trigger) {
		rootIter.setRoot(root);
		Iterator<T> iter = rootIter.childrenIter();
		if (null != iter) {
			while (iter.hasNext()) {
				T current = iter.next();
				if(false==trigger.traverse(root, current, iter))
					break;
				rootIter.setRoot(current);
				traverse(current, rootIter, trigger);
			}
		}
	}

	/**
	 * 获取树的深度
	 * 
	 * @param root
	 *            根节点
	 * @param rootIter
	 *            叠代器
	 * @return
	 */
	public static <T> int getDepth(T root, final TreeIterator<T> rootIter) {
		DepthTreeTrigger<T> dtt = new DepthTreeTrigger<T>(rootIter);
		traverse(root, rootIter, dtt);
		return dtt.getDepth();
	}
	

}
