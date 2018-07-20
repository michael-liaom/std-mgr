package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

public class AbsenceApplyDataUtils extends DBHandlerService{
    final static String TBL_NAME        = "absence_cc";
    final static String COL_APPLY_ID    = "apply_id";
    final static String COL_TEACHER_ID  = "teacher_id";

    final public static String TAG_FETCH_ALLMY_APPLY    = "TAG_FETCH_STUDENT_REG";
    final public static String TAG_COMMIT_APPLY         = "TAG_COMMIT_APPLY";

    private static WeakReference<AbsenceApplyDataUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;
    private AuthUserData authUser;

    private AbsenceApplyDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
        authUser = MyApplication.getInstance().authUser;
    }

    public synchronized static AbsenceApplyDataUtils getInstance(){
        if (instance == null){
            instance = new WeakReference<>(new AbsenceApplyDataUtils());
        }
        else if (instance.get() == null) {
            instance = new WeakReference<>(new AbsenceApplyDataUtils());
        }
        return instance.get();
    }

    public void requestFetchAllMyApply(final ArrayList<AbsenceApplyData> arrayList,
                                       final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    sql = "SELECT "
                            + AbsenceApplyData.toDomain(AbsenceApplyData.COL_ID)
                            + ","
                            + AbsenceApplyData.toDomain(AbsenceApplyData.COL_BEGIN)
                            + ","
                            + AbsenceApplyData.toDomain(AbsenceApplyData.COL_END)
                            + ","
                            + AbsenceApplyData.toDomain(AbsenceApplyData.COL_TYPE)
                            + ","
                            + AbsenceApplyData.toDomain(AbsenceApplyData.COL_CAUSE)
                            + ","
                            + AbsenceApplyData.toDomain(AbsenceApplyData.COL_APPROVAL)
                            + ","
                            + StudentData.toDomainAs(StudentData.COL_CODE)
                            + ","
                            + StudentData.toDomainAs(StudentData.COL_NAME)
                            + ","
                            + TeacherData.toDomainAs(TeacherData.COL_NAME)
                            + " FROM "
                            + AbsenceApplyData.TBL_NAME + ","
                            + StudentData.TBL_NAME + ","
                            + TeacherData.TBL_NAME
                            + " WHERE "
                            + AbsenceApplyData.toDomain(AbsenceApplyData.COL_STUDENT_ID)  + "=" + toValue(authUser.studend_id)
                            + " AND "
                            + StudentData.toDomain(StudentData.COL_ID)  + "=" + toValue(authUser.studend_id)
                            + " ORDER BY "
                            + AbsenceApplyData.COL_ID
                            + " DESC "
                            + ";";
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        if (resultSet.next()) {
                            AbsenceApplyData absenceApplyData = new AbsenceApplyData();
                            absenceApplyData.extractFromResultSet(resultSet);
                            absenceApplyData.extractAsFromResultSet(resultSet);
                            arrayList.add(absenceApplyData);
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

    public void requestCommitApply(final AbsenceApplyData absenceApplyData,
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
                                + AbsenceApplyData.TBL_NAME
                                + " SET "
                                + AbsenceApplyData.COL_STUDENT_ID + "=" + toValue(absenceApplyData.studentId)
                                + ","
                                + AbsenceApplyData.COL_TO_TEACHER_ID + "=" + toValue(absenceApplyData.toTeacherId)
                                + ","
                                + AbsenceApplyData.COL_TYPE + "=" + toValue(absenceApplyData.type)
                                + ","
                                + AbsenceApplyData.COL_BEGIN + "=" + toValue(absenceApplyData.begin)
                                + ","
                                + AbsenceApplyData.COL_END + "=" + toValue(absenceApplyData.ending)
                                + ","
                                + AbsenceApplyData.COL_CAUSE + "=" + toValue(absenceApplyData.cause)
                                + ";";
                        int affect = statement.executeUpdate(sql);
                        if (affect == 1) {
                            resultSet = statement.getGeneratedKeys();
                            if (resultSet.next()) {
                                absenceApplyData.id = resultSet.getInt(1);
                            } else {
                                isOk = false;
                            }
                        }
                        else {
                            isOk = false;
                        }
                    }

                    if (isOk) {
                        for (CourseData courseData : absenceApplyData.ccList) {
                            sql = "INSERT "
                                    + TBL_NAME
                                    + " SET "
                                    + COL_APPLY_ID + "=" + toValue(absenceApplyData.id)
                                    + ","
                                    + COL_TEACHER_ID + "=" + toValue(courseData.teacherId)
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
}
