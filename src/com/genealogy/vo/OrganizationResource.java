package com.genealogy.vo;

public class OrganizationResource {
	private Long organizationID;
	private Long robotID;
	private String organizationName;

	public Long getOrganizationID() {
		return organizationID;
	}

	public void setOrganizationID(Long organizationID) {
		this.organizationID = organizationID;
	}

	public Long getRobotID() {
		return robotID;
	}

	public void setRobotID(Long robotID) {
		this.robotID = robotID;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

}