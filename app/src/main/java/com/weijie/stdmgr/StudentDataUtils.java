package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class StudentDataUtils extends DBHandlerService {
    final static String TBL_STUDENT_COURSE = "student_course";
    final static String COL_ID  = "id";
    final static String COL_STUDENT_ID  = "student_id";
    final static String COL_COURSE_ID   = "course_id";
    final static String TAG_FETCH_STUDENT_REGISTRATION  = "TAG_FETCH_STUDENT_REGISTRATION";
    final static String TAG_FETCH_STUDENT_COURSE        = "TAG_FETCH_STUDENT_COURSE";

    private static WeakReference<StudentDataUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;
    private AuthUserData authUser;

    private StudentDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
        authUser = MyApplication.getInstance().authUser;
    }

    public synchronized static StudentDataUtils getInstance(){
        if (instance == null){
            instance = new WeakReference<>(new StudentDataUtils());
        }
        return instance.get();
    }

    private String toDomain(String col) {
        return TBL_STUDENT_COURSE + "." + col;
    }

    private String toValue(String value) {
        return "'" + value + "'";
    }

    private String toValue(int value) {
        return toValue(Integer.toString(value));
    }

    public void requestFetchStudentRegistration(final int studentId, final StudentData studentData,
                                       final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql = "SELECT * FROM " + CourseData.TBL_NAME
                        + " WHERE "
                        + StudentData.COL_ID + "=" + toValue(studentId)
                        + " AND "
                        + COL_STATUS + "=" + STATUS_VALID
                        + ";";
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null && resultSet.next()) {
                        studentData.extractFromResultSet(resultSet);
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

    public void requestFetchStudentCourseData(final int studentId, final ArrayList<CourseData> arrayList,
                                              final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql = "SELECT "
                        + CourseData.toDomain(CourseData.COL_ID) + ","
                        + CourseData.toDomain(CourseData.COL_NAME) + ","
                        + CourseData.toDomain(CourseData.COL_CODE) + ","
                        + CourseData.toDomain(CourseData.COL_TEACHER_ID) + ","
                        + TeacherData.toDomainAs(TeacherData.COL_NAME)
                        + " FROM "
                        + TBL_STUDENT_COURSE
                        + " , "
                        + CourseData.TBL_NAME
                        + " , "
                        + TeacherData.TBL_NAME
                        + " WHERE "
                        + COL_STUDENT_ID + "=" + toValue(studentId)
                        + " AND "
                        + COL_COURSE_ID + "=" + CourseData.toDomain(CourseData.COL_ID)
                        + " AND "
                        + CourseData.COL_TEACHER_ID + "=" + TeacherData.toDomain(TeacherData.COL_ID)
                        + ";";
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            CourseData courseData = new CourseData();
                            courseData.extractFromResultSet(resultSet);
                            courseData.extractAsFromResultSet(resultSet);
                            arrayList.add(courseData);
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
