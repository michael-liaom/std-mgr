package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClassData {
    final static String TBL_NAME    = "class";
    final static String COL_ID      = "id";
    final static String COL_NAME    = "name";
    final static String COL_CODE    = "code";

    int id;
    String name;
    int code;
    //Seperated data
    ArrayList<TeacherData> materList;

    ClassData() {
        materList = new ArrayList<>();
    }

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
                + toDomain(COL_CODE);
    }

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id = resultSet.getInt(COL_ID);
        name = resultSet.getString(COL_NAME);
        code = resultSet.getInt(COL_CODE);
    }
}
