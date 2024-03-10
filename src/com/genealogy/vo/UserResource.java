package com.genealogy.vo;

public class UserResource{
	private Long userID;
	private String userAccount;
	private String userName;
	private Long representPositionID;
	
	public Long getUserID() {
		return userID;
	}
	public void setUserID(Long userID) {
		this.userID = userID;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Long getRepresentPositionID() {
		return representPositionID;
	}
	public void setRepresentPositionID(Long representPositionID) {
		this.representPositionID = representPositionID;
	}
}