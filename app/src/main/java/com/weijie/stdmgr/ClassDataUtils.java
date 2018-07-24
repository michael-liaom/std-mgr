package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClassDataUtils extends DBHandlerService {
    final public static String TAG_FETCH_CLASS_DATA = "TAG_FETCH_CLASS_DATA";

    private static WeakReference<ClassDataUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;

    private ClassDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
    }

    public synchronized static ClassDataUtils getInstance(){
        if (instance == null || instance.get() == null){
            instance = new WeakReference<>(new ClassDataUtils());
        }

        return instance.get();
    }

    public void requestFetchClassData(final int classId, final ClassData classData,
                                                final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    sql = "SELECT "
                            + ClassData.getDomainColums()
                            + ","
                            + ClassData.getJointDomainColums()
                            + " FROM "
                            + ClassData.TBL_NAME
                            + ","
                            + ClassData.getJointTables()
                            + " WHERE "
                            + ClassData.getJointCondition()
                            + " AND "
                            + ClassData.COL_ID + "=" + toValue(classId)
                            + " AND "
                            + COL_STATUS + "=" + STATUS_VALID
                            + ";";
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null && resultSet.next()) {
                        classData.extractFromResultSet(resultSet);
                        classData.extractJointFromResultSet(resultSet);
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
