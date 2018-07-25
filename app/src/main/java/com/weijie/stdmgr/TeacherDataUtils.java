package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by weijie on 2018/5/22.
 */
public class TeacherDataUtils extends DBHandlerService {
    final static String TAG_ID          = "TDU_";
    final static String TAG_FETCH_REGIST= TAG_ID + "TAG_FETCH_REGIST";
    final static String TAG_FETCH_LIST  = TAG_ID + "TAG_FETCH_LIST";

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

    public void requestFetchTeacherListOfStudent(final int studentId,
                                                 final ArrayList<TeacherData> arrayList,
                                                 final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    sql = "SELECT "
                            + TeacherData.getDomainColums()
                            + " FROM "
                            + TeacherData.TBL_NAME
                            + ","
                            + ClassData.TBL_NAME
                            + ","
                            + StudentData.TBL_NAME
                            + " WHERE "
                            + StudentData.toDomain(COL_ID) + "=" + toValue(studentId)
                            + " AND "
                            + ClassData.toDomain(COL_ID)
                            + " = "
                            + StudentData.toDomain(StudentData.COL_CLASS_ID)
                            + " AND "
                            + TeacherData.toDomain(COL_ID)
                            + "="
                            + ClassData.toDomain(ClassData.COL_TEACHER_ID)
                            + " UNION "
                            + " SELECT "
                            + TeacherData.getDomainColums()
                            + " FROM "
                            + TeacherData.TBL_NAME
                            + ","
                            + CourseDataUtils.TBL_STUDENT_COURSE
                            + ","
                            + CourseData.TBL_NAME
                            + ","
                            + StudentData.TBL_NAME
                            + " WHERE "
                            + StudentData.toDomain(COL_ID) + "=" + toValue(studentId)
                            + " AND "
                            + CourseDataUtils.toDomain(CourseDataUtils.COL_STUDENT_ID)
                            + " = "
                            + StudentData.toDomain(COL_ID)
                            + " AND "
                            + CourseDataUtils.toDomain(CourseDataUtils.COL_COURSE_ID)
                            + "="
                            + CourseData.toDomain(COL_ID)
                            + " AND "
                            + CourseData.toDomain(CourseData.COL_TEACHER_ID)
                            + "="
                            + TeacherData.toDomain(COL_ID)
                            + ";";

                    sql += ";";

                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            TeacherData teacherData = new TeacherData();
                            teacherData.extractFromResultSet(resultSet);
                            arrayList.add(teacherData);
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
