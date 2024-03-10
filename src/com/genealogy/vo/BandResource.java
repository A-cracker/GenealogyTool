package com.genealogy.vo;

public class BandResource{
	private Long bandID;
	private Long organizationID;
	private Long objID;


	public Long getBandID() {
		return bandID;
	}

	public void setBandID(Long bandID) {
		this.bandID = bandID;
	}

	public Long getOrganizationID() {
		return organizationID;
	}

	public void setOrganizationID(Long organizationID) {
		this.organizationID = organizationID;
	}

	public Long getObjID() {
		return objID;
	}

	public void setObjID(Long objID) {
		this.objID = objID;
	}
}