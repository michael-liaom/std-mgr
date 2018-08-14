package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by weijie on 2018/5/17.
 */
public class RalApplyData extends DBHandlerService {
    final public static int PENDING     = 0;
    final public static int APPROVAL    = 1;
    final public static int REJECT      = 2;

    final public static String TBL_NAME = "ral_apply";
    final static String COL_ID          = "id";
    final static String COL_RAL_ID      = "ral_id";
    final static String COL_STUDENT_ID  = "student_id";
    final static String COL_CAUSE       = "cause";
    final static String COL_TEACHER_ID  = "teacher_id";
    final static String COL_APPROVAL    = "approval";
    final static String COL_APPROVAL_COMMENT = "approval_comment";


    int     id;
    int     ralId;
    int     studentId;
    String  cause;
    int     teacherId;
    int     approval;
    String  approvalComment;
    //Jointed
    String  ralName;
    int     ralAmount;
    String  ralTerm;
    String  studentCode;
    String  studentName;
    //seperated
    TeacherData classTeacher;
    StudentData studentData;
    RalData     ralData;

    public RalApplyData() {
        cause = "";
    }

    static String toDomain(String col) {
        return TBL_NAME + "." + col;
    }

    static String getColums() {
        return COL_ID
                + ","
                + COL_RAL_ID
                + ","
                + COL_STUDENT_ID
                + ","
                + COL_CAUSE
                + ","
                + COL_TEACHER_ID
                + ","
                + COL_APPROVAL
                + ","
                + COL_APPROVAL_COMMENT;
    }


    static String getDomainColums() {
        return toDomain(COL_ID)
                + ","
                + toDomain(COL_RAL_ID)
                + ","
                + toDomain(COL_STUDENT_ID)
                + ","
                + toDomain(COL_CAUSE)
                + ","
                + toDomain(COL_TEACHER_ID)
                + ","
                + toDomain(COL_APPROVAL)
                + ","
                + toDomain(COL_APPROVAL_COMMENT);
    }

    static String getJointDomainColums() {
        return  RalData.toDomainAs(RalData.COL_NAME)
                + ","
                + RalData.toDomainAs(RalData.COL_AMOUNT)
                + ","
                + RalData.toDomainAs(RalData.COL_TERM)
                + ","
                + StudentData.toDomainAs(StudentData.COL_CODE)
                + ","
                + StudentData.toDomainAs(StudentData.COL_NAME);
    }

    static String getJointTables() {
        return RalData.TBL_NAME
                + ","
                + StudentData.TBL_NAME;
    }

    static String getJointCondition() {
        return  toDomain(COL_RAL_ID)
                + "=" + RalData.toDomain(COL_ID)
                + " AND "
                + toDomain(COL_STUDENT_ID)
                + "=" + StudentData.toDomain(COL_ID);
    }

    String getApprovalStatus() {
        if (approval == RalApplyData.APPROVAL) {
            return "批准";
        }
        else if (approval == RalApplyData.REJECT) {
            return "拒绝";
        }
        else {
            return "待批";
        }
    }

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id          = resultSet.getInt(COL_ID);
        ralId       = resultSet.getInt(COL_RAL_ID);
        studentId   = resultSet.getInt(COL_STUDENT_ID);
        cause       = resultSet.getString(COL_CAUSE);
        teacherId   = resultSet.getInt(COL_TEACHER_ID);
        approval    = resultSet.getInt(COL_APPROVAL);
        approvalComment = resultSet.getString(COL_APPROVAL_COMMENT);
    }

    public void extractJointFromResultSet(ResultSet resultSet) throws SQLException {
        ralName     = resultSet.getString(RalData.getAsCol(RalData.COL_NAME));
        ralAmount   = resultSet.getInt(RalData.getAsCol(RalData.COL_AMOUNT));
        ralTerm     = resultSet.getString(RalData.getAsCol(RalData.COL_TERM));
        studentCode = resultSet.getString(StudentData.getAsCol(StudentData.COL_CODE));
        studentName = resultSet.getString(StudentData.getAsCol(StudentData.COL_NAME));
    }

    public String setColumsData() {
        return  COL_RAL_ID   + "=" + toValue(ralId)
                + ","
                + COL_STUDENT_ID   + "=" + toValue(studentId)
                + ","
                + COL_CAUSE     + "=" + toValue(cause)
                + ","
                + COL_TEACHER_ID + "=" + toValue(teacherId);
    }
}
