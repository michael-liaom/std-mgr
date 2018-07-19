package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseData {
    final static String TBL_NAME    = "course";
    final static String COL_ID      = "id";
    final static String COL_NAME    = "name";
    final static String COL_CODE    = "code";
    final static String COL_TEACHER_ID  = "teacher_id";

    final static String COL_AS_TEACHER_NAME = "teacher_name";

    int id;
    String name;
    int code;
    int teacherId;
    //joined data
    String teacherName;
    //Seperated
    TeacherData teacherData;

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id          = resultSet.getInt(COL_ID);
        name        = resultSet.getString(COL_NAME);
        code        = resultSet.getInt(COL_CODE);
        teacherId   = resultSet.getInt(COL_TEACHER_ID);
    }

    public void extractAsFromResultSet(ResultSet resultSet) throws SQLException {
        teacherName = resultSet.getString(COL_AS_TEACHER_NAME);
    }

}
