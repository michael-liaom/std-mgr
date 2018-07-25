package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by weijie on 2018/5/9.
 */
public class ClassData {
    final static String TBL_NAME    = "class";
    final static String COL_ID      = "id";
    final static String COL_NAME    = "name";
    final static String COL_CODE    = "code";
    final static String COL_SECTION = "section";
    final static String COL_MAJOR   = "major";
    final static String COL_GRADE   = "grade";
    final static String COL_TEACHER_ID  = "teacher_id";

    int id;
    String name;
    int code;
    String section,
        major;
    int grade;
    int teacherId;
    //Joint data
    String teacherName;
    //Seperated data
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

    static String getDomainColums() {
        return toDomain(COL_ID)
                + ","
                + toDomain(COL_NAME)
                + ","
                + toDomain(COL_CODE)
                + ","
                + toDomain(COL_SECTION)
                + ","
                + toDomain(COL_MAJOR)
                + ","
                + toDomain(COL_GRADE)
                + ","
                + toDomain(COL_TEACHER_ID);
    }

    static String getColums() {
        return COL_ID
                + ","
                + COL_NAME
                + ","
                + COL_CODE
                + ","
                + COL_SECTION
                + ","
                + COL_MAJOR
                + ","
                + COL_GRADE
                + ","
                + COL_TEACHER_ID;
    }

    static String getJointDomainColums() {
        return TeacherData.toDomainAs(TeacherData.COL_NAME);
    }

    static String getJointTables() {
        return TeacherData.TBL_NAME;
    }

    static String getJointCondition() {
        return toDomain(COL_TEACHER_ID) + "=" + TeacherData.toDomain(COL_ID);
    }

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id      = resultSet.getInt(COL_ID);
        name    = resultSet.getString(COL_NAME);
        code    = resultSet.getInt(COL_CODE);
        section = resultSet.getString(COL_SECTION);
        major   = resultSet.getString(COL_MAJOR);
        grade   = resultSet.getInt(COL_GRADE);
        teacherId = resultSet.getInt(COL_TEACHER_ID);
    }

    public void extractJointFromResultSet(ResultSet resultSet) throws SQLException {
        teacherName = resultSet.getString(TeacherData.getAsCol(TeacherData.COL_NAME));
    }
}
