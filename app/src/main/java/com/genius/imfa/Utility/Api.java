package com.genius.imfa.Utility;

public class Api {
    public static String BASE_URL = "https://cloud.geniusconsultant.com/GHRMSApi_v2/api/";
    public static String BASE_URL_SUBHAM_DA = "http://171.16.2.67/GHRMSApi_V2_DevMode/api/";
    public static String SECURITY_CODE = "1185";
    public static String sLogin = BASE_URL+"Login/LoginUser";
    public static String sProfilePic=BASE_URL+"Profile/ProfilePic";
    public static String sProfileApi=BASE_URL+"Profile/GetProfileDetails";

    public static String sGetMenuOnOff= BASE_URL+"MenuAccess/GetMenuOnOff";

    public static String sCommonDDL= BASE_URL+"General/GetCommonDDL";
    public static String sPayslipApi=BASE_URL+"Payroll/GetPaySlip";
    public static String sChangePasswordApi=BASE_URL+"Login/ChangePassword";
    public static String sApproverCheckApi=BASE_URL+"Attendance/LeaveApplicationApprover";
    public static String sLeaveDetails=BASE_URL+"Leave/LeaveApplicationDetails";

    public static String sLeaveModeApi= BASE_URL+"Leave/GetLeaveMode";

    public static String sLeaveStartCheckApi= BASE_URL+"Leave/CheckLeaveStartDayStatus";

    public static String sGetCompOffBreakUp = BASE_URL+"Leave/GetCompOffBreakUp";

    public static String sCheckLeaveViewSummary = BASE_URL+"Leave/CheckLeaveViewSummary";

    public static String sBindViewSummary = BASE_URL+"Leave/BindViewSummary";
    //public static String sBindViewSummary = "http://171.16.2.67/GHRMSApi_V2_DevMode/api/Leave/BindViewSummary";

    public static String sLeaveDayDetailsapi = BASE_URL+"Leave/DayDetails";
    public static String sLeaveAdd=BASE_URL+"Leave/LeaveAdd";
    //public static String sLeaveAdd=BASE_URL+"http://171.16.2.67/GHRMSApi_V2_DevMode/api/Leave/LeaveAdd";

    public static String sApproverLeaveItemapi=BASE_URL+"Leave/ApproverLeaveApp";

    public static String sLeaveApprovedapi=BASE_URL+"Leave/ApprovedApplication";

    public static String sLeaveDeleteByApproverapi=BASE_URL+"Leave/DeleteLeaveApplicationByApprover";
    public static String sLeaveReportapi=BASE_URL+"Leave/LeaveApplicationDeatilsForApplicant";

    public static String sDeleteLeaveApplication= BASE_URL+"Leave/DeleteLeaveApplication";

    //public static String sLeaveDetails=BASE_URL+"Leave/LeaveApplicationDetails";

    public static String sCalendarapi=BASE_URL+"Attendance/GetEmployeeAttendanceReport";

    public static String sForgotPasswordapi=BASE_URL+"Profile/GClForgotPassword";

    public static String sHolidayapi=BASE_URL+"Holiday/GCLHolidayList_New";

    public static String sVersionCheckApi=BASE_URL+"VersionCheck/ApkVersionAndAutoUpdateStatus";


    public static String sGetAdjustmentApplicationAllDetails = BASE_URL+"IMFALeave/GetAdjustmentApplicationAllDetails";
    public static String sSaveODApplicationDetails = BASE_URL+"IMFALeave/SaveODApplicationDetails";
    //public static String sSaveODApplicationDetails = "http://171.16.2.67/GHRMSApi_V2_DevMode/api/IMFALeave/SaveODApplicationDetails";
    public static String sGetAdjustmentDetailsForGrid = BASE_URL+"IMFALeave/GetAdjustmentDetailsforGrid";
    public static String sDeleteAdjutmentApplication = BASE_URL+"IMFALeave/DeleteAdjutmentApplication";
    public static String sGetAdjutmentApplicationForApprover = BASE_URL+"IMFALeave/GetAdjutmentApplicationForApprover";

    public static String sSaveAdjustmentApprovalRejected = BASE_URL+"IMFALeave/SaveAdjustmentApprovalRejected";
    public static String sGetBulletinBoardDetails = BASE_URL+"IMFALeave/GetBulletinBoardDetails";
    public static String sGetLeaveApplicationAllDetailsEncash = BASE_URL+"IMFALeave/GetLeaveApplicationAllDetailsEncash";
    public static String sSaveEncashmentDetails = BASE_URL+"IMFALeave/SaveEncashmentDetails";
    //public static String sGetAdjustmentDetailsForGrid = "http://171.16.2.67/GHRMSApi_v2/api/IMFALeave/GetAdjustmentDetailsforGrid";

    public static String sGetwfhApplicationDetailsDetails = BASE_URL_SUBHAM_DA+"IMFALeave/GetwfhApplicationDetails";
    public static String sSavewfhDetails = BASE_URL_SUBHAM_DA+"IMFALeave/SavewfhDetails";
}
