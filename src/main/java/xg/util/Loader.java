package xg.util;

import java.net.URL;

public abstract class Loader {

	public static ClassLoader getClassLoaderOfClass(final Class<?> clazz) {
		ClassLoader cl = clazz.getClassLoader();
		if (cl == null) {
			return ClassLoader.getSystemClassLoader();
		} else {
			return cl;
		}
	}

	public static ClassLoader getClassLoaderOfObject(Object o) {
		if (o == null) {
			throw new NullPointerException("Argument cannot be null");
		}
		return getClassLoaderOfClass(o.getClass());
	}

	public static URL getResource(String resource, ClassLoader classLoader) {
		try {
			return classLoader.getResource(resource);
		} catch (Throwable t) {
			return null;
		}
	}
	
	public static URL getResource(String resource) {
		try {
			return getClassLoaderOfClass(Loader.class).getResource(resource);
		} catch (Throwable t) {
			return null;
		}
	}

}
