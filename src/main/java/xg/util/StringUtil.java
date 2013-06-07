package xg.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.StringUtils;

public abstract class StringUtil {
	
	/**
	 * 字符串转换 javaCode-->java_code
	 * @param name
	 * @return
	 */
	public static String underscoreName(String name) {
		StringBuilder result = new StringBuilder();
		if (name != null && name.length() > 0) {
			result.append(name.substring(0, 1).toLowerCase());
			for (int i = 1; i < name.length(); i++) {
				String s = name.substring(i, i + 1);
				if (!isNumeric(s)&&s.equals(s.toUpperCase())&&!s.equals("_")) {
					result.append("_");
					result.append(s.toLowerCase());
				}
				else {
					result.append(s);
				}
			}
		}
		return result.toString();
	}
	
	/**
	 * 字符串转换 java_code-->javaCode,JAVA_CODE-->javaCode
	 * @param name
	 * @return
	 */
	public static String codeName(String name){
		StringBuilder result = new StringBuilder();
		if (name != null && name.length() > 0) {
			name = name.toLowerCase();
			String[] ss = name.split("_");
			result.append(ss[0]);
			for(int i=1 ; i <ss.length ; i++){
				result.append(StringUtils.capitalize(ss[i]));
			}
		}
		return result.toString();
	}
	
	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}
	/**
	 * sql参数检查
	 * @param v
	 * @return
	 */
	public static String escapeSql(String v){
		return StringEscapeUtils.escapeSql(v);
	}

	/**
	 * Check that the given String is neither <code>null</code> nor of length 0.
	 * Note: Will return <code>true</code> for a String that purely consists of whitespace.
	 * @param str the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not null and has length
	 * @see #hasLength(CharSequence)
	 */
	public static boolean hasLength(String str) {
		return hasLength((CharSequence) str);
	}
	
	public static boolean hasText(CharSequence str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	public static String appendString(String in,int totalLen,String append){
		if(null==append) append = " ";
		int len = totalLen - in.length();
		if(len<0){
			throw new IllegalArgumentException("输入字符串的长度大于要扩展成的长度.");
		}
		if(append.length()>1){
			throw new IllegalArgumentException("要添加的字符串长度必须为1.");
		}
		StringBuilder sb = new StringBuilder(in);
		for(int i =0 ; i < len ; i++){
			sb.append(append);
		}
		return sb.toString();
	}
	
	public static String getPatternValue(Pattern pattern, String source) {
		Matcher matcher = pattern.matcher(source);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "";
	}

	public static List<String> getPatternValues(Pattern pattern, String source) {
		List<String> list = new ArrayList<String>();
		Matcher matcher = pattern.matcher(source);
		while (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				list.add(matcher.group(i));
			}
		}
		return list;
	}
	
	public static boolean isNumeric(String str){  
		Pattern pattern = Pattern.compile("[0-9]*");  
		return pattern.matcher(str).matches();     
	}
	
	public static void main(String[] args) {
		System.out.println(xg.util.StringUtil.underscoreName("ht_operateorgidname1"));
	}
	

}
