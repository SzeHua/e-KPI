package com.example.user.e_kpiv3;

/**
 * Created by USER on 11/23/2016.
 */

public class SpinnerObjCat {
    int categoryID;
    String categoryName;
    int kpiID;
    String kpiName;
    int measuresID;
    String measuresName;

    /*public SpinnerObjCat(int categoryID, String categoryName) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
    }*/

    public int getCategoryID(){
        return categoryID;
    }

    public void setCategoryID(int categoryID){
        this.categoryID = categoryID;
    }

    public String getCategoryName(){
        return categoryName;
    }

    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    public int getKpiID() {
        return kpiID;
    }

    public void setKpiID(int kpiID) {
        this.kpiID = kpiID;
    }

    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

    public int getMeasuresID() {
        return measuresID;
    }

    public void setMeasuresID(int measuresID) {
        this.measuresID = measuresID;
    }

    public String getMeasuresName() {
        return measuresName;
    }

    public void setMeasuresName(String measuresName) {
        this.measuresName = measuresName;
    }
}


