package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AbsenceFormDataUtils extends DBHandlerService{
    final public static String TAG_FETCH_APPLIE_LIST= "TAG_FETCH_APPLIE_LIST";
    final public static String TAG_FETCH_ONE_APPLY  = "TAG_FETCH_ONE_APPLY";
    final public static String TAG_COMMIT_APPLY     = "TAG_COMMIT_APPLY";
    final public static String TAG_SIGN_APPLY       = "TAG_SIGN_APPLY";

    private static WeakReference<AbsenceFormDataUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;

    private AbsenceFormDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
    }

    public synchronized static AbsenceFormDataUtils getInstance(){
        if (instance == null || instance.get() == null){
            instance = new WeakReference<>(new AbsenceFormDataUtils());
        }

        return instance.get();
    }

    public void requestFetchApplieListFromStudent(final int studentId,
                                             final ArrayList<AbsenceFormData> arrayList,
                                             final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    sql = "SELECT "
                            + AbsenceFormData.getDomainColums()
                            + ","
                            + AbsenceFormData.getJointDomainColums()
                            + " FROM "
                            + AbsenceFormData.TBL_NAME
                            + ","
                            + AbsenceFormData.getJointTables()
                            + " WHERE "
                            + AbsenceFormData.toDomain(AbsenceFormData.COL_STUDENT_ID)
                            + "=" + toValue(studentId)
                            + " AND "
                            + AbsenceFormData.getJointCondition()
                            + " ORDER BY "
                            + AbsenceFormData.COL_ID
                            + " DESC "
                            + ";";
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            AbsenceFormData absenceFormData = new AbsenceFormData();
                            absenceFormData.extractFromResultSet(resultSet);
                            absenceFormData.extractJointFromResultSet(resultSet);
                            arrayList.add(absenceFormData);
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

    public void requestFetchApplyListToTeacher(final int teacherId,
                                             final ArrayList<AbsenceFormData> arrayList,
                                             final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    sql = "SELECT "
                            + AbsenceFormData.getDomainColums()
                            + ","
                            + AbsenceFormData.getJointDomainColums()
                            + " FROM "
                            + AbsenceFormData.TBL_NAME
                            + ","
                            + AbsenceFormData.getJointTables()
                            + " WHERE "
                            + AbsenceFormData.getJointCondition()
                            + " AND "
                            + " ("          //---1
                            + TeacherData.toDomainClass(COL_ID)
                            + "=" + toValue(teacherId)
                            + " OR "
                            + " ("          //---2
                            + AbsenceFormData.toDomain(AbsenceFormData.COL_CLASS_APPROVAL)
                            + "=" + toValue(AbsenceFormData.APPROVAL)
                            + " AND "
                            + TeacherData.toDomainCourse(COL_ID)
                            + "=" + toValue(teacherId)
                            + ")) "          //---2,1
                            + " ORDER BY "
                            + AbsenceFormData.toDomain(AbsenceFormData.COL_CLASS_APPROVAL)
                            + ","
                            + AbsenceFormData.toDomain(AbsenceFormData.COL_COURSE_APPROVAL)
                            + ";";
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            AbsenceFormData absenceFormData = new AbsenceFormData();
                            absenceFormData.extractFromResultSet(resultSet);
                            absenceFormData.extractJointFromResultSet(resultSet);
                            arrayList.add(absenceFormData);
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

    public void requestFetchApply(final int applyId, final AbsenceFormData absenceFormData,
                                  final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    sql = "SELECT "
                            + AbsenceFormData.getDomainColums()
                            + ","
                            + AbsenceFormData.getJointDomainColums()
                            + " FROM "
                            + AbsenceFormData.TBL_NAME
                            + ","
                            + AbsenceFormData.getJointTables()
                            + " WHERE "
                            + AbsenceFormData.toDomain(COL_ID) + "=" + applyId
                            + " AND "
                            + AbsenceFormData.getJointCondition()
                            + ";";
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        if (resultSet.next()) {
                            absenceFormData.extractFromResultSet(resultSet);
                            absenceFormData.extractJointFromResultSet(resultSet);
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
                    StudentData studentData = new StudentData();
                    isOk = StudentDataUtils.getInstance().
                            fetchStudentData(absenceFormData.studentId, studentData);
                    if (isOk) {
                        absenceFormData.studentData = studentData;
                    }
                }

                if (isOk) {
                    TeacherData teacherData = new TeacherData();
                    isOk = TeacherDataUtils.getInstance().
                            fetchTeachData(absenceFormData.classTeacherId, teacherData);
                    if (isOk) {
                        absenceFormData.classTeacher = teacherData;
                    }
                }

                if (isOk) {
                    CourseData courseData = new CourseData();
                    isOk = CourseDataUtils.getInstance().
                            fetchCourseData(absenceFormData.courseId, courseData);
                    if (isOk) {
                        absenceFormData.courseData = courseData;
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

    public void requestCommitApply(final AbsenceFormData absenceFormData,
                                   final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isOk = true;

                try {
                    String sql;
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet;

                    sql = "INSERT "
                            + AbsenceFormData.TBL_NAME
                            + " SET "
                            + absenceFormData.setColumsData()
                            + ";";
                    int affect = statement.executeUpdate(sql);
                    if (affect == 1) {
                        resultSet = statement.getGeneratedKeys();
                        if (resultSet.next()) {
                            absenceFormData.id = resultSet.getInt(1);
                        } else {
                            isOk = false;
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

    public void requestSignApply(final AbsenceFormData absenceFormData,
                                   final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isOk = true;

                try {
                    String sql;
                    Statement statement = jdbcMgrUtils.createStatement();

                    sql = "UPDATE "
                            + AbsenceFormData.TBL_NAME
                            + " SET "
                            + AbsenceFormData.COL_CLASS_APPROVAL + "=" + toValue(absenceFormData.classApproval)
                            +","
                            + AbsenceFormData.COL_COURSE_APPROVAL + "=" + toValue(absenceFormData.courseApproval)
                            + " WHERE "
                            + COL_ID + "=" + toValue(absenceFormData.id)
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
