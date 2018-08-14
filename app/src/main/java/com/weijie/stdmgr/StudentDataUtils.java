package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by weijie on 2018/5/24.
 */
public class StudentDataUtils extends DBHandlerService {
    final static String TBL_STUDENT_COURSE = "student_course";
    final static String COL_STUDENT_ID  = "student_id";
    final static String COL_COURSE_ID   = "course_id";
    final static String COL_APPROVAL    = "approval";

    final static String TAG_ID          = "SDU_";
    final static String TAG_FETCH_REGIST    = TAG_ID + "TAG_FETCH_REGIST";
    final static String TAG_FETCH_COURSE    = TAG_ID + "TAG_FETCH_COURSE";
    final static String TAG_FETCH_LIST      = TAG_ID + "TAG_FETCH_LIST";
    final static String TAG_COMMIT_DATA     = TAG_ID + "TAG_COMMIT_DATA";

    private static WeakReference<StudentDataUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;

    private StudentDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
    }

    public synchronized static StudentDataUtils getInstance(){
        if (instance == null || instance.get() == null){
            instance = new WeakReference<>(new StudentDataUtils());
        }

        return instance.get();
    }

    static String toDomain(String col) {
        return TBL_STUDENT_COURSE + "." + col;
    }

    public boolean fetchStudentData(int studentId, StudentData studentData) {
        String sql = "SELECT * FROM "
                + StudentData.TBL_NAME
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
            ClassData classData = new ClassData();
            isOk = ClassDataUtils.getInstance().fetchClassData(studentData.class_id, classData);
            if (isOk) {
                studentData.classData = classData;
            }
        }

        return  isOk;
    }

    public void requestFetchStudentRegistration(final int studentId, final StudentData studentData,
                                       final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isOk = fetchStudentData(studentId, studentData);

                if (isOk) {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_SUCCESS, tag);
                }
                else {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_FAILURE, tag);
                }
            }
        }).start();
    }

    public void requestFetchStudentCourseData(final int studentId, final boolean isStrictApproval,
                                              final ArrayList<CourseData> arrayList,
                                              final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    sql = "SELECT "
                            + CourseData.getDomainColums()
                            + ","
                            + CourseData.getJointDomainColums()
                            + " FROM "
                            + TBL_STUDENT_COURSE
                            + " , "
                            + CourseData.TBL_NAME
                            + " , "
                            + CourseData.getJointTables()
                            + " WHERE "
                            + COL_STUDENT_ID + "=" + toValue(studentId)
                            + " AND "
                            + COL_COURSE_ID + "=" + CourseData.toDomain(CourseData.COL_ID)
                            + " AND "
                            + CourseData.getJointCondition();
                    if (isStrictApproval) {
                        sql +=  " AND ";
                        sql += toDomain(COL_APPROVAL) + "=" + STATUS_VALID;
                    }
                    sql += ";";

                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            CourseData courseData = new CourseData();
                            courseData.extractFromResultSet(resultSet);
                            courseData.extractJointFromResultSet(resultSet);
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

    private boolean fetchStudentListOfCourse(final int courseId,
                                          final boolean isStrictApproval,
                                          final ArrayList<StudentData> arrayList) {
        String sql;
        boolean isOk = true;

        try {
            Statement statement = jdbcMgrUtils.createStatement();
            sql = "SELECT "
                    + StudentData.getDomainColums()
                    + " FROM "
                    + TBL_STUDENT_COURSE
                    + " , "
                    + StudentData.TBL_NAME
                    + " WHERE "
                    + toDomain(COL_COURSE_ID) + "=" + toValue(courseId)
                    + " AND "
                    + toDomain(COL_STUDENT_ID) + "=" + StudentData.toDomain(COL_ID);
            if (isStrictApproval) {
                sql +=  " AND ";
                sql += toDomain(COL_APPROVAL) + "=" + STATUS_VALID;
            }
            sql += ";";

            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet != null) {
                while (resultSet.next()) {
                    StudentData studentData = new StudentData();
                    studentData.extractFromResultSet(resultSet);
                    arrayList.add(studentData);
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

        return isOk;
    }

    public void requestFetchStudentListOfCourse(final int courseId, final boolean isStrictApproval,
                                             final ArrayList<StudentData> arrayList,
                                             final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = fetchStudentListOfCourse(courseId, isStrictApproval, arrayList);

                if (isOk) {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_SUCCESS, tag);
                }
                else {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_FAILURE, tag);
                }
            }
        }).start();
    }

    private boolean fetchStudentListOfClass(final int classId,
                                             final ArrayList<StudentData> arrayList) {
        String sql;
        boolean isOk = true;

        try {
            Statement statement = jdbcMgrUtils.createStatement();
            sql = "SELECT "
                    + StudentData.getDomainColums()
                    + ","
                    + StudentData.getJointDomainColums()
                    + " FROM "
                    + StudentData.TBL_NAME
                    + ","
                    + StudentData.getJointTables()
                    + " WHERE "
                    + StudentData.toDomain(StudentData.COL_CLASS_ID) + "=" + toValue(classId)
                    + " AND "
                    + StudentData.getJointCondition()
                    + ";";

            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet != null) {
                while (resultSet.next()) {
                    StudentData studentData = new StudentData();
                    studentData.extractFromResultSet(resultSet);
                    arrayList.add(studentData);
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

        return isOk;
    }

    public void requestFetchStudentListOfClass(final int classId,
                                                final ArrayList<StudentData> arrayList,
                                                final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = fetchStudentListOfClass(classId, arrayList);

                if (isOk) {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_SUCCESS, tag);
                }
                else {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_FAILURE, tag);
                }
            }
        }).start();
    }

    public void requestCommitData(final StudentData studentData,
                                  final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isOk = true;

                try {
                    String sql;
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet;

                    sql = "UPDATE "
                            + StudentData.TBL_NAME
                            + " SET "
                            + studentData.setColumsData()
                            + " WHERE "
                            + StudentData.COL_ID + "=" + toValue(studentData.id)
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
