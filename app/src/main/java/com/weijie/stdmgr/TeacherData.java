package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TeacherData {
    final static String TBL_NAME        = "teacher_registration";
    final static String COL_ID          = "id";
    final static String COL_NAME        = "name";
    final static String COL_CODE        = "code";
    final static String COL_REG_CODE    = "reg_code";
    int id;
    String name;
    int code;
    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id = resultSet.getInt(COL_ID);
        name = resultSet.getString(COL_NAME);
        code = resultSet.getInt(COL_CODE);
    }
}