package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
public class AbsenceApplyData {
    final public static String TBL_NAME     = "absence_apply";
    final static String COL_ID              = "id";
    final static String COL_STUDENT_ID      = "student_id";
    final static String COL_TO_TEACHER_ID   = "to_teacher_id";
    final static String COL_TYPE            = "type";
    final static String COL_BEGIN           = "begin";
    final static String COL_END             = "end";
    final static String COL_CAUSE           = "cause";
    final static String COL_APPROVAL        = "approval";

    final static String COL_AS_CODE         = "code";
    final static String COL_AS_NAME         = "name";
    final static String COL_AS_TEACHER      = "teacher";

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

    AbsenceApplyData() {
        ccList = new ArrayList<>();
    }

    static String toDomain(String col) {
        return TBL_NAME + "." + col;
    }

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id          = resultSet.getInt(COL_ID);
        studentId   = resultSet.getInt(COL_STUDENT_ID);
        toTeacherId = resultSet.getInt(COL_TO_TEACHER_ID);
        type        = resultSet.getString(COL_TYPE);
        cause       = resultSet.getString(COL_CAUSE);
        begin       = resultSet.getDate(COL_BEGIN);
        ending      = resultSet.getDate(COL_END);
    }

    public void extractAsFromResultSet(ResultSet resultSet) throws SQLException {
        studentCode = resultSet.getInt(StudentData.getAsCol(StudentData.COL_CODE));
        studentName = resultSet.getString(StudentData.getAsCol(StudentData.COL_NAME));
        toTeacherName = resultSet.getString(TeacherData.getAsCol(TeacherData.COL_NAME));
    }
}
