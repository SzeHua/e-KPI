package com.example.user.e_kpiv3;

/**
 * Created by USER on 5/29/2016.
 */
public class Evidence {
    private String evidenceID, title, description, date, categoryname, kpiName, measureName;

    public Evidence() {
    }

    public Evidence(String evidenceID, String title, String description, String date, String categoryname, String kpiName, String measureName) {
        this.evidenceID = evidenceID;
        this.title = title;
        this.description = description;
        this.date = date;
        this.categoryname = categoryname;
        this.kpiName = kpiName;
        this.measureName = measureName;
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

    public String getCategoryname() { return this.categoryname; }

    public void setCategoryname(String categoryname) { this.categoryname = categoryname; }

    public String getKpiName() { return this.kpiName; }

    public void setKpiName(String kpiName) { this.kpiName = kpiName; }

    public String getMeasureName() { return this.measureName; }

    public void setMeasureName(String measureName) { this.measureName = measureName; }
}


