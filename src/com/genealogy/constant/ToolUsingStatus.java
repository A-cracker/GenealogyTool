package com.genealogy.constant;


/**
 * 工具常量
 * 
 * @author linjie
 * @since 4.3.0
 */
public enum ToolUsingStatus {

	/**
	 * 正在使用
	 *
	 * @author linjie
	 * @since 4.3.0
	 */
	IN_USING(0),

	/**
	 * 停止使用
	 *
	 * @author linjie
	 * @since 4.3.0
	 */
	STOP_USING(1);

	private Integer value;

	private ToolUsingStatus(Integer value){
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
	public static ToolUsingStatus valueOf(Integer value) {
		for (ToolUsingStatus element : ToolUsingStatus.values()) {
			if (element.value.equals(value)) {
				return element;
			}
		}
		throw new IllegalArgumentException(String.format("[%s]中根据常量值[%s]找不到对应的枚举常量",
				ToolUsingStatus.class.getName(), value));
	}
}
