package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class TeacherDataUtils extends DBHandlerService {
    final static String TAG_FETCH_TEACHER_REGISTRATION  = "TAG_FETCH_TEACHER_REGISTRATION";
    final public static String TAG_FETCH_CLASS_DATA     = "TAG_FETCH_CLASS_DATA";

    private static WeakReference<TeacherDataUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;

    private TeacherDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
    }

    public synchronized static TeacherDataUtils getInstance(){
        if (instance == null || instance.get() == null){
            instance = new WeakReference<>(new TeacherDataUtils());
        }
        return instance.get();
    }

    public boolean fetchTeachData(int teacherId, TeacherData teacherData) {
        String sql = "SELECT * FROM " + TeacherData.TBL_NAME
                + " WHERE "
                + TeacherData.COL_ID + "=" + toValue(teacherId)
                + " AND "
                + COL_STATUS + "=" + STATUS_VALID
                + ";";
        boolean isOk = true;

        try {
            Statement statement = jdbcMgrUtils.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet != null && resultSet.next()) {
                teacherData.extractFromResultSet(resultSet);
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

    public void requestFetchTeacherRegistration(final int studentId, final TeacherData teacherData,
                                                final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Boolean isOk = fetchTeachData(studentId, teacherData);
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
