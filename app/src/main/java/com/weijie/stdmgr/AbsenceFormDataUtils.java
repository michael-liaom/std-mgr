package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AbsenceFormDataUtils extends DBHandlerService{
    final static String TBL_NAME        = "absence_cc";
    final static String COL_APPLY_ID    = "apply_id";
    final static String COL_Course_ID   = "course_id";

    final public static String TAG_FETCH_APPLIES_AS_STUDENT = "TAG_FETCH_APPLIES_AS_STUDENT";
    final public static String TAG_FETCH_ONE_APPLY      = "TAG_FETCH_ONE_APPLY";
    final public static String TAG_COMMIT_APPLY         = "TAG_COMMIT_APPLY";
    final public static String TAG_COMMIT_APPROVAL      = "TAG_COMMIT_APPROVAL";
    final public static String TAG_FETCH_APPLY_CC       = "TAG_FETCH_APPLY_CC";
    final public static String TAG_APPROVE_APPLY        = "TAG_APPROVE_APPLY";
    final public static String TAG_REJECT_APPLY         = "TAG_REJECT_APPLY";

    private static WeakReference<AbsenceFormDataUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;
    private AuthUserData authUser;

    private AbsenceFormDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
        authUser = MyApplication.getInstance().authUser;
    }

    public synchronized static AbsenceFormDataUtils getInstance(){
        if (instance == null || instance.get() == null){
            instance = new WeakReference<>(new AbsenceFormDataUtils());
        }

        return instance.get();
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


    public void requestFetchAppliesAsStudent(final int studentId,
                                             final ArrayList<AbsenceFormData> arrayList,
                                             final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    sql = "SELECT "
                            + AbsenceFormData.getDomainColums()
                            + ","
                            + AbsenceFormData.getJointDomainColums()
                            + " FROM "
                            + AbsenceFormData.TBL_NAME
                            + ","
                            + AbsenceFormData.getJointTables()
                            + " WHERE "
                            + AbsenceFormData.toDomain(AbsenceFormData.COL_STUDENT_ID)
                            + "=" + toValue(studentId)
                            + " AND "
                            + AbsenceFormData.getJointCondition()
                            + " ORDER BY "
                            + AbsenceFormData.COL_ID
                            + " DESC "
                            + ";";
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            AbsenceFormData absenceFormData = new AbsenceFormData();
                            absenceFormData.extractFromResultSet(resultSet);
                            absenceFormData.extractJointFromResultSet(resultSet);
                            arrayList.add(absenceFormData);
                        }
                    }
                    else {
                        isOk = false;
                    }
                    statement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    isOk = false;
                }

                if (isOk) {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_SUCCESS, tag);
                }
                else {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_FAILURE, tag);
                }
            }
        }).start();
    }

    public void requestFetchAppliesToAsTeacher(final int teacherId,
                                             final ArrayList<AbsenceFormData> arrayList,
                                             final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    sql = "SELECT "
                            + AbsenceFormData.getDomainColums()
                            + ","
                            + AbsenceFormData.getJointDomainColums()
                            + " FROM "
                            + AbsenceFormData.TBL_NAME
                            + ","
                            + AbsenceFormData.getJointTables()
                            + " WHERE "
                            + AbsenceFormData.getJointCondition()
                            + " AND "
                            + " ("          //---1
                            + AbsenceFormData.toDomain(AbsenceFormData.COL_TO_TEACHER_ID)
                            + "=" + toValue(teacherId)
                            + " OR "
                            + " ("          //---2
                            + AbsenceFormData.toDomain(AbsenceFormData.COL_APPROVAL)
                            + "=" + toValue(AbsenceFormData.APPROVAL)
                            + " AND "
                            + AbsenceFormData.toDomain(COL_ID)
                            + " IN "
                            + " (SELECT "   //---3
                            + toDomain(COL_APPLY_ID)
                            + " FROM "
                            + TBL_NAME + "," + CourseData.TBL_NAME
                            + " WHERE "
                            + CourseData.toDomain(CourseData.COL_TEACHER_ID)
                            + "=" + toValue(teacherId)
                            + " AND "
                            + toDomain(COL_Course_ID) + "=" +CourseData.toDomain(COL_ID)
                            + " )))"          //---3,2,1
                            + " ORDER BY "
                            + AbsenceFormData.toDomain(AbsenceFormData.COL_APPROVAL)
                            + ";";
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            AbsenceFormData absenceFormData = new AbsenceFormData();
                            absenceFormData.extractFromResultSet(resultSet);
                            absenceFormData.extractJointFromResultSet(resultSet);
                            arrayList.add(absenceFormData);
                        }
                    }
                    else {
                        isOk = false;
                    }
                    statement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    isOk = false;
                }

                if (isOk) {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_SUCCESS, tag);
                }
                else {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_FAILURE, tag);
                }
            }
        }).start();
    }

    public void requestFetchCCMeApply(final ArrayList<AbsenceFormData> arrayList,
                                       final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    sql = "SELECT "
                            + AbsenceFormData.getDomainColums()
                            + ","
                            + AbsenceFormData.getJointDomainColums()
                            + " FROM "
                            + AbsenceFormData.TBL_NAME
                            + ","
                            + AbsenceFormData.getJointTables()
                            + " WHERE "
                            + AbsenceFormData.toDomain(AbsenceFormData.COL_STUDENT_ID)
                            + "=" + toValue(authUser.studend_id)
                            + " AND "
                            + AbsenceFormData.getJointCondition()
                            + " ORDER BY "
                            + AbsenceFormData.COL_ID
                            + " DESC "
                            + ";";
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            AbsenceFormData absenceFormData = new AbsenceFormData();
                            absenceFormData.extractFromResultSet(resultSet);
                            absenceFormData.extractJointFromResultSet(resultSet);
                            arrayList.add(absenceFormData);
                        }
                    }
                    else {
                        isOk = false;
                    }
                    statement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    isOk = false;
                }

                if (isOk) {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_SUCCESS, tag);
                }
                else {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_FAILURE, tag);
                }
            }
        }).start();
    }

    public void requestFetchApply(final int applyId, final AbsenceFormData absenceFormData,
                                  final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    sql = "SELECT "
                            + AbsenceFormData.getDomainColums()
                            + ","
                            + AbsenceFormData.getJointDomainColums()
                            + " FROM "
                            + AbsenceFormData.TBL_NAME
                            + ","
                            + AbsenceFormData.getJointTables()
                            + " WHERE "
                            + AbsenceFormData.toDomain(COL_ID) + "=" + applyId
                            + " AND "
                            + AbsenceFormData.getJointCondition()
                            + ";";
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        if (resultSet.next()) {
                            absenceFormData.extractFromResultSet(resultSet);
                            absenceFormData.extractJointFromResultSet(resultSet);
                        }
                    }
                    else {
                        isOk = false;
                    }
                    statement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    isOk = false;
                }

                if (isOk) {
                    isOk = fetchApplyCcCourse(absenceFormData);
                }

                if (isOk) {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_SUCCESS, tag);
                }
                else {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_FAILURE, tag);
                }
            }
        }).start();
    }

    private boolean fetchApplyCcCourse(final AbsenceFormData absenceFormData) {
        String sql;
        boolean isOk = true;

        try {
            Statement statement = jdbcMgrUtils.createStatement();
            sql = "SELECT "
                    + CourseData.getDomainColums()
                    + ","
                    + CourseData.getJointDomainColums()
                    + " FROM "
                    + TBL_NAME + ","
                    + CourseData.TBL_NAME + ","
                    + CourseData.getJointTables()
                    + " WHERE "
                    + toDomain(COL_APPLY_ID) + "=" + absenceFormData.id
                    + " AND "
                    + CourseData.toDomain(COL_ID) + "=" + toDomain(COL_Course_ID)
                    + " AND "
                    + CourseData.getJointCondition()
                    + ";";
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet != null) {
                while (resultSet.next()) {
                    CourseData courseData = new CourseData();
                    courseData.extractFromResultSet(resultSet);
                    courseData.extractJointFromResultSet(resultSet);
                    absenceFormData.ccList.add(courseData);
                }
            }
            else {
                isOk = false;
            }
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            isOk = false;
        }

        return isOk;
    }

    public void requestFetchApplyCcCourse(final AbsenceFormData absenceFormData,
                                        final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                isOk = fetchApplyCcCourse(absenceFormData);

                if (isOk) {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_SUCCESS, tag);
                }
                else {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_FAILURE, tag);
                }
            }
        }).start();
    }

    public void requestCommitApply(final AbsenceFormData absenceFormData,
                                   final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isOk = true;

                try {
                    String sql;
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet;

                    sql = "START TRANSACTION;";
                    resultSet = statement.executeQuery(sql);
                    if (resultSet == null) {
                        isOk = false;
                    }

                    if (isOk) {
                        sql = "INSERT "
                                + AbsenceFormData.TBL_NAME
                                + " SET "
                                + AbsenceFormData.COL_STUDENT_ID
                                + "=" + toValue(absenceFormData.studentId)
                                + ","
                                + AbsenceFormData.COL_TO_TEACHER_ID
                                + "=" + toValue(absenceFormData.toTeacherId)
                                + ","
                                + AbsenceFormData.COL_TYPE
                                + "=" + toValue(absenceFormData.type)
                                + ","
                                + AbsenceFormData.COL_BEGIN
                                + "=" + toValue(absenceFormData.begin)
                                + ","
                                + AbsenceFormData.COL_END + "=" + toValue(absenceFormData.ending)
                                + ","
                                + AbsenceFormData.COL_CAUSE + "=" + toValue(absenceFormData.cause)
                                + ";";
                        int affect = statement.executeUpdate(sql);
                        if (affect == 1) {
                            resultSet = statement.getGeneratedKeys();
                            if (resultSet.next()) {
                                absenceFormData.id = resultSet.getInt(1);
                            } else {
                                isOk = false;
                            }
                        }
                        else {
                            isOk = false;
                        }
                    }

                    if (isOk) {
                        for (CourseData courseData : absenceFormData.ccList) {
                            sql = "INSERT "
                                    + TBL_NAME
                                    + " SET "
                                    + COL_APPLY_ID + "=" + toValue(absenceFormData.id)
                                    + ","
                                    + COL_Course_ID + "=" + toValue(courseData.id)
                                    +";";
                            int affect = statement.executeUpdate(sql);
                            if (affect != 1) {
                                isOk = false;
                                break;
                            }
                        }
                    }

                    if (isOk) {
                        sql = "COMMIT;";
                    }
                    else {
                        sql = "ROLLBACK;";
                    }
                    statement.executeQuery(sql);
                    statement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    isOk = false;
                }

                if (isOk) {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_SUCCESS, tag);
                }
                else {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_FAILURE, tag);
                }
            }
        }).start();
    }

    public void requestApproveApply(final AbsenceFormData absenceFormData,
                                   final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isOk = true;

                try {
                    String sql;
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet;

                    sql = "UPDATE "
                            + AbsenceFormData.TBL_NAME
                            + " SET "
                            + AbsenceFormData.COL_APPROVAL + "=" + toValue(absenceFormData.APPROVAL)
                            + " WHERE "
                            + COL_ID + "=" + toValue(absenceFormData.id)
                            + ";";
                    int affect = statement.executeUpdate(sql);
                    if (affect != 1) {
                        isOk = false;
                    }

                    statement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    isOk = false;
                }

                if (isOk) {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_SUCCESS, tag);
                }
                else {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_FAILURE, tag);
                }
            }
        }).start();
    }

    public void requestRejectApply(final AbsenceFormData absenceFormData,
                                    final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isOk = true;

                try {
                    String sql;
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet;

                    sql = "UPDATE "
                            + AbsenceFormData.TBL_NAME
                            + " SET "
                            + AbsenceFormData.COL_APPROVAL + "=" + toValue(absenceFormData.REJECT)
                            + " WHERE "
                            + COL_ID + "=" + toValue(absenceFormData.id)
                            + ";";
                    int affect = statement.executeUpdate(sql);
                    if (affect != 1) {
                        isOk = false;
                    }

                    statement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    isOk = false;
                }

                if (isOk) {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_SUCCESS, tag);
                }
                else {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_FAILURE, tag);
                }
            }
        }).start();
    }
}
