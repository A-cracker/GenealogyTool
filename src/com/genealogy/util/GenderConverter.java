package com.genealogy.util;

import com.genealogy.pojo.entity.Member;
/**
 * 
 * @author zzb
 *
 */
public class GenderConverter {
	public static String convert(Integer gender) {
		String result = null;
		if(gender == Member.MAN) {
			result = "man";
		}else {
			result = "woman";
		}
		return result;
	}
	
	public static String convertOpposite(Integer gender) {
		String result = null;
		if(gender == Member.MAN) {
			result = "woman";
		}else {
			result = "man";
		}
		return result;
	}
}
