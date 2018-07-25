package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CourseDataUtils extends DBHandlerService {
    final static String TBL_STUDENT_COURSE = "student_course";
    final static String COL_STUDENT_ID  = "student_id";
    final static String COL_COURSE_ID   = "course_id";
    final static String COL_APPROVAL    = "approval";

    final static String TAG_FETCH_COURSE_DATA           = "TAG_FETCH_COURSE_DATA";
    final static String TAG_FETCH_COURSES_AS_STUDENT    = "TAG_COURSES_AS_STUDENT";
    final static String TAG_FETCH_COURSES_AS_TEACHER    = "TAG_FETCH_COURSES_AS_TEACHER";
    final static String TAG_FETCH_STUDENTS_OF_COURSE    = "TAG_FETCH_STUDENTS_OF_COURSE";

    private static WeakReference<CourseDataUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;

    private CourseDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
    }

    public synchronized static CourseDataUtils getInstance(){
        if (instance == null || instance.get() == null){
            instance = new WeakReference<>(new CourseDataUtils());
        }

        return instance.get();
    }

    static String toDomain(String col) {
        return TBL_STUDENT_COURSE + "." + col;
    }

    public boolean fetchCourseData( int courseId, CourseData courseData) {
        boolean isOk = true;

        try {
            Statement statement = jdbcMgrUtils.createStatement();
            String sql = "SELECT "
                    + CourseData.getDomainColums()
                    + ","
                    + CourseData.getJointDomainColums()
                    + " FROM "
                    + CourseData.TBL_NAME
                    + ","
                    + CourseData.getJointTables()
                    + " WHERE "
                    + CourseData.toDomain(COL_ID) + "=" + toValue(courseId)
                    + " AND "
                    + CourseData.toDomain(COL_STATUS) + "=" + STATUS_VALID
                    + " AND "
                    + CourseData.getJointCondition()
                    + ";";
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet != null && resultSet.next()) {
                courseData.extractFromResultSet(resultSet);
                courseData.extractJointFromResultSet(resultSet);
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

    public void requestFetchCourseData(final int courseId, final CourseData courseData,
                                      final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                isOk = fetchCourseData(courseId, courseData);

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

    private boolean fetchStudentsOfCourse(final int courseId,
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

    public void requestFetchStudentsOfCourse(final int courseId, final boolean isStrictApproval,
                                             final ArrayList<StudentData> arrayList,
                                             final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = fetchStudentsOfCourse(courseId, isStrictApproval, arrayList);

                if (isOk) {
                processHandler(handler, JdbcMgrUtils.DB_REQUEST_SUCCESS, tag);
            }
                else {
                processHandler(handler, JdbcMgrUtils.DB_REQUEST_FAILURE, tag);
            }
        }
    }).start();
}

    public void requestFetchCoursesAsStudent(final int studentId,
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
                            + CourseData.TBL_NAME
                            + ","
                            + CourseData.getJointTables()
                            + " WHERE "
                            + CourseData.toDomain(COL_STATUS) + "=" + STATUS_VALID
                            + " AND "
                            + CourseData.getJointCondition()
                            + " AND "
                            + CourseData.toDomain(COL_ID)
                            + " IN "
                            + " (SELECT "
                            + StudentDataUtils.COL_COURSE_ID
                            + " FROM "
                            + StudentDataUtils.TBL_STUDENT_COURSE
                            + " WHERE "
                            + StudentDataUtils.COL_STUDENT_ID + "=" + toValue(studentId)
                            + " )"
                            + ";";
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            CourseData courseData = new CourseData();
                            courseData.extractFromResultSet(resultSet);
                            courseData.extractJointFromResultSet(resultSet);
                            arrayList.add((courseData));
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

    public void requestFetchCoursesAsTeacher(final int teacherId,
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
                            + CourseData.TBL_NAME
                            + ","
                            + CourseData.getJointTables()
                            + " WHERE "
                            + CourseData.toDomain(CourseData.COL_TEACHER_ID) + "=" + toValue(teacherId)
                            + " AND "
                            + CourseData.toDomain(COL_STATUS) + "=" + STATUS_VALID
                            + " AND "
                            + CourseData.getJointCondition()
                            + ";";
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            CourseData courseData = new CourseData();
                            courseData.extractFromResultSet(resultSet);
                            courseData.extractJointFromResultSet(resultSet);
                            arrayList.add((courseData));
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
