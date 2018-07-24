package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
public class AbsenceFormData extends DBHandlerService {
    final public static int PENDING     = 0;
    final public static int APPROVAL    = 1;
    final public static int REJECT      = 2;
    final public static String TBL_NAME = "absence_apply";
    final static String COL_ID          = "id";
    final static String COL_STUDENT_ID  = "student_id";
    final static String COL_BEGIN       = "begin";
    final static String COL_END         = "end";
    final static String COL_TYPE        = "type";
    final static String COL_CAUSE       = "cause";
    final static String COL_COURSE_ID   = "course_id";
    final static String COL_COURSE_COUNT= "course_count";
    final static String COL_CLASS_APPROVAL  = "class_approval";
    final static String COL_COURSE_APPROVAL = "course_approval";


    int     id;
    int     studentId;
    Date    begin,
            ending;
    String  type;
    String  cause;
    int     courseId;
    int     courseCount;
    int     classApproval;
    int     courseApproval;
    //Jointed
    int     studentCode;
    String  studentName;
    int     classTeacherId;
    int     courseTecherId;
    //seperated
    TeacherData classTeacher;
    StudentData studentData;
    CourseData courseData;

    static String toDomain(String col) {
        return TBL_NAME + "." + col;
    }

    static String getColums() {
        return COL_ID
                + ","
                + COL_STUDENT_ID
                + ","
                + COL_BEGIN
                + ","
                + COL_END
                + ","
                + COL_TYPE
                + ","
                + COL_CAUSE
                + ","
                + COL_COURSE_ID
                + ","
                + COL_COURSE_COUNT
                + ","
                + COL_CLASS_APPROVAL
                + ","
                + COL_COURSE_APPROVAL;
    }


    static String getDomainColums() {
        return toDomain(COL_ID)
                + ","
                + toDomain(COL_STUDENT_ID)
                + ","
                + toDomain(COL_BEGIN)
                + ","
                + toDomain(COL_END)
                + ","
                + toDomain(COL_TYPE)
                + ","
                + toDomain(COL_CAUSE)
                + ","
                + toDomain(COL_COURSE_ID)
                + ","
                + toDomain(COL_COURSE_COUNT)
                + ","
                + toDomain(COL_CLASS_APPROVAL)
                + ","
                + toDomain(COL_COURSE_APPROVAL);
    }

    static String getJointDomainColums() {
        return  StudentData.toDomainAs(StudentData.COL_CODE)
                + ","
                + StudentData.toDomainAs(StudentData.COL_NAME)
                + ","
                + TeacherData.toDomainAsClass(COL_ID)
                + ","
                + TeacherData.toDomainAsCourse(COL_ID);
    }

    static String getJointTables() {
        return StudentData.TBL_NAME
                + ","
                + ClassData.TBL_NAME
                + ","
                + TeacherData.TBL_ALIAS_CLASS
                + ","
                + CourseData.TBL_NAME
                + ","
                + TeacherData.TBL_ALIAS_COURSE;
    }

    static String getJointCondition() {
        return  toDomain(COL_STUDENT_ID)
                + "=" + StudentData.toDomain(COL_ID)
                + " AND "
                + StudentData.toDomain(StudentData.COL_CLASS_ID)
                + "=" + ClassData.toDomain(COL_ID)
                + " AND "
                + ClassData.toDomain(CourseData.COL_TEACHER_ID)
                + "=" + TeacherData.toDomainInClass(COL_ID)
                + " AND "
                + toDomain(COL_COURSE_ID)
                + "=" + CourseData.toDomain(COL_ID)
                + " AND "
                + CourseData.toDomain(CourseData.COL_TEACHER_ID)
                + "=" + TeacherData.toDomainInCourse(COL_ID);
    }

    String getClassApprovalStatus() {
        if (classApproval == AbsenceFormData.APPROVAL) {
            return "批准";
        }
        else if (classApproval == AbsenceFormData.REJECT) {
            return "拒绝";
        }
        else {
            return "待批";
        }
    }

    String getCourseApprovalStatus() {
        if (courseApproval == AbsenceFormData.APPROVAL) {
            return "批准";
        }
        else if (courseApproval == AbsenceFormData.REJECT) {
            return "拒绝";
        }
        else {
            return "待批";
        }
    }

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id          = resultSet.getInt(COL_ID);
        studentId   = resultSet.getInt(COL_STUDENT_ID);
        type        = resultSet.getString(COL_TYPE);
        cause       = resultSet.getString(COL_CAUSE);
        begin       = CommUtils.toTimestamp(resultSet.getString(COL_BEGIN));
        ending      = CommUtils.toTimestamp(resultSet.getString(COL_END));
        courseId  = resultSet.getInt(COL_COURSE_ID);
        courseCount = resultSet.getInt(COL_COURSE_COUNT);
        classApproval = resultSet.getInt(COL_CLASS_APPROVAL);
        courseApproval = resultSet.getInt(COL_COURSE_APPROVAL);
    }

    public void extractJointFromResultSet(ResultSet resultSet) throws SQLException {
        studentCode = resultSet.getInt(StudentData.getAsCol(StudentData.COL_CODE));
        studentName = resultSet.getString(StudentData.getAsCol(StudentData.COL_NAME));
        classTeacherId = resultSet.getInt(TeacherData.getAsClassCol(COL_ID));
        courseTecherId = resultSet.getInt(TeacherData.getAsCourseCol(COL_ID));
    }

    public String setColumsData() {
        return COL_STUDENT_ID   + "=" + toValue(studentId)
                + ","
                + COL_TYPE      + "=" + toValue(type)
                + ","
                + COL_CAUSE     + "=" + toValue(cause)
                + ","
                + COL_BEGIN     + "=" + toValue(begin)
                + ","
                + COL_END       + "=" + toValue(ending)
                + ","
                + COL_COURSE_ID + "=" + toValue(courseId)
                + ","
                + COL_COURSE_COUNT + "=" + toValue(courseCount)
                + ","
                + COL_CLASS_APPROVAL + "=" + toValue(classApproval)
                + ","
                + COL_COURSE_APPROVAL + "=" + toValue(courseApproval);
    }
}
