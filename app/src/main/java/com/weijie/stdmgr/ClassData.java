package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClassData {
    final static String TBL_NAME    = "class";
    final static String COL_ID      = "id";
    final static String COL_NAME    = "name";
    final static String COL_CODE    = "code";
    final static String COL_MASTER_ID   = "master_teacher_id";
    final static String COL_ASSITANT_ID = "assitant_teacher_id";

    int id;
    String name;
    int code;
    int masterTeacherId;
    int assistantTeacherId;
    //joined data
    TeacherData masterTeacher, assistantTeacher;

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
        masterTeacherId     = resultSet.getInt(COL_MASTER_ID);
        assistantTeacherId  = resultSet.getInt(COL_ASSITANT_ID);
    }
}
