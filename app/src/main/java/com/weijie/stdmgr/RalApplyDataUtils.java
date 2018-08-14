package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class RalApplyDataUtils extends DBHandlerService{
    final public static String TAG_FETCH_APPLIE_LIST= "TAG_FETCH_APPLIE_LIST";
    final public static String TAG_FETCH_ONE_APPLY  = "TAG_FETCH_ONE_APPLY";
    final public static String TAG_COMMIT_APPLY     = "TAG_COMMIT_APPLY";
    final public static String TAG_SIGN_APPLY       = "TAG_SIGN_APPLY";

    private static WeakReference<RalApplyDataUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;

    private RalApplyDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
    }

    public synchronized static RalApplyDataUtils getInstance(){
        if (instance == null || instance.get() == null){
            instance = new WeakReference<>(new RalApplyDataUtils());
        }

        return instance.get();
    }

    public void requestFetchApplieListFromStudent(final int studentId,
                                                  final ArrayList<RalApplyData> arrayList,
                                                  final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    sql = "SELECT "
                            + RalApplyData.getDomainColums()
                            + ","
                            + RalApplyData.getJointDomainColums()
                            + " FROM "
                            + RalApplyData.TBL_NAME
                            + ","
                            + RalApplyData.getJointTables()
                            + " WHERE "
                            + RalApplyData.toDomain(RalApplyData.COL_STUDENT_ID)
                            + "=" + toValue(studentId)
                            + " AND "
                            + RalApplyData.getJointCondition()
                            + " ORDER BY "
                            + RalApplyData.COL_ID
                            + " DESC "
                            + ";";
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            RalApplyData ralApplyData = new RalApplyData();
                            ralApplyData.extractFromResultSet(resultSet);
                            ralApplyData.extractJointFromResultSet(resultSet);
                            arrayList.add(ralApplyData);
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

    public void requestFetchApplyListToTeacher(final int teacherId,
                                               final ArrayList<RalApplyData> arrayList,
                                               final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    sql = "SELECT "
                            + RalApplyData.getDomainColums()
                            + ","
                            + RalApplyData.getJointDomainColums()
                            + " FROM "
                            + RalApplyData.TBL_NAME
                            + ","
                            + RalApplyData.getJointTables()
                            + " WHERE "
                            + RalApplyData.toDomain(RalApplyData.COL_TEACHER_ID)
                            + "=" + toValue(teacherId)
                            + " AND "
                            + RalApplyData.getJointCondition()
                            + " ORDER BY "
                            + RalApplyData.COL_APPROVAL
                            + " ASC, "
                            + RalApplyData.COL_ID
                            + " DESC "
                            + ";";
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            RalApplyData ralApplyData = new RalApplyData();
                            ralApplyData.extractFromResultSet(resultSet);
                            ralApplyData.extractJointFromResultSet(resultSet);
                            arrayList.add(ralApplyData);
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

    public void requestFetchApply(final int applyId, final RalApplyData ralApplyData,
                                  final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    sql = "SELECT "
                            + RalApplyData.getDomainColums()
                            + ","
                            + RalApplyData.getJointDomainColums()
                            + " FROM "
                            + RalApplyData.TBL_NAME
                            + ","
                            + RalApplyData.getJointTables()
                            + " WHERE "
                            + RalApplyData.toDomain(COL_ID) + "=" + applyId
                            + " AND "
                            + RalApplyData.getJointCondition()
                            + ";";
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        if (resultSet.next()) {
                            ralApplyData.extractFromResultSet(resultSet);
                            ralApplyData.extractJointFromResultSet(resultSet);
                        }
                        else {
                            isOk = false;
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
                    StudentData studentData = new StudentData();
                    isOk = StudentDataUtils.getInstance().
                            fetchStudentData(ralApplyData.studentId, studentData);
                    if (isOk) {
                        ralApplyData.studentData = studentData;
                    }
                }

                if (isOk) {
                    TeacherData teacherData = new TeacherData();
                    isOk = TeacherDataUtils.getInstance().
                            fetchTeachData(ralApplyData.teacherId, teacherData);
                    if (isOk) {
                        ralApplyData.classTeacher = teacherData;
                    }
                }

                if (isOk) {
                    RalData ralData = new RalData();
                    isOk = RalDataUtils.getInstance().
                            fetchRalData(ralApplyData.ralId, ralData);
                    if (isOk) {
                        ralApplyData.ralData = ralData;
                    }
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

    public void requestCommitApply(final RalApplyData ralApplyData,
                                   final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isOk = true;

                try {
                    String sql;
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet;

                    sql = "INSERT "
                            + RalApplyData.TBL_NAME
                            + " SET "
                            + ralApplyData.setColumsData()
                            + ";";
                    int affect = statement.executeUpdate(sql);
                    if (affect == 1) {
                        resultSet = statement.getGeneratedKeys();
                        if (resultSet.next()) {
                            ralApplyData.id = resultSet.getInt(1);
                        } else {
                            isOk = false;
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

    public void requestSignApply(final RalApplyData ralApplyData,
                                 final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isOk = true;

                try {
                    String sql;
                    Statement statement = jdbcMgrUtils.createStatement();

                    sql = "UPDATE "
                            + RalApplyData.TBL_NAME
                            + " SET "
                            + RalApplyData.COL_APPROVAL + "=" + toValue(ralApplyData.approval)
                            +","
                            + RalApplyData.COL_APPROVAL_COMMENT + "=" + toValue(ralApplyData.approvalComment)
                            + " WHERE "
                            + COL_ID + "=" + toValue(ralApplyData.id)
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
