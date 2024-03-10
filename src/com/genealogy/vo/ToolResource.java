package com.genealogy.vo;

public class ToolResource {
	
	private Long objID;
	
	private Long realObjID;
	
	private Long organizationID;
	
	private Long mountBandID;
	private String name;

	public Long getObjID() {
		return objID;
	}

	public void setObjID(Long objID) {
		this.objID = objID;
	}

	public Long getOrganizationID() {
		return organizationID;
	}

	public void setOrganizationID(Long organizationID) {
		this.organizationID = organizationID;
	}

	public Long getRealObjID() {
		return realObjID;
	}

	public void setRealObjID(Long realObjID) {
		this.realObjID = realObjID;
	}

	public Long getMountBandID() {
		return mountBandID;
	}

	public void setMountBandID(Long mountBandID) {
		this.mountBandID = mountBandID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
