package com.weijie.studentworkmanagementsystem;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuthUserMgrUtils extends JdbcDataMgrUtils{
    final public static String TBL_STUDENT_REGISTATION = "student_registration";
    static private WeakReference<AuthUserMgrUtils> instance = null;
    JdbcMgrUtils jdbcMgrUtils;

    private AuthUserMgrUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
    }

    public synchronized static AuthUserMgrUtils getInstance(){
        if (instance == null){
            instance = new WeakReference<>(new AuthUserMgrUtils());
        }
        return instance.get();
    }

    public void requestFetchStudendRegistration(int studentId, final StudentData studentData,
                                                final Handler handler, final String tag) {
        final String sql = "SELECT * FROM " + TBL_STUDENT_REGISTATION
                + " WHERE id = '" + Integer.toString(studentId) +"'";
        new Thread(new Runnable() {
            @Override
            public void run() {
                ResultSet resultSet = jdbcMgrUtils.executeSQL(sql);
                if (resultSet != null) {
                    try {
                        int col = resultSet.getMetaData().getColumnCount();
                        if (resultSet.next()) {
                            studentData.id      = resultSet.getInt(StudentData.COL_ID);
                            studentData.name    = resultSet.getString(StudentData.COL_NAME);
                            studentData.class_id= resultSet.getInt(StudentData.COL_CLASS_ID);
                            studentData.code    = resultSet.getInt(StudentData.COL_CODE);
                        }
                        processHandler(handler, tag, JdbcMgrUtils.DB_REQUEST_SUCCESS);
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                        processHandler(handler, tag, JdbcMgrUtils.DB_REQUEST_FAILURE);
                    }
                }
                else {
                    processHandler(handler, tag, JdbcMgrUtils.DB_REQUEST_FAILURE);
                }
            }
        }).start();
    }
}
