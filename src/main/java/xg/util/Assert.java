package xg.util;


import java.util.Collection;
import java.util.Map;


public abstract class Assert {


	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isTrue(boolean expression) {
		isTrue(expression, "[Assertion failed] - 输入表达式必须为true");
	}

	public static void isNull(Object object, String message) {
		if (object != null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isNull(Object object) {
		isNull(object, "[Assertion failed] - 输入参数对象必须为null");
	}


	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notNull(Object object) {
		notNull(object, "[Assertion failed] - 输入参数对象不能为null");
	}


	public static void hasLength(String text, String message) {
		if (!StringUtil.hasLength(text)) {
			throw new IllegalArgumentException(message);
		}
	}


	public static void hasLength(String text) {
		hasLength(text,
				"[Assertion failed] - 输入字符串长度必须大于零");
	}


	public static void hasText(String text, String message) {
		if (!StringUtil.hasText(text)) {
			throw new IllegalArgumentException(message);
		}
	}


	public static void hasText(String text) {
		hasText(text,
				"[Assertion failed] - 输入字符串长度必须大于零且不能为空字符串");
	}


	public static void doesNotContain(String textToSearch, String substring, String message) {
		if (StringUtil.hasLength(textToSearch) && StringUtil.hasLength(substring) &&
				textToSearch.indexOf(substring) != -1) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void doesNotContain(String textToSearch, String substring) {
		doesNotContain(textToSearch, substring,
				"[Assertion failed] - 输入字符串的不包含字符串 [" + substring + "]");
	}


	public static void notEmpty(Object[] array, String message) {
		if (array == null || array.length == 0) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notEmpty(Object[] array) {
		notEmpty(array, "[Assertion failed] - 数组不能为空:必须包含至少一个元素");
	}


	public static void noNullElements(Object[] array, String message) {
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == null) {
					throw new IllegalArgumentException(message);
				}
			}
		}
	}


	public static void noNullElements(Object[] array) {
		noNullElements(array, "[Assertion failed] - 数组不能包含为null的元素");
	}


	public static <T> void notEmpty(Collection<T> collection, String message) {
		if (collection == null || collection.isEmpty()) {
			throw new IllegalArgumentException(message);
		}
	}


	public static <T> void notEmpty(Collection<T> collection) {
		notEmpty(collection,
				"[Assertion failed] - 集合不能为空:必须包含至少一个元素");
	}


	public static <K,T> void notEmpty(Map<K,T> map, String message) {
		if (map == null || map.isEmpty()) {
			throw new IllegalArgumentException(message);
		}
	}


	public static <K,T> void notEmpty(Map<K,T> map) {
		notEmpty(map, "[Assertion failed] - 键值对不能为空:必须包含至少一个元素");
	}


	public static <T> void isInstanceOf(Class<T> clazz, Object obj) {
		isInstanceOf(clazz, obj, "");
	}


	public static <T> void isInstanceOf(Class<T> type, Object obj, String message) {
		notNull(type, "类型参数不能为空");
		if (!type.isInstance(obj)) {
			throw new IllegalArgumentException(message +
					"对象 [" + (obj != null ? obj.getClass().getName() : "null") +
					"] 必须是类型 " + type + "的一个实例");
		}
	}

	public static <K,T> void isAssignable(Class<K> superType, Class<T> subType) {
		isAssignable(superType, subType, "");
	}


	public static <K,T> void isAssignable(Class<K> superType, Class<T> subType, String message) {
		notNull(superType, "类型参数不能为空");
		if (subType == null || !superType.isAssignableFrom(subType)) {
			throw new IllegalArgumentException(message + subType + " 不是由类型 " + superType +"派生的");
		}
	}



	public static void state(boolean expression, String message) {
		if (!expression) {
			throw new IllegalStateException(message);
		}
	}

	public static void state(boolean expression) {
		state(expression, "[Assertion failed] - this state invariant must be true");
	}

}

