package com.genealogy.pojo.entity;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.Serializable;

/**
 * 
 * @author xjk&zzb
 *
 */
public class Relation{
	public static final Integer FATHER = 0;
	public static final Integer  MOTHER = 1;
	public static final Integer  HUSBAND = 2;
	private Integer id;
	private Long u1;
	private Long u2;

	/**
	 * 0: 父子
	 * 1: 母子
	 * 2. 配偶
	 */
	private Integer type;
	private Long treeId;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Long getU1() {
		return u1;
	}
	public void setU1(Long u1) {
		this.u1 = u1;
	}
	public Long getU2() {
		return u2;
	}
	public void setU2(Long u2) {
		this.u2 = u2;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getTreeId() {
		return treeId;
	}
	public void setTreeId(Long treeId) {
		this.treeId = treeId;
	}

	@Override
	public boolean equals(Object arg0) {
		Relation p = (Relation) arg0;
		return u1.equals(p.u1)&&u2.equals(p.u2)&&type.equals(p.type)&&treeId.equals(p.treeId);
	}

	@Override
	public int hashCode() {
		String str = u1.toString() + u2.toString() + type.toString() + treeId.toString();
		return str.hashCode();
	}

}
