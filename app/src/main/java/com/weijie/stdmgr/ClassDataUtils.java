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
    private AuthUserData authUser;

    private ClassDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
        authUser = MyApplication.getInstance().authUser;
    }

    public synchronized static ClassDataUtils getInstance(){
        if (instance == null){
            instance = new WeakReference<>(new ClassDataUtils());
        }
        return instance.get();
    }

    private String toValue(String value) {
        return "'" + value + "'";
    }

    private String toValue(int value) {
        return toValue(Integer.toString(value));
    }

    public void requestFetchClassData(final int classId, final ClassData classData,
                                                final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql = "SELECT * FROM " + ClassData.TBL_NAME
                        + " WHERE "
                        + ClassData.COL_ID + "=" + toValue(classId)
                        + " AND "
                        + COL_STATUS + "=" + STATUS_VALID
                        + ";";
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null && resultSet.next()) {
                        classData.extractFromResultSet(resultSet);
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
                    TeacherData teacherData = new TeacherData();
                    isOk = TeacherDataUtils.getInstance().fetchTeachData(classData.masterTeacherId, teacherData);
                    if (isOk) {
                        classData.masterTeacher = teacherData;
                    }
                }

                if (isOk) {
                    TeacherData teacherData = new TeacherData();
                    isOk = TeacherDataUtils.getInstance().fetchTeachData(classData.assistantTeacherId, teacherData);
                    if (isOk) {
                        classData.assistantTeacher = teacherData;
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

}
