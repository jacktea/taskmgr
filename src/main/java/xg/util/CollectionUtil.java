package xg.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public final class CollectionUtil {
	
	/**
	 * 筛选列表中的数据
	 * @param <T>
	 * @param unfiltered		初始数据
	 * @param predicate			条件
	 * @return
	 */
	public static <T> Collection<T> filter(Collection<T> unfiltered,Predicate<? super T> predicate){
		return ImmutableList.copyOf(Iterables.filter(unfiltered, predicate));
	}
	/**
	 * 数据筛选
	 * @param <T>			类型为String时为Like操作
	 * @param unfiltered	初始数据
	 * @param value			条件		
	 * @return
	 */
	public static <T> Collection<T> filter(Collection<T> unfiltered,final T value){
		return filter(unfiltered, new Predicate<T>(){
			@Override
			public boolean apply(T input) {				
				if(value.getClass().equals(String.class)){
					String in = null==input ? "" : (String)input;
					return in.indexOf((String)value)!=-1;
				}
				return Objects.equal(input, value);
			}
		});
	}
	
	/**
	 * 筛选列表(Map)中的数据
	 * @param <K>
	 * @param <V>
	 * @param unfiltered		初始数据
	 * @param key				条件键
	 * @param value				值
	 * @return		map列表中,键为指定key,值为指定值的数据
	 */
	public static <K,V> Collection<Map<K,V>> filterListMap(Collection<Map<K,V>> unfiltered,final K key,final V value){
		return filter(unfiltered, new Predicate<Map<K,V>>(){
			@Override
			public boolean apply(Map<K, V> input) {
				return Objects.equal(input.get(key), value);
			}
		});
	}
	/**
	 * 筛选列表(Map)中的数据
	 * @param <K>
	 * @param <V>
	 * @param unfiltered	初始数据
	 * @param map
	 * @return 当且仅当map中数据完全匹配才返回
	 */
	public static <K,V> Collection<Map<K,V>> filterListMap(Collection<Map<K,V>> unfiltered,final Map<K,V> map){
		return filter(unfiltered, new Predicate<Map<K,V>>(){
			@Override
			public boolean apply(Map<K, V> input) {
				if(null==map||map.isEmpty()) return true;
				Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
				boolean flag = true;
				while(iter.hasNext()){
					Map.Entry<K, V> entry = iter.next();
					V iV = input.get(entry.getKey());
					V cV = entry.getValue();
					flag &= Objects.equal(iV, cV);
					if(!flag)break;
				}
				return flag;
			}
		});
	}

	
	public static <K,T> MultiValueMap<K, T> index(Collection<T> source,KeyFunction<K,T> fn){
		MultiValueMap<K, T> map = new LinkedMultiValueMap<K, T>();
		Iterator<T> iter = source.iterator();
		while(iter.hasNext()){
			T input = iter.next();
			K key = fn.getKey(input);	
			map.add(key, input);
		}
		return map;
	}
	


	
}
