package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AuthUserMgrUtils extends DBHandlerService{
    final public static String TBL_STUDENT_REGISTATION = "student_registration";
    final public static String TBL_TEACHER_REGISTATION = "teacher_registration";
    final public static String TBL_USER_LOGIN           = "user_login";
    final public static String TAG_LOGIN                = "TAG_LOGIN";
    final public static String TAG_FETCH_STUDENT_REG    = "TAG_FETCH_STUDENT_REG";
    final public static String TAG_REGISTRATION         = "TAG_REGISTRATION";
    final public static String TAG_CHECK_NAME_VALID     = "TAG_CHECK_NAME_VALID";
    final public static String TAG_CHECK_INVATION_VALID = "TAG_CHECK_INVATION_VALID";

    private static WeakReference<AuthUserMgrUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;
    private AuthUserData authUser;

    private AuthUserMgrUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
        authUser = MyApplication.getInstance().authUser;
    }

    public synchronized static AuthUserMgrUtils getInstance(){
        if (instance == null){
            instance = new WeakReference<>(new AuthUserMgrUtils());
        }
        return instance.get();
    }

    public void requestCheckUserNameValid(final String name, final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                        sql = "SELECT * FROM " + TBL_USER_LOGIN
                                + " WHERE '"
                                + AuthUserData.COL_NAME  + "'='" + name
                                + "';";
                        Statement statement = jdbcMgrUtils.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet != null) {
                            if (resultSet.next()) {
                                isOk = false;
                            }
                        }
                        else {
                            isOk = false;
                        }


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

    public void requestCheckInviationValid(final String regCode, final String genre,
                                           final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    int studend_id = -1;
                    int teacher_id = -1;
                    if (genre.equals(AuthUserData.GENRE_STUDENT)) {
                        sql = "SELECT * FROM " + TBL_STUDENT_REGISTATION
                                + " WHERE '"
                                + StudentData.COL_REG_CODE  + "'='" + regCode
                                + "' AND '"
                                + JdbcMgrUtils.COL_STATUS + "'='" + JdbcMgrUtils.STATUS_VALID
                                + "';";
                        Statement statement = jdbcMgrUtils.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet == null) {
                            if(!resultSet.next()) {
                                isOk = false;
                            }
                        }
                        else {
                            isOk = false;
                        }
                    }
                    else {
                        sql = "SELECT * FROM " + TBL_TEACHER_REGISTATION
                                + " WHERE '"
                                + TeacherData.COL_REG_CODE  + "'='" + regCode
                                + "' AND '"
                                + JdbcMgrUtils.COL_STATUS + "'='" + JdbcMgrUtils.STATUS_VALID
                                + "';";
                        Statement statement = jdbcMgrUtils.createStatement();
                        ResultSet resultSet = statement.executeQuery(sql);
                        if (resultSet != null) {
                            if (!resultSet.next()) {
                                isOk = false;
                            }
                        }
                        else {
                            isOk = false;
                        }

                    }

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

    public void requestRegistration(final String regCode, final String genre,
                            final String name, final String password,
                            final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    int studend_id = -1;
                    int teacher_id = -1;

                    sql = "START TRANSACTION;";
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet == null) {
                        isOk = false;
                    }

                    if (isOk) {
                        if (genre.equals(AuthUserData.GENRE_STUDENT)) {
                            sql = "SELECT * FROM " + TBL_STUDENT_REGISTATION
                                    + " WHERE '"
                                    + StudentData.COL_REG_CODE + "'='" + regCode
                                    + "' AND '"
                                    + JdbcMgrUtils.COL_STATUS + "'='" + JdbcMgrUtils.STATUS_VALID
                                    + "';";
                            statement = jdbcMgrUtils.createStatement();
                            resultSet = statement.executeQuery(sql);
                            if (resultSet != null && resultSet.next()) {
                                StudentData studentData = new StudentData();
                                studentData.extractFromResultSet(resultSet);
                                authUser.studentData = studentData;
                                studend_id = studentData.id;
                            } else {
                                isOk = false;
                            }
                        } else {
                            sql = "SELECT * " + TBL_TEACHER_REGISTATION
                                    + " WHERE '"
                                    + TeacherData.COL_REG_CODE + "'='" + regCode
                                    + "' AND '"
                                    + JdbcMgrUtils.COL_STATUS + "'='" + JdbcMgrUtils.STATUS_VALID
                                    + "';";
                            statement = jdbcMgrUtils.createStatement();
                            resultSet = statement.executeQuery(sql);
                            if (resultSet != null && resultSet.next()) {
                                TeacherData teacherData = new TeacherData();
                                teacherData.extractFromResultSet(resultSet);
                                authUser.teacherData = teacherData;
                                teacher_id = teacherData.id;
                            } else {
                                isOk = false;
                            }
                        }
                    }

                    if (isOk) {
                        sql = "INSERT " + TBL_USER_LOGIN
                                + " SET '"
                                + AuthUserData.COL_NAME     + "'='" + name
                                + "','"
                                + AuthUserData.COL_PASSWORD + "'='" + password
                                + "','"
                                + AuthUserData.COL_GENRE    + "'='" + genre
                                + "','"
                                + AuthUserData.COL_STUDENT_ID + "'='" + studend_id
                                + "','"
                                + AuthUserData.COL_TEACHER_ID + "'='" + teacher_id
                                + "','"
                                + JdbcMgrUtils.COL_STATUS + "'='" + JdbcMgrUtils.STATUS_VALID
                                + "';";

                        statement = jdbcMgrUtils.createStatement();
                        resultSet = statement.executeQuery(sql);
                        if (resultSet != null) {
                            authUser.name       = name;
                            authUser.password   = password;
                            authUser.genre      = genre;
                            authUser.studend_id = studend_id;
                            authUser.teacher_id = teacher_id;
                        }
                        else {
                            isOk = false;
                        }
                        statement.close();
                    }

                    if (isOk) {
                        sql = "COMMIT;";
                    }
                    else {
                        sql = "ROLLBACK;";
                    }
                    statement = jdbcMgrUtils.createStatement();
                    statement.executeQuery(sql);
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

    public void requestLogin(final String userName, final String password,
                             final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql = "SELECT * FROM " + TBL_USER_LOGIN
                        + " WHERE '"
                        + AuthUserData.COL_NAME     + "'='" + userName
                        + "' AND '"
                        + AuthUserData.COL_PASSWORD + "'='" + password
                        + "' AND '"
                        + JdbcMgrUtils.COL_STATUS + "'='" + JdbcMgrUtils.STATUS_VALID
                        + "';";

                boolean isOk = true;
                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null && resultSet.next()) {
                            authUser.id = resultSet.getInt(AuthUserData.COL_ID);
                            authUser.name = resultSet.getString(AuthUserData.COL_NAME);
                            authUser.genre = resultSet.getString(AuthUserData.COL_GENRE);
                            authUser.studend_id = resultSet.getInt(AuthUserData.COL_STUDENT_ID);
                            authUser.teacher_id = resultSet.getInt(AuthUserData.COL_TEACHER_ID);
                    }
                    else {
                        isOk = false;
                    }
                    statement.close();

                    if (isOk) {
                        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
                            sql = "SELECT * FROM " + TBL_STUDENT_REGISTATION
                                    + " WHERE '"
                                    + StudentData.COL_ID     + "'='" + authUser.studend_id
                                    + "';";
                            resultSet = statement.executeQuery(sql);
                            if (resultSet != null && resultSet.next()) {
                                authUser.studentData.extractFromResultSet(resultSet);
                            }
                            else {
                                isOk = false;
                            }
                            statement.close();
                        }
                        else {
                            sql = "SELECT * FROM " + TBL_TEACHER_REGISTATION
                                    + " WHERE '"
                                    + TeacherData.COL_ID     + "'='" + authUser.teacher_id
                                    + "';";
                            resultSet = statement.executeQuery(sql);
                            if (resultSet != null && resultSet.next()) {
                                authUser.teacherData.extractFromResultSet(resultSet);
                            }
                            else {
                                isOk = false;
                            }
                            statement.close();
                        }
                    }
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

    public void requestFetchStudendRegistration(final int studentId, final StudentData studentData,
                                                final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql = "SELECT * FROM " + TBL_STUDENT_REGISTATION
                        + " WHERE '"
                        + StudentData.COL_ID + "'='" + Integer.toString(studentId)
                        + "' AND '"
                        + JdbcMgrUtils.COL_STATUS + "'='" + JdbcMgrUtils.STATUS_VALID
                        + "';";
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

    public void requstUpdateStudentRegistration(final StudentData studentData,
                                                final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String sql="UPDATE " + TBL_STUDENT_REGISTATION
                        + " SET '"
                        + StudentData.COL_NAME      + "'='" + studentData.name
                        + "',"
                        + StudentData.COL_CLASS_ID  + "'='" + Integer.toString(studentData.class_id)
                        + "','"
                        + StudentData.COL_CODE      + "'='" + Integer.toString(studentData.code)
                        + "' WHERE '"
                        + StudentData.COL_ID        + "'='" + Integer.toString(studentData.id)
                        + "';";
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet == null) {
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

    public void requstAppendStudentRegistration(final StudentData studentData,
                                                final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String sql="INSERT " + TBL_STUDENT_REGISTATION
                        + " SET '"
                        + StudentData.COL_NAME      + "'='" + studentData.name
                        + "','"
                        + StudentData.COL_CLASS_ID  + "'='" + Integer.toString(studentData.class_id)
                        + "','"
                        + StudentData.COL_CODE      + "'='" + Integer.toString(studentData.code)
                        + "';";
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet == null) {
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
