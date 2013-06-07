package xg.tree;

import java.util.Iterator;



/**
 * 获取树的深度触发器
 * @author xiaogang
 *
 * @param <T>
 */
public class DepthTreeTrigger<T> implements TreeTrigger<T> {
	
	private TreeIterator<T> rootIter;
	
	private DepthGen dg;
	
	public DepthTreeTrigger(TreeIterator<T> rootIter) {
		super();
		this.rootIter = rootIter;
		this.dg = new DepthGen(0);
	}

	@Override
	public boolean traverse(T parent, T current, Iterator<T> iter) {
		int currentDepth = 0;
		rootIter.setRoot(current);
		T _p = parent;
		while (_p != null) {
			currentDepth++;
			rootIter.setRoot(_p);
			_p = rootIter.getParent();
		}
		dg.max(currentDepth);
		return true;
	}
	
	public int getDepth(){
		return dg.getDepth();
	}
	
	/**
	 * 简单的数据生成器,生产一个比构造大的数
	 * @author xiaogang
	 *
	 */
	private class DepthGen {
		int depth;

		DepthGen(int depth) {
			this.depth = depth;
		}

		void max(int num) {
			if (num > depth) {
				depth = num;
			}
		}
		
		int getDepth() {
			return this.depth;
		}
	}

}
