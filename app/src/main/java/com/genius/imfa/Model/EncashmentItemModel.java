package com.genius.imfa.Model;

public class EncashmentItemModel {
    String code,opening,leaveAvailed,avaliable,leaveTypeID,leaveTypeName,appCLS,appValue, iD;

    public EncashmentItemModel(String code, String opening, String leaveAvailed, String avaliable, String leaveTypeID, String leaveTypeName, String appCLS, String appValue, String iD) {
        this.code = code;
        this.opening = opening;
        this.leaveAvailed = leaveAvailed;
        this.avaliable = avaliable;
        this.leaveTypeID = leaveTypeID;
        this.leaveTypeName = leaveTypeName;
        this.appCLS = appCLS;
        this.appValue = appValue;
        this.iD = iD;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOpening() {
        return opening;
    }

    public void setOpening(String opening) {
        this.opening = opening;
    }

    public String getLeaveAvailed() {
        return leaveAvailed;
    }

    public void setLeaveAvailed(String leaveAvailed) {
        this.leaveAvailed = leaveAvailed;
    }

    public String getAvaliable() {
        return avaliable;
    }

    public void setAvaliable(String avaliable) {
        this.avaliable = avaliable;
    }

    public String getLeaveTypeID() {
        return leaveTypeID;
    }

    public void setLeaveTypeID(String leaveTypeID) {
        this.leaveTypeID = leaveTypeID;
    }

    public String getLeaveTypeName() {
        return leaveTypeName;
    }

    public void setLeaveTypeName(String leaveTypeName) {
        this.leaveTypeName = leaveTypeName;
    }

    public String getAppCLS() {
        return appCLS;
    }

    public void setAppCLS(String appCLS) {
        this.appCLS = appCLS;
    }

    public String getAppValue() {
        return appValue;
    }

    public void setAppValue(String appValue) {
        this.appValue = appValue;
    }

    public String getiD() {
        return iD;
    }

    public void setiD(String iD) {
        this.iD = iD;
    }
}
