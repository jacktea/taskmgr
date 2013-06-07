package xg.tree;

import java.util.Collection;
import java.util.Iterator;

public class WrapperTreeIterator<T> implements TreeIterator<TreeNodeWrapper<T>> {
	
	TreeNodeWrapper<T> root;

	@Override
	public void setRoot(TreeNodeWrapper<T> root) {
		this.root = root;
	}

	@Override
	public TreeNodeWrapper<T> getRoot() {
		return root;
	}

	@Override
	public void addChildren(Collection<TreeNodeWrapper<T>> children) {
		root.addChildren(children);
	}

	@Override
	public Iterator<TreeNodeWrapper<T>> childrenIter() {
		return root.childrenIter();
	}

	@Override
	public Object getTid() {
		return root.getTid();
	}

	@Override
	public Object getPid() {
		return root.getPid();
	}

	@Override
	public TreeNodeWrapper<T> getParent() {
		return root.getParent();
	}

}
