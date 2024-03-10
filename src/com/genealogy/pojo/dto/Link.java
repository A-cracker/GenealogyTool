package com.genealogy.pojo.dto;

/**
 * 
 * @author zzb
 *
 */
public class Link {
	public static final Integer FATHERORMOTHER = 1;
	public static final Integer SONORDAUGHTER = 2;
	public static final Integer SPOUSE = 3;
	public static final Integer NONE = 4;
	public static final Integer SELF = 5;
	private Long sourceId;
	private String source;
	private String sourceClass;
	private Long targetId;
	private String target;
	private String targetClass;
	private Integer relation;
	public Link() {
		
	}
	//是有向Link
	public Link(Long sourceId,String source,String sourceClass,Long targetId,String target,String targetClass,Integer relation) {
		this.sourceId = sourceId;
		this.source = source;
		this.sourceClass = sourceClass;
		this.targetId = targetId;
		this.target = target;
		this.targetClass = targetClass;
		this.relation = relation;
	}
	public Long getSourceId() {
		return sourceId;
	}
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
	public Long getTargetId() {
		return targetId;
	}
	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public Integer getRelation() {
		return relation;
	}
	public void setRelation(Integer relation) {
		this.relation = relation;
	}
	public String getSourceClass() {
		return sourceClass;
	}
	public void setSourceClass(String sourceClass) {
		this.sourceClass = sourceClass;
	}
	public String getTargetClass() {
		return targetClass;
	}
	public void setTargetClass(String targetClass) {
		this.targetClass = targetClass;
	}
}
