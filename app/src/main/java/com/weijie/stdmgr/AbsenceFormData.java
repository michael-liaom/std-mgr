package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
public class AbsenceFormData {
    final public static int PENDING     = 0;
    final public static int APPROVAL    = 1;
    final public static int REJECT      = 2;
    final public static String TBL_NAME     = "absence_apply";
    final static String COL_ID              = "id";
    final static String COL_STUDENT_ID      = "student_id";
    final static String COL_TO_TEACHER_ID   = "to_teacher_id";
    final static String COL_TYPE            = "type";
    final static String COL_BEGIN           = "begin";
    final static String COL_END             = "end";
    final static String COL_CAUSE           = "cause";
    final static String COL_APPROVAL        = "approval";


    int     id;
    int     studentId;
    int     toTeacherId;
    String  type;
    String  cause;
    Date    begin,
            ending;
    int     approval;
    //Jointed
    int  studentCode;
    String  studentName;
    String  toTeacherName;
    //Sperated
    ArrayList <CourseData> ccList;

    AbsenceFormData() {
        ccList = new ArrayList<>();
    }

    static String toDomain(String col) {
        return TBL_NAME + "." + col;
    }

    static String getColums() {
        return COL_ID
                + ","
                + COL_STUDENT_ID
                + ","
                + COL_TO_TEACHER_ID
                + ","
                + COL_BEGIN
                + ","
                + COL_END
                + ","
                + COL_TYPE
                + ","
                + COL_CAUSE
                + ","
                + COL_APPROVAL;
    }


    static String getDomainColums() {
        return toDomain(COL_ID)
                + ","
                + toDomain(COL_STUDENT_ID)
                + ","
                + toDomain(COL_TO_TEACHER_ID)
                + ","
                + toDomain(COL_BEGIN)
                + ","
                + toDomain(COL_END)
                + ","
                + toDomain(COL_TYPE)
                + ","
                + toDomain(COL_CAUSE)
                + ","
                + toDomain(COL_APPROVAL);
    }

    static String getJointDomainColums() {
        return  StudentData.toDomainAs(StudentData.COL_CODE)
                + ","
                + StudentData.toDomainAs(StudentData.COL_NAME)
                + ","
                + TeacherData.toDomainAs(TeacherData.COL_NAME);
    }

    static String getJointTables() {
        return StudentData.TBL_NAME + ","
                + TeacherData.TBL_NAME;
    }

    static String getJointCondition() {
        return  toDomain(COL_STUDENT_ID) + "=" + StudentData.toDomain(StudentData.COL_ID)
                + " AND "
                + toDomain(COL_TO_TEACHER_ID) + "=" + TeacherData.toDomain(TeacherData.COL_ID);
    }

    String getApprovalStatus() {
        if (approval == AbsenceFormData.APPROVAL) {
            return "批准";
        }
        else if (approval == AbsenceFormData.REJECT) {
            return "拒绝";
        }
        else {
            return "待批";
        }
    }

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id          = resultSet.getInt(COL_ID);
        studentId   = resultSet.getInt(COL_STUDENT_ID);
        toTeacherId = resultSet.getInt(COL_TO_TEACHER_ID);
        type        = resultSet.getString(COL_TYPE);
        cause       = resultSet.getString(COL_CAUSE);
        begin       = CommUtils.toTimestamp(resultSet.getString(COL_BEGIN));
        ending      = CommUtils.toTimestamp(resultSet.getString(COL_END));
        approval    = resultSet.getInt(COL_APPROVAL);
    }

    public void extractJointFromResultSet(ResultSet resultSet) throws SQLException {
        studentCode     = resultSet.getInt(StudentData.getAsCol(StudentData.COL_CODE));
        studentName     = resultSet.getString(StudentData.getAsCol(StudentData.COL_NAME));
        toTeacherName   = resultSet.getString(TeacherData.getAsCol(TeacherData.COL_NAME));
    }
}