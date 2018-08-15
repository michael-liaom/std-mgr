package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by weijie on 2018/8/17.
 */
public class ExchangeDetailData extends DBHandlerService {
    final public static String TBL_NAME = "exchange_detail";
    final static String COL_ID          = "id";
    final static String COL_SUBJECT_ID  = "subject_id";
    final static String COL_DIRECTION   = "direction";
    final static String COL_CONTENT     = "content";
    final static String COL_CREATE      = "create";

    int id;
    int subjectId;
    String direction;
    String content;
    Date create;
    //Joint
    //String studentName;
    //Seperated
    ExchangeSubjectData subjectData;
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
                + toDomain(COL_SUBJECT_ID)
                + ","
                + toDomain(COL_DIRECTION)
                + ","
                + toDomain(COL_CONTENT)
                + ","
                + toDomain(COL_CREATE)
                ;
    }

    /*
    static String getJointDomainColums() {
        return StudentData.toDomainAs(StudentData.COL_NAME);
    }

    static String getJointTables() {
        return StudentData.TBL_NAME;
    }

    static String getJointCondition() {
        return toDomain(COL_STUDENT_ID) + "=" + StudentData.toDomain(COL_ID);
    }
    */

    public String setColumsData() {
        return COL_SUBJECT_ID   + "=" + toValue(subjectId)
                + ","
                + COL_DIRECTION   + "=" + toValue(direction)
                + ","
                + COL_CONTENT   + "=" + toValue(content)
                + ","
                + COL_CREATE   + "=" + toValue(create)
                ;
    }

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id          = resultSet.getInt(COL_ID);
        subjectId   = resultSet.getInt(COL_SUBJECT_ID);
        direction   = resultSet.getString(COL_DIRECTION);
        content     = resultSet.getString(COL_CONTENT);
        create      = resultSet.getDate(COL_CREATE);
    }

    /*
    public void extractJointFromResultSet(ResultSet resultSet) throws SQLException {
        studentName = resultSet.getString(StudentData.getAsCol(StudentData.COL_NAME));
    }
    */
}
