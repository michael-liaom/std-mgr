package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentData {
    final public static String TBL_NAME = "student_registratioin";
    final static String COL_ID          = "id";
    final static String COL_NAME        = "name";
    final static String COL_CLASS_ID    = "class_id";
    final static String COL_CODE        = "code";
    final static String COL_REG_CODE    = "reg_code";
    int id;
    String name;
    int class_id;
    int code;

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id = resultSet.getInt(COL_ID);
        name = resultSet.getString(COL_NAME);
        class_id = resultSet.getInt(COL_CLASS_ID);
        code = resultSet.getInt(COL_CODE);
    }
}
