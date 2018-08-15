package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by weijie on 2018/8/17.
 */
public class ExchangeDetailDataUtils extends DBHandlerService {
    final public static String TAG_FETCH_CLASS_DATA = "TAG_FETCH_CLASS_DATA";
    final public static String TAG_FETCH_CLASS_LIST = "TAG_FETCH_CLASS_LIST";

    private static WeakReference<ExchangeDetailDataUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;

    private ExchangeDetailDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
    }

    public synchronized static ExchangeDetailDataUtils getInstance(){
        if (instance == null || instance.get() == null){
            instance = new WeakReference<>(new ExchangeDetailDataUtils());
        }

        return instance.get();
    }

    public boolean fetchDetailtData(int detailId, ExchangeDetailData detailData) {
        String sql;
        boolean isOk = true;

        try {
            Statement statement = jdbcMgrUtils.createStatement();
            sql = "SELECT "
                    + ExchangeDetailData.getDomainColums()
                    //+ ","
                    //+ ExchangeDetailData.getJointDomainColums()
                    + " FROM "
                    + ExchangeDetailData.TBL_NAME
                    //+ ","
                    //+ ExchangeDetailData.getJointTables()
                    + " WHERE "
                    //+ ExchangeDetailData.getJointCondition()
                    //+ " AND "
                    + ExchangeDetailData.toDomain(COL_ID) + "=" + toValue(detailId)
                    + ";";
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet != null && resultSet.next()) {
                detailData.extractFromResultSet(resultSet);
                //detailData.extractJointFromResultSet(resultSet);
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

    public void requestFetchDetailData(final int detailId, final ExchangeDetailData detailData,
                                        final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isOk = fetchDetailtData(detailId, detailData);

                if (isOk) {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_SUCCESS, tag);
                }
                else {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_FAILURE, tag);
                }
            }
        }).start();
    }

    public void requestFetchDetailListOfSubject(final int subjectId,
                                               final ArrayList<ExchangeDetailData> arrayList,
                                               final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    sql = "SELECT "
                            + ExchangeDetailData.getDomainColums()
                            //+ ","
                            //+ ExchangeDetailData.getJointDomainColums()
                            + " FROM "
                            + ExchangeDetailData.TBL_NAME
                            //+ ","
                            //+ ExchangeDetailData.getJointTables()
                            + " WHERE "
                            + ExchangeDetailData.toDomain(ExchangeSubjectData.COL_CLASS_ID) + "=" + toValue(subjectId)
                            //+ " AND "
                            //+ ExchangeDetailData.getJointCondition()
                            + ";";

                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            ExchangeDetailData detailData = new ExchangeDetailData();
                            detailData.extractFromResultSet(resultSet);
                            //detailData.extractJointFromResultSet(resultSet);
                            arrayList.add(detailData);
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

    public void requestCommit(final ExchangeDetailData detailData,
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
                                + detailData.setColumsData()
                                + ";";
                        int affect = statement.executeUpdate(sql);
                        if (affect == 1) {
                            resultSet = statement.getGeneratedKeys();
                            if (resultSet.next()) {
                                detailData.id = resultSet.getInt(1);
                            } else {
                                isOk = false;
                            }
                        } else {
                            isOk = false;
                        }
                    }

                    if (isOk) {
                        isOk = ExchangeSubjectDataUtils.getInstance()
                                .updateTimestamp(detailData.subjectId, detailData.create);
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

}

