package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseData {
    final static String TBL_NAME    = "course";
    final static String COL_ID      = "id";
    final static String COL_NAME    = "name";
    final static String COL_CODE    = "code";
    final static String COL_TEACHER_ID  = "teacher_id";
    final static String COL_TERM    = "term";

    int id;
    String name;
    int code;
    int teacherId;
    int term;
    //joined data
    String teacherName;
    //Seperated
    TeacherData teacherData;

    static String toDomain(String col) {
        return TBL_NAME + "." + col;
    }

    static String toDomainAs(String col) {
        return TBL_NAME + "." + col + " AS " + TBL_NAME + "_" + col;
    }

    static String getAsCol(String col) {
        return TBL_NAME + "_" + col;
    }

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id          = resultSet.getInt(COL_ID);
        name        = resultSet.getString(COL_NAME);
        code        = resultSet.getInt(COL_CODE);
        teacherId   = resultSet.getInt(COL_TEACHER_ID);
        term        = resultSet.getInt(COL_TERM);
    }

    public void extractAsFromResultSet(ResultSet resultSet) throws SQLException {
        teacherName = resultSet.getString(TeacherData.getAsCol(TeacherData.COL_NAME));
    }

}
