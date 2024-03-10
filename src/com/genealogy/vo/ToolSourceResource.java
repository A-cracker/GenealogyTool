package com.genealogy.vo;


import com.genealogy.constant.ToolUsingStatus;

public class ToolSourceResource {
	private ToolUsingStatus usingStatus;
	private String name;
	private Long toolSourceID;

	public ToolUsingStatus getUsingStatus() {
		return usingStatus;
	}

	public void setUsingStatus(ToolUsingStatus usingStatus) {
		this.usingStatus = usingStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getToolSourceID() {
		return toolSourceID;
	}

	public void setToolSourceID(Long toolSourceID) {
		this.toolSourceID = toolSourceID;
	}
}