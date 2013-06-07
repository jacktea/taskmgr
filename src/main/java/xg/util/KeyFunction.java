package xg.util;

public interface KeyFunction<K, T>{
	K getKey(T input);
}
