package xg.common;

import java.util.Collection;

import com.google.common.base.Predicate;

public interface CollectionFilter<T> {
	/**
	 * 设置数据源
	 * @param <T>
	 * @param source
	 */
	public void setSource(Collection<T> source);
	/**
	 * 获取源数据
	 * @param <T>
	 * @return
	 */
	public Collection<T> getSource();
	/**
	 * 获取筛选后的数据
	 * @param <T>
	 * @return
	 */
	public Collection<T> getTarget();
	/**
	 * 进行筛选
	 * @param <T>
	 * @param predicate
	 * @return
	 */
	public CollectionFilter<T> filter(Predicate<? super T> predicate);
	/**
	 * 进行筛选
	 * @param <T>
	 * @param condition
	 * @return
	 */
	public CollectionFilter<T> filter(T condition);
	/**
	 * 数据还原
	 */
	public void reset();

}
