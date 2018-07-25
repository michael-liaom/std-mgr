package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by weijie on 2018/5/15.
 */
public class TeacherData {
    final static String TBL_NAME        = "teacher_registration";
    final static String COL_ID          = "id";
    final static String COL_NAME        = "name";
    final static String COL_CODE        = "code";
    final static String COL_SECTION     = "section";
    final static String COL_ROOM        = "room";
    final static String COL_EMAIL       = "email";
    final static String COL_REG_CODE    = "reg_code";
    final static String ALIAS_CLASS       = "class_teacher";
    final static String TBL_ALIAS_CLASS = "teacher_registration AS " + ALIAS_CLASS;
    final static String ALIAS_COURSE    = "course_teacher";
    final static String TBL_ALIAS_COURSE= "teacher_registration AS " + ALIAS_COURSE;

    int id;
    String name;
    int code;
    String section;
    String room;
    String email;

    static String toDomain(String col) {
        return TBL_NAME + "." + col;
    }

    static String toDomainAs(String col) {
        return TBL_NAME + "." + col + " AS " + TBL_NAME + "_" + col;
    }

    static String getAsCol(String col) {
        return TBL_NAME + "_" + col;
    }

    static String toDomainClass(String col) {
        return ALIAS_CLASS + "." + col;
    }

    static String toDomainAsClass(String col) {
        return ALIAS_CLASS + "." + col + " AS " + ALIAS_CLASS + "_" + col;
    }

    static String getAsClassCol(String col) {
        return ALIAS_CLASS + "_" + col;
    }

    static String toDomainCourse(String col) {
        return ALIAS_COURSE + "." + col;
    }

    static String toDomainAsCourse(String col) {
        return ALIAS_COURSE + "." + col + " AS " + ALIAS_COURSE + "_" + col;
    }

    static String getAsCourseCol(String col) {
        return ALIAS_COURSE + "_" + col;
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
                + toDomain(COL_EMAIL)
                + ","
                + toDomain(COL_CODE);
    }

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id      = resultSet.getInt(COL_ID);
        name    = resultSet.getString(COL_NAME);
        code    = resultSet.getInt(COL_CODE);
        section = resultSet.getString(COL_SECTION);
        room    = resultSet.getString(COL_ROOM);
        email   = resultSet.getString(COL_EMAIL);
    }
}
