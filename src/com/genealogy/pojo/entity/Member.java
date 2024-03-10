package com.genealogy.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.genealogy.constant.DateType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 * 
 * @author xjk&zzb
 *
 */
public class Member{
	public static final int MAN = 1;
	public static final int WOMAN = 0;
	public static final int ALIVE = 1;
	public static final int NOTALIVE = 0;
	private Long id;
	private String name;
	private Integer gender;

	@JsonFormat(pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthdate;
	private Date deathDate;
	private String birthplace;
	private String restplace;
	private Integer is_alive;
	private String description;
	private String img;
	private String generation;
	private String identityId;
	private String residence;
	private String phoneNumber;
	public Integer isUser;

	private Integer openIdentity;

	private Integer openPhone;


	private Long albumId;

	private DateType birthDateType;

	private DateType deathDateType;

	private String lunarBirthDate;

	private String lunarDeathDate;

	private Long creatorId;

	private Date createTime;


	public Integer getIsUser() {
		return isUser;
	}
	public void setIsUser(Integer isUser) {
		this.isUser = isUser;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public Date getBrithdate() {
		return birthdate;
	}
	public void setBrithdate(Date birthdate) {
		this.birthdate = birthdate;
	}
	public Date getDeathDate() {
		return deathDate;
	}
	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}
	public String getBirthplace() {
		return birthplace;
	}
	public void setBirthplace(String birthplace) {
		this.birthplace = birthplace;
	}
	public Integer getIs_alive() {
		return is_alive;
	}
	public void setIs_alive(Integer is_alive) {
		this.is_alive = is_alive;
	}
	public String getRestplace() {
		return restplace;
	}
	public void setRestplace(String restplace) {
		this.restplace = restplace;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getGeneration() {
		return generation;
	}
	public void setGeneration(String generation) {
		this.generation = generation;
	}
	public String getIdentityId() {
		return identityId;
	}
	public void setIdentityId(String identityId) {
		this.identityId = identityId;
	}
	public String getResidence() {
		return residence;
	}
	public void setResidence(String residence) {
		this.residence = residence;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public Member() {
		super();
	}

	
	@Override
	public String toString(){
		return "{\"id\":"+id+", \"name\":\""+ name +"\", \"gender\":"+gender+",\"birthdate\":\""+birthdate+"\", \"deathDate\":\""+deathDate+"\", \"is_alive\":"+is_alive+", \"birthplace\":\""+ birthplace +"\", \"restplace\":\""+ restplace +"\", \"description\":\""+ description +"\", \"identityId\":\""+ identityId +"\", \"residence\":\""+ residence +"\", \"phoneNumber\":\""+ phoneNumber +"\" + \"isUser\":"+isUser+"}";
	}

	public Integer getOpenIdentity() {
		return openIdentity;
	}

	public void setOpenIdentity(Integer openIdentity) {
		this.openIdentity = openIdentity;
	}

	public Integer getOpenPhone() {
		return openPhone;
	}

	public void setOpenPhone(Integer openPhone) {
		this.openPhone = openPhone;
	}

	public Long getAlbumId() {
		return albumId;
	}

	public void setAlbumId(Long albumId) {
		this.albumId = albumId;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public DateType getBirthDateType() {
		return birthDateType;
	}

	public void setBirthDateType(DateType birthDateType) {
		this.birthDateType = birthDateType;
	}

	public DateType getDeathDateType() {
		return deathDateType;
	}

	public void setDeathDateType(DateType deathDateType) {
		this.deathDateType = deathDateType;
	}

	public String getLunarBirthDate() {
		return lunarBirthDate;
	}

	public void setLunarBirthDate(String lunarBirthDate) {
		this.lunarBirthDate = lunarBirthDate;
	}

	public String getLunarDeathDate() {
		return lunarDeathDate;
	}

	public void setLunarDeathDate(String lunarDeathDate) {
		this.lunarDeathDate = lunarDeathDate;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
