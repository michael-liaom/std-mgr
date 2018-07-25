package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class StudentDataUtils extends DBHandlerService {
    final static String TBL_STUDENT_COURSE = "student_course";
    final static String COL_STUDENT_ID  = "student_id";
    final static String COL_COURSE_ID   = "course_id";
    final static String COL_APPROVAL    = "approval";
    final static String TAG_FETCH_STUDENT_REGISTRATION  = "TAG_FETCH_STUDENT_REGISTRATION";
    final static String TAG_FETCH_STUDENT_COURSE        = "TAG_FETCH_STUDENT_COURSE";
    final static String TAG_FETCH_TEACHERS_OF_STUDENT   = "TAG_FETCH_TEACHERS_OF_STUDENT";
    final static String TAG_FETCH_STUDENT_LIST_OF_COURSE= "TAG_FETCH_STUDENT_LIST_OF_COURSE";
    final static String TAG_FETCH_STUDENT_LIST_OF_CLASS = "TAG_FETCH_STUDENT_LIST_OF_CLASS";

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

    public void requestFetchTeachersOfStudent(final int studentId,
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
                                             final boolean isStrictApproval,
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
                    + " AND "
                    + StudentData.getJointTables()
                    + " WHERE "
                    + StudentData.toDomain(StudentData.COL_CLASS_ID) + "=" + toValue(classId)
                    + " AND "
                    + StudentData.getJointCondition();
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

    public void requestFetchStudentListOfClass(final int classId, final boolean isStrictApproval,
                                                final ArrayList<StudentData> arrayList,
                                                final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = fetchStudentListOfClass(classId, isStrictApproval, arrayList);

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
