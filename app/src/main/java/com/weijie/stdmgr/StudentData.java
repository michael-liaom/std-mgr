package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by weijie on 2018/5/23.
 */
public class StudentData extends DBHandlerService {
    final public static String TBL_NAME = "student_registration";
    final static String COL_ID          = "id";
    final static String COL_NAME        = "name";
    final static String COL_GENDER      = "gender";
    final static String COL_CODE        = "code";
    final static String COL_EMAIL       = "email";
    final static String COL_MOBILE      = "mobile";
    final static String COL_ROOM        = "room";
    final static String COL_CLASS_ID    = "class_id";
    final static String COL_REG_CODE    = "reg_code";

    int id;
    String name;
    int gender;
    int code;
    String email;
    String mobile;
    String room;
    int class_id;
    //Joint
    String className;
    //Seperated
    ClassData classData;

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
                + toDomain(COL_GENDER)
                + ","
                + toDomain(COL_CODE)
                + ","
                + toDomain(COL_EMAIL)
                + ","
                + toDomain(COL_MOBILE)
                + ","
                + toDomain(COL_ROOM)
                + ","
                + toDomain(COL_CLASS_ID);
    }

    static String getJointDomainColums() {
        return ClassData.toDomainAs(ClassData.COL_NAME);
    }

    static String getJointTables() {
        return ClassData.TBL_NAME;
    }

    static String getJointCondition() {
        return toDomain(COL_CLASS_ID) + "=" + ClassData.toDomain(COL_ID);
    }

    public String setColumsData() {
        return COL_EMAIL   + "=" + toValue(email)
                + ","
                + COL_MOBILE      + "=" + toValue(mobile);
    }

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id      = resultSet.getInt(COL_ID);
        name    = resultSet.getString(COL_NAME);
        gender  = resultSet.getInt(COL_GENDER);
        code    = resultSet.getInt(COL_CODE);
        email   = resultSet.getString(COL_EMAIL);
        mobile   = resultSet.getString(COL_MOBILE);
        room    = resultSet.getString(COL_ROOM);
        class_id= resultSet.getInt(COL_CLASS_ID);
    }

    public String getGenderName() {
        if (gender == 1) {
            return "男";
        }
        else {
            return "女";
        }
    }

    public static String getGenderName(int gender) {
        if (gender == 1) {
            return "男";
        }
        else {
            return "女";
        }
    }
}
