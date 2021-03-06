package com.hz.greendao.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "GEOLOGICAL_CONDITION_TYPE".
 */
public class GeologicalConditionType implements java.io.Serializable {

    private long id;
    private String soilType;
    private String scbz;
    private java.util.Date cjsj;
    private java.util.Date updateDate;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public GeologicalConditionType() {
    }

    public GeologicalConditionType(long id) {
        this.id = id;
    }

    public GeologicalConditionType(long id, String soilType, String scbz, java.util.Date cjsj, java.util.Date updateDate) {
        this.id = id;
        this.soilType = soilType;
        this.scbz = scbz;
        this.cjsj = cjsj;
        this.updateDate = updateDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public String getScbz() {
        return scbz;
    }

    public void setScbz(String scbz) {
        this.scbz = scbz;
    }

    public java.util.Date getCjsj() {
        return cjsj;
    }

    public void setCjsj(java.util.Date cjsj) {
        this.cjsj = cjsj;
    }

    public java.util.Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(java.util.Date updateDate) {
        this.updateDate = updateDate;
    }

    // KEEP METHODS - put your custom methods here

    @Override
    public String toString() {
        return "GeologicalConditionType{" +
                "id=" + id +
                ", soilType='" + soilType + '\'' +
                ", scbz='" + scbz + '\'' +
                ", cjsj=" + cjsj +
                ", updateDate=" + updateDate +
                '}';
    }
    // KEEP METHODS END

}
