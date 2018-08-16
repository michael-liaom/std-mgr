package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ExchangeSubjectData extends DBHandlerService {
    final public static String TBL_NAME = "exchange_subject";
    final static String COL_ID          = "id";
    final static String COL_STUDENT_ID  = "student_id";
    final static String COL_DIRECTION   = "direction";
    final static String COL_SUBJECT     = "subject";
    final static String COL_CLASS_ID    = "class_id";
    final static String COL_CREATE      = "create";
    final static String COL_UPDATE      = "update";

    int id;
    int studentId;
    String direction;
    String subject;
    int classId;
    Date create, update;
    //Joint
    String studentName;
    int studentGender;
    String studentCode;
    //Seperated
    //StudentData studentData;
    //ClassData classData;

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
                + toDomain(COL_STUDENT_ID)
                + ","
                + toDomain(COL_DIRECTION)
                + ","
                + toDomain(COL_SUBJECT)
                + ","
                + toDomain(COL_CLASS_ID)
                + ","
                + toDomain(COL_CREATE)
                + ","
                + toDomain(COL_UPDATE)
                ;
    }

    static String getJointDomainColums() {
        return StudentData.toDomainAs(StudentData.COL_NAME)
                + ","
                + StudentData.toDomainAs(StudentData.COL_GENDER)
                + ","
                + StudentData.toDomainAs(StudentData.COL_CODE)
                ;
    }

    static String getJointTables() {
        return StudentData.TBL_NAME;
    }

    static String getJointCondition() {
        return toDomain(COL_STUDENT_ID) + "=" + StudentData.toDomain(COL_ID);
    }

    public String setColumsData() {
        return toDomain(COL_STUDENT_ID)   + "=" + toValue(studentId)
                + ","
                + toDomain(COL_DIRECTION) + "=" + toValue(direction)
                + ","
                + toDomain(COL_SUBJECT)   + "=" + toValue(subject)
                + ","
                + toDomain(COL_CLASS_ID)  + "=" + toValue(classId)
                + ","
                + toDomain(COL_CREATE)    + "=" + toValue(create)
                + ","
                + toDomain(COL_UPDATE)    + "=" + toValue(update)
                ;
    }

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id          = resultSet.getInt(COL_ID);
        studentId   = resultSet.getInt(COL_STUDENT_ID);
        direction   = resultSet.getString(COL_DIRECTION);
        subject     = resultSet.getString(COL_SUBJECT);
        classId     = resultSet.getInt(COL_CLASS_ID);
        create      = resultSet.getTimestamp(COL_CREATE);
        update      = resultSet.getTimestamp(COL_UPDATE);
    }

    public void extractJointFromResultSet(ResultSet resultSet) throws SQLException {
        studentName     = resultSet.getString(StudentData.getAsCol(StudentData.COL_NAME));
        studentGender   = resultSet.getInt(StudentData.getAsCol(StudentData.COL_GENDER));
        studentCode     = resultSet.getString(StudentData.getAsCol(StudentData.COL_CODE));
    }
}
