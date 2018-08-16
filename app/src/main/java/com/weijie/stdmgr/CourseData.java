package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CourseData {
    final static String TBL_NAME        = "course";
    final static String COL_ID          = "id";
    final static String COL_NAME        = "name";
    final static String COL_CODE        = "code";
    final static String COL_TERM        = "term";
    final static String COL_CREDIT      = "credit";
    final static String COL_GENRE       = "genre";
    final static String COL_SECTION     = "section";
    final static String COL_CLASSROOM   = "classroom";
    final static String COL_SCHEDURE    = "schedule";
    final static String COL_TEACHER_ID  = "teacher_id";

    final static String GENRE_REQIRED   = "必修";
    final static String GENRE_ELECTIVE  = "选修";

    int id;
    String name;
    String code;
    int term;
    int credit;
    String genre;
    String section;
    String classroom;
    String schedule;
    int teacherId;
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

    static String getDomainColums() {
        return toDomain(COL_ID)
                + ","
                + toDomain(COL_NAME)
                + ","
                + toDomain(COL_CODE)
                + ","
                + toDomain(COL_TERM)
                + ","
                + toDomain(COL_CREDIT)
                + ","
                + toDomain(COL_GENRE)
                + ","
                + toDomain(COL_SECTION)
                + ","
                + toDomain(COL_CLASSROOM)
                + ","
                + toDomain(COL_SCHEDURE)
                + ","
                + toDomain(COL_TEACHER_ID);
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

    static String getColums() {
        return COL_ID
                + ","
                + COL_NAME
                + ","
                + COL_CODE
                + ","
                + COL_TERM
                + ","
                + COL_CREDIT
                + ","
                + COL_GENRE
                + ","
                + COL_SECTION
                + ","
                + COL_CLASSROOM
                + ","
                + COL_SCHEDURE
                + ","
                + COL_TEACHER_ID;
    }

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id          = resultSet.getInt(COL_ID);
        name        = resultSet.getString(COL_NAME);
        code        = resultSet.getString(COL_CODE);
        term        = resultSet.getInt(COL_TERM);
        credit      = resultSet.getInt(COL_CREDIT);
        genre       = resultSet.getString(COL_GENRE);
        section     = resultSet.getString(COL_SECTION);
        classroom   = resultSet.getString(COL_CLASSROOM);
        schedule    = resultSet.getString(COL_SCHEDURE);
        teacherId   = resultSet.getInt(COL_TEACHER_ID);
    }

    public void extractJointFromResultSet(ResultSet resultSet) throws SQLException {
        teacherName = resultSet.getString(TeacherData.getAsCol(TeacherData.COL_NAME));
    }


}
