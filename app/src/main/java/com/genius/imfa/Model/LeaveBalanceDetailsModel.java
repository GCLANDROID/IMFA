package com.genius.imfa.Model;

public class LeaveBalanceDetailsModel {
    String leave,leaveBalance,leaveTaken,leaveTypeName;

    public LeaveBalanceDetailsModel(String leave, String leaveBalance, String leaveTaken) {
        this.leave = leave;
        this.leaveBalance = leaveBalance;
        this.leaveTaken = leaveTaken;
    }

    public LeaveBalanceDetailsModel(String leave, String leaveBalance, String leaveTaken, String leaveTypeName) {
        this.leave = leave;
        this.leaveBalance = leaveBalance;
        this.leaveTaken = leaveTaken;
        this.leaveTypeName = leaveTypeName;
    }

    public String getLeave() {
        return leave;
    }

    public void setLeave(String leave) {
        this.leave = leave;
    }

    public String getLeaveBalance() {
        return leaveBalance;
    }

    public void setLeaveBalance(String leaveBalance) {
        this.leaveBalance = leaveBalance;
    }

    public String getLeaveTaken() {
        return leaveTaken;
    }

    public void setLeaveTaken(String leaveTaken) {
        this.leaveTaken = leaveTaken;
    }

    public String getLeaveTypeName() {
        return leaveTypeName;
    }

    public void setLeaveTypeName(String leaveTypeName) {
        this.leaveTypeName = leaveTypeName;
    }
}
