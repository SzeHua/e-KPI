package com.example.user.e_kpiv3;

/**
 * Created by USER on 5/29/2016.
 */
public class Evidence {
    private String evidenceID, title, description, date, categoryName, kpiName, measureName, roleType;

    public Evidence() {
    }

    public Evidence(String evidenceID, String title, String description, String date, String categoryName, String kpiName, String measureName, String roleType) {
        this.evidenceID = evidenceID;
        this.title = title;
        this.description = description;
        this.date = date;
        this.categoryName = categoryName;
        this.kpiName = kpiName;
        this.measureName = measureName;
        this.roleType = roleType;
    }

    public String getEvidenceID() {
        return evidenceID;
    }

    public void setEvidenceID(String evidenceID) {
        this.title = evidenceID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() { return this.categoryName; }

    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getKpiName() { return this.kpiName; }

    public void setKpiName(String kpiName) { this.kpiName = kpiName; }

    public String getMeasureName() { return this.measureName; }

    public void setMeasureName(String measureName) { this.measureName = measureName; }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getRoleType() {
        return roleType;
    }
}


