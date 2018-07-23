package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TeacherData {
    final static String TBL_NAME        = "teacher_registration";
    final static String COL_ID          = "id";
    final static String COL_NAME        = "name";
    final static String COL_CODE        = "code";
    final static String COL_SECTION     = "section";
    final static String COL_ROOM        = "room";
    final static String COL_REG_CODE    = "reg_code";
    int id;
    String name;
    int code;
    int section;
    int room;

    static String toDomain(String col) {
        return TBL_NAME + "." + col;
    }

    static String toDomainAs(String col) {
        return TBL_NAME + "." + col + " AS " + TBL_NAME + "_" + col;
    }

    static String getAsCol(String col) {
        return TBL_NAME + "_" + col;
    }

    static String getDomainColums() {
        return toDomain(COL_ID)
                + ","
                + toDomain(COL_NAME)
                + ","
                + toDomain(COL_SECTION)
                + ","
                + toDomain(COL_ROOM)
                + ","
                + toDomain(COL_CODE);
    }

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id      = resultSet.getInt(COL_ID);
        name    = resultSet.getString(COL_NAME);
        code    = resultSet.getInt(COL_CODE);
        section = resultSet.getInt(COL_SECTION);
        room    = resultSet.getInt(COL_ROOM);
    }
}
