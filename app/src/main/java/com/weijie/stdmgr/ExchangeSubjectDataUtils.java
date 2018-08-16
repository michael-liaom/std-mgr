package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by weijie on 2018/8/16.
 */
public class ExchangeSubjectDataUtils extends DBHandlerService {
    final public static String TAG_FETCH_DATA = "TAG_FETCH_DATA";
    final public static String TAG_FETCH_LIST = "TAG_FETCH_LIST";
    final public static String TAG_COMMIT_DATA= "TAG_COMMIT_DATA";

    private static WeakReference<ExchangeSubjectDataUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;

    private ExchangeSubjectDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
    }

    public synchronized static ExchangeSubjectDataUtils getInstance(){
        if (instance == null || instance.get() == null){
            instance = new WeakReference<>(new ExchangeSubjectDataUtils());
        }

        return instance.get();
    }

    public boolean fetchSubjectData(int subjectId, ExchangeSubjectData subjectData) {
        String sql;
        boolean isOk = true;

        try {
            Statement statement = jdbcMgrUtils.createStatement();
            sql = "SELECT "
                    + ExchangeSubjectData.getDomainColums()
                    + ","
                    + ExchangeSubjectData.getJointDomainColums()
                    + " FROM "
                    + ExchangeSubjectData.TBL_NAME
                    + ","
                    + ExchangeSubjectData.getJointTables()
                    + " WHERE "
                    + ExchangeSubjectData.getJointCondition()
                    + " AND "
                    + ExchangeSubjectData.toDomain(COL_ID) + "=" + toValue(subjectId)
                    + ";";
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet != null && resultSet.next()) {
                subjectData.extractFromResultSet(resultSet);
                subjectData.extractJointFromResultSet(resultSet);
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

        /*
        if (isOk) {
            TeacherData teacherData = new TeacherData();
            isOk = TeacherDataUtils.getInstance().fetchTeachData(classData.teacherId, teacherData);
            if (isOk) {
                classData.teacherData = teacherData;
            }
        }
        */

        return isOk;
    }

    public void requestFetchSubjectData(final int subjectId, final ExchangeSubjectData subjectData,
                                      final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isOk = fetchSubjectData(subjectId, subjectData);

                if (isOk) {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_SUCCESS, tag);
                }
                else {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_FAILURE, tag);
                }
            }
        }).start();
    }

    public void requestFetchSubjectListOfClass(final int classId,
                                               final ArrayList<ExchangeSubjectData> arrayList,
                                               final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    sql = "SELECT "
                            + ExchangeSubjectData.getDomainColums()
                            + ","
                            + ExchangeSubjectData.getJointDomainColums()
                            + " FROM "
                            + ExchangeSubjectData.TBL_NAME
                            + ","
                            + ExchangeSubjectData.getJointTables()
                            + " WHERE "
                            + ExchangeSubjectData.toDomain(ExchangeSubjectData.COL_CLASS_ID) + "=" + toValue(classId)
                            + " AND "
                            + ExchangeSubjectData.getJointCondition()
                            + " ORDER BY "
                            + ExchangeSubjectData.toDomain(ExchangeSubjectData.COL_UPDATE)
                            + " DESC"
                            + ";";

                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            ExchangeSubjectData subjectData = new ExchangeSubjectData();
                            subjectData.extractFromResultSet(resultSet);
                            subjectData.extractJointFromResultSet(resultSet);
                            arrayList.add(subjectData);
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

    public void requestFetchSubjectListOfStudent(final int studentId,
                                               final ArrayList<ExchangeSubjectData> arrayList,
                                               final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    sql = "SELECT "
                            + ExchangeSubjectData.getDomainColums()
                            + ","
                            + ExchangeSubjectData.getJointDomainColums()
                            + " FROM "
                            + ExchangeSubjectData.TBL_NAME
                            + ","
                            + ExchangeSubjectData.getJointTables()
                            + " WHERE "
                            + ExchangeSubjectData.toDomain(ExchangeSubjectData.COL_STUDENT_ID) + "=" + toValue(studentId)
                            + " AND "
                            + ExchangeSubjectData.getJointCondition()
                            + " ORDER BY "
                            + ExchangeSubjectData.toDomain(ExchangeSubjectData.COL_UPDATE)
                            + " DESC"
                            + ";";

                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            ExchangeSubjectData subjectData = new ExchangeSubjectData();
                            subjectData.extractFromResultSet(resultSet);
                            subjectData.extractJointFromResultSet(resultSet);
                            arrayList.add(subjectData);
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

    public void requestCommit(final ExchangeSubjectData subjectData,
                              final ExchangeDetailData detailData,
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
                                + ExchangeSubjectData.TBL_NAME
                                + " SET "
                                + subjectData.setColumsData()
                                + ";";
                        int affect = statement.executeUpdate(sql);
                        if (affect == 1) {
                            resultSet = statement.getGeneratedKeys();
                            if (resultSet.next()) {
                                subjectData.id = resultSet.getInt(1);
                            } else {
                                isOk = false;
                            }
                        } else {
                            isOk = false;
                        }
                    }

                    if (isOk) {
                        detailData.subjectId = subjectData.id;
                        isOk = ExchangeDetailDataUtils.getInstance().createData(detailData);
                    }

                    if (isOk) {
                        sql = "COMMIT;";
                        statement.executeQuery(sql);
                    }
                    else {
                        sql = "ROLLBACK;";
                        statement.executeQuery(sql);
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

    public boolean updateTimestamp(int subjectId, Date timestamp) {
        boolean isOk = true;

        try {
            String sql;
            Statement statement = jdbcMgrUtils.createStatement();

            sql = "UPDATE "
                    + ExchangeSubjectData.TBL_NAME
                    + " SET "
                    + ExchangeSubjectData.COL_UPDATE + "=" + toValue(timestamp)
                    + " WHERE "
                    + COL_ID + "=" + toValue(subjectId)
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

        return isOk;
    }
}

