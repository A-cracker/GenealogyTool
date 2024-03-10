package com.genealogy.vo;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public class Resources<T> {
	private List<T> rows;
	private int total;
	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}

	@JSONField(serialize = false)
	public boolean isEmpty() {
		return rows.isEmpty();
	}

	@JSONField(serialize = false)
	public T getFirst() {
		if (rows.isEmpty()) {
			throw new EmptyResultDataAccessException("从核心获取信息为空", 1);
		}
		
		return rows.get(0);
	}
	 
}



