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
        id = resultSet.getInt(COL_ID);
        name = resultSet.getString(COL_NAME);
        code = resultSet.getInt(COL_CODE);
    }
}
