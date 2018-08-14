package com.weijie.stdmgr;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by weijie on 2018/8/14.
 */
public class RalData {
    final static String TBL_NAME        = "ral_detail";
    final static String COL_ID          = "id";
    final static String COL_NAME        = "name";
    final static String COL_AMOUNT        = "amount";
    final static String COL_GRADE     = "grade";
    final static String COL_TERM        = "term";
    final static String COL_NUMBER       = "number";
    final static String COL_REQUIRE    = "requirement";
//    final static String ALIAS_CLASS       = "class_teacher";
//    final static String TBL_ALIAS_CLASS = "teacher_registration AS " + ALIAS_CLASS;
//    final static String ALIAS_COURSE    = "course_teacher";
//    final static String TBL_ALIAS_COURSE= "teacher_registration AS " + ALIAS_COURSE;

    int id;
    String name;
    int amount;
    String grade;
    String term;
    int number;
    String requirement;

    static String toDomain(String col) {
        return TBL_NAME + "." + col;
    }

    static String toDomainAs(String col) {
        return TBL_NAME + "." + col + " AS " + TBL_NAME + "_" + col;
    }

    static String getAsCol(String col) {
        return TBL_NAME + "_" + col;
    }

    /*
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
*/
    static String getDomainColums() {
        return toDomain(COL_ID)
                + ","
                + toDomain(COL_NAME)
                + ","
                + toDomain(COL_AMOUNT)
                + ","
                + toDomain(COL_GRADE)
                + ","
                + toDomain(COL_TERM)
                + ","
                + toDomain(COL_NUMBER)
                + ","
                + toDomain(COL_REQUIRE);
    }

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id      = resultSet.getInt(COL_ID);
        name    = resultSet.getString(COL_NAME);
        amount  = resultSet.getInt(COL_AMOUNT);
        grade   = resultSet.getString(COL_GRADE);
        term    = resultSet.getString(COL_TERM);
        number  = resultSet.getInt(COL_NUMBER);
        requirement   = resultSet.getString(COL_REQUIRE);
    }
}
