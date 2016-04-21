package com.hz.greendao.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "CONDUCTOR_WIRE_ENTITY".
 */
public class ConductorWireEntity implements java.io.Serializable {

    private long id;
    private String materialNum;
    private String materialDetail;
    private String materialUnit;
    private String materialNameEn;
    private String materialType;
    private String scbz;
    private java.util.Date cjsj;
    private String materialWeight;
    private String materialDrawing;
    private String materialTechnical;
    private java.util.Date updateDate;
    private Integer areaType;
    private Integer areaId;
    private Integer voltageType;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public ConductorWireEntity() {
    }

    public ConductorWireEntity(long id) {
        this.id = id;
    }

    public ConductorWireEntity(long id, String materialNum, String materialDetail, String materialUnit, String materialNameEn, String materialType, String scbz, java.util.Date cjsj, String materialWeight, String materialDrawing, String materialTechnical, java.util.Date updateDate, Integer areaType, Integer areaId, Integer voltageType) {
        this.id = id;
        this.materialNum = materialNum;
        this.materialDetail = materialDetail;
        this.materialUnit = materialUnit;
        this.materialNameEn = materialNameEn;
        this.materialType = materialType;
        this.scbz = scbz;
        this.cjsj = cjsj;
        this.materialWeight = materialWeight;
        this.materialDrawing = materialDrawing;
        this.materialTechnical = materialTechnical;
        this.updateDate = updateDate;
        this.areaType = areaType;
        this.areaId = areaId;
        this.voltageType = voltageType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMaterialNum() {
        return materialNum;
    }

    public void setMaterialNum(String materialNum) {
        this.materialNum = materialNum;
    }

    public String getMaterialDetail() {
        return materialDetail;
    }

    public void setMaterialDetail(String materialDetail) {
        this.materialDetail = materialDetail;
    }

    public String getMaterialUnit() {
        return materialUnit;
    }

    public void setMaterialUnit(String materialUnit) {
        this.materialUnit = materialUnit;
    }

    public String getMaterialNameEn() {
        return materialNameEn;
    }

    public void setMaterialNameEn(String materialNameEn) {
        this.materialNameEn = materialNameEn;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
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

    public String getMaterialWeight() {
        return materialWeight;
    }

    public void setMaterialWeight(String materialWeight) {
        this.materialWeight = materialWeight;
    }

    public String getMaterialDrawing() {
        return materialDrawing;
    }

    public void setMaterialDrawing(String materialDrawing) {
        this.materialDrawing = materialDrawing;
    }

    public String getMaterialTechnical() {
        return materialTechnical;
    }

    public void setMaterialTechnical(String materialTechnical) {
        this.materialTechnical = materialTechnical;
    }

    public java.util.Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(java.util.Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getAreaType() {
        return areaType;
    }

    public void setAreaType(Integer areaType) {
        this.areaType = areaType;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getVoltageType() {
        return voltageType;
    }

    public void setVoltageType(Integer voltageType) {
        this.voltageType = voltageType;
    }

    // KEEP METHODS - put your custom methods here

    @Override
    public String toString() {
        return "ConductorWireEntity{" +
                "id=" + id +
                ", materialNum='" + materialNum + '\'' +
                ", materialDetail='" + materialDetail + '\'' +
                ", materialUnit='" + materialUnit + '\'' +
                ", materialNameEn='" + materialNameEn + '\'' +
                ", materialType='" + materialType + '\'' +
                ", scbz='" + scbz + '\'' +
                ", cjsj=" + cjsj +
                ", materialWeight='" + materialWeight + '\'' +
                ", materialDrawing='" + materialDrawing + '\'' +
                ", materialTechnical='" + materialTechnical + '\'' +
                ", updateDate=" + updateDate +
                ", areaType=" + areaType +
                ", areaId=" + areaId +
                ", voltageType=" + voltageType +
                '}';
    }
    // KEEP METHODS END

}
