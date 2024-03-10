package com.genealogy.pojo.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "pedigree")
public class Pedigree {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "doisser")
    private String dossier;//卷宗
    @Column(name = "faction")
    private String faction;//派系
    @Column(name = "branch")
    private String branch;//房支
    @Column(name = "name")
    private String name;
    @Column(name = "originPlace")
    private String originPlace;//普籍地
    @Column(name = "description")
    private String description;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "creator_id")
    private Long creatorId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDossier() {
        return dossier;
    }

    public void setDossier(String dossier) {
        this.dossier = dossier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getOriginPlace() {
        return originPlace;
    }

    public void setOriginPlace(String originPlace) {
        this.originPlace = originPlace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "{\"id\":" + id + ", \"name\":\"" + name + "\", \"dossier\":\"" + dossier + "\", \"faction\":\"" + faction + "\", \"branch\":\"" + branch + "\", \"originPlace\":\"" + originPlace + "\",\"description\":\"" + description + "\"}";
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
}
