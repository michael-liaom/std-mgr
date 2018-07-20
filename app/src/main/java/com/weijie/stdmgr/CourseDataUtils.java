package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CourseDataUtils extends DBHandlerService {
    final static String TAG_FETCH_COURSE_DATA    = "TAG_FETCH_COURSE_DATA";

    private static WeakReference<CourseDataUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;
    private AuthUserData authUser;

    private CourseDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
        authUser = MyApplication.getInstance().authUser;
    }

    public synchronized static CourseDataUtils getInstance(){
        if (instance == null){
            instance = new WeakReference<>(new CourseDataUtils());
        }
        return instance.get();
    }

    private String toValue(String value) {
        return "'" + value + "'";
    }

    private String toValue(int value) {
        return toValue(Integer.toString(value));
    }

    public void requestFetchCourseData(final int courseId, final CourseData courseData,
                                      final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql = "SELECT * FROM " + CourseData.TBL_NAME
                        + " WHERE "
                        + CourseData.COL_ID + "=" + toValue(courseId)
                        + " AND "
                        + COL_STATUS + "=" + STATUS_VALID
                        + ";";
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null && resultSet.next()) {
                        courseData.extractFromResultSet(resultSet);
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
                    isOk = TeacherDataUtils.getInstance().fetchTeachData(courseData.teacherId, teacherData);
                    if (isOk) {
                        courseData.teacherData = teacherData;
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
