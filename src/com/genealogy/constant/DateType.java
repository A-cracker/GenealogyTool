package com.genealogy.constant;


/**
 * 日期类型
 * 
 */
public enum DateType {

	/**
	 * 年月日
	 */
	DATE(0),

	/**
	 * 朝代
	 */
	DYNASTY(1);

	private Integer value;

	private DateType(Integer value){
		this.value = value;
	}

	/**
	 * 取得枚举值
	 * @return 枚举值
	 */
	public Integer value(){
		return this.value;
	}

	/**
	 * 根据枚举值取得指定枚举常量
	 * @param value int类型的枚举值
	 * @return 枚举常量
	 */
	public static DateType valueOf(Integer value) {
		for (DateType element : DateType.values()) {
			if (element.value.equals(value)) {
				return element;
			}
		}
		throw new IllegalArgumentException(String.format("[%s]中根据常量值[%s]找不到对应的枚举常量",
				DateType.class.getName(), value));
	}
}
