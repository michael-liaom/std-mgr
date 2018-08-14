package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by weijie on 2018/8/14.
 */
public class RalDataUtils extends DBHandlerService {
    final static String TAG_FETCH_DATA  = "TAG_FETCH_DATA";
    final static String TAG_FETCH_LIST  = "TAG_FETCH_LIST";

    private static WeakReference<RalDataUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;

    private RalDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
    }

    public synchronized static RalDataUtils getInstance(){
        if (instance == null || instance.get() == null){
            instance = new WeakReference<>(new RalDataUtils());
        }
        return instance.get();
    }

    public boolean fetchRalData(int ralId, RalData ralData) {
        String sql = "SELECT * FROM " + RalData.TBL_NAME
                + " WHERE "
                + RalData.COL_ID + "=" + toValue(ralId)
                + ";";
        boolean isOk = true;

        try {
            Statement statement = jdbcMgrUtils.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet != null && resultSet.next()) {
                ralData.extractFromResultSet(resultSet);
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

    public void requestFetchRalData(final int ralId, final RalData ralData,
                                                final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Boolean isOk = fetchRalData(ralId, ralData);
                if (isOk) {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_SUCCESS, tag);
                }
                else {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_FAILURE, tag);
                }
            }
        }).start();
    }

    public void requestRalDataList(final int term,
                               final ArrayList<RalData> arrayList,
                               final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    sql = "SELECT "
                            + "*"
                            + " FROM "
                            + RalData.TBL_NAME
                            + " WHERE "
                            + RalData.COL_TERM + "=" + toValue(term)
                            + ";";

                    sql += ";";

                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            RalData ralData = new RalData();
                            ralData.extractFromResultSet(resultSet);
                            arrayList.add(ralData);
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

}
