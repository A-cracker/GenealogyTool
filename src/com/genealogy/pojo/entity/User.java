package com.genealogy.pojo.entity;


import java.sql.Date;

public class User {
    private Long id;
    private String name;
    private Integer gender;
    private Date birthdate;
    private String birthplace;
    private String description;
    private byte[] img;
    private String generation;
    private String identityId;
    private String residence;
    private String phoneNumber;
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
    public String getBirthplace() {
        return birthplace;
    }
    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public byte[] getImg() {
        return img;
    }
    public void setImg(byte[] img) {
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
    public User() {
        super();
    }
    public User(Long id, String name, Integer gender, Date birthdate, String birthplace, String description, byte[] img, String generation, String identityId, String residence, String phoneNumber) {
        super();
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.birthdate = birthdate;
        this.birthplace = birthplace;
        this.description = description;
        this.img = img;
        this.generation = generation;
        this.identityId = identityId;
        this.residence = residence;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString(){
        return "{\"id\":"+id+", \"name\":\""+ name +"\", \"gender\":"+gender+",\"birthdate\":\""+birthdate+"\",  \"birthplace\":\""+ birthplace +"\",  \"description\":\""+ description +"\", \"identityId\":\""+ identityId +"\", \"residence\":\""+ residence +"\", \"phoneNumber\":\""+ phoneNumber +"\"}";
    }
}
