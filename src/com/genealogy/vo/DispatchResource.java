package com.genealogy.vo;


public class DispatchResource {
	private Long dispatchID;

	/**
	 * 发布名称
	 * @author linjie
	 * @since 4.3.0
	 */
	private String dispatchName;

	/**
	 * 发布的创建时间
	 * @author linjie
	 * @since 4.3.0
	 */
	private String createDate;

	/**
	 * 发布对应的授权的描述
	 * @author linjie
	 * @since 4.3.0
	 */
	private String description; //authorization表

	private String status;

	private UserModel creator;

	public Long getDispatchID() {
		return dispatchID;
	}

	public void setDispatchID(Long dispatchID) {
		this.dispatchID = dispatchID;
	}

	public String getDispatchName() {
		return dispatchName;
	}

	public void setDispatchName(String dispatchName) {
		this.dispatchName = dispatchName;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UserModel getCreator() {
		return creator;
	}

	public void setCreator(UserModel creator) {
		this.creator = creator;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}

class UserModel {
	private String userName;

	private Long userID;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}
}