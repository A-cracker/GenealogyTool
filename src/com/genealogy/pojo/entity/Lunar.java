package com.genealogy.pojo.entity;

public class Lunar {
    private Integer id;
    private String year;
    private String lunarYear;
    private String dynasty;
    private String reignTitle;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getLunarYear() {
        return lunarYear;
    }

    public void setLunarYear(String lunarYear) {
        this.lunarYear = lunarYear;
    }

    public String getDynasty() {
        return dynasty;
    }

    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }

    public String getReignTitle() {
        return reignTitle;
    }

    public void setReignTitle(String reignTitle) {
        this.reignTitle = reignTitle;
    }
}
