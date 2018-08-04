package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AuthUserDataUtils extends DBHandlerService{
    final private static String TBL_STUDENT_REGISTATION = "student_registration";
    final private static String TBL_TEACHER_REGISTATION = "teacher_registration";
    final private static String TBL_USER_LOGIN           = "user_login";

    final public static String TAG_LOGIN                = "TAG_LOGIN";
    final public static String TAG_REGISTRATION         = "TAG_REGISTRATION";
    final public static String TAG_CHECK_NAME_VALID     = "TAG_CHECK_NAME_VALID";
    final public static String TAG_CHECK_INVATION_VALID = "TAG_CHECK_INVATION_VALID";

    private static WeakReference<AuthUserDataUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;
    private AuthUserData authUser;

    private AuthUserDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
        authUser = MyApplication.getInstance().authUser;
    }

    public synchronized static AuthUserDataUtils getInstance(){
        if (instance == null || instance.get() == null){
            instance = new WeakReference<>(new AuthUserDataUtils());
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
                            + " WHERE "
                            + AuthUserData.COL_NAME  + "=" + toValue(name)
                            + ";";
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

    private int checkInvitation(final int unifiedCode, final String regCode,
                                    final String genre) {
        String sql;
        boolean isOk = true;
        int unifiedID = -1;

        try {
            Statement statement = jdbcMgrUtils.createStatement();

            if (genre.equals(AuthUserData.GENRE_STUDENT)) {
                sql = "SELECT * FROM " + TBL_STUDENT_REGISTATION
                        + " WHERE "
                        + StudentData.COL_CODE  + "=" + toValue(unifiedCode)
                        + " AND "
                        + StudentData.COL_REG_CODE  + "=" + toValue(regCode)
                        + " AND "
                        + COL_STATUS + "=" + STATUS_VALID
                        + ";";
                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet != null) {
                    if(resultSet.next()) {
                        StudentData studentData = new StudentData();
                        studentData.extractFromResultSet(resultSet);
                        unifiedID = studentData.id;
                    }
                    else {
                        isOk = false;
                    }
                }
                else {
                    isOk = false;
                }
            }
            else {
                sql = "SELECT * FROM " + TBL_TEACHER_REGISTATION
                        + " WHERE "
                        + TeacherData.COL_CODE  + "=" + toValue(unifiedCode)
                        + " AND "
                        + TeacherData.COL_REG_CODE  + "=" + toValue(regCode)
                        + " AND "
                        + COL_STATUS + "=" + STATUS_VALID
                        + ";";
                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet != null) {
                    if (resultSet.next()) {
                        TeacherData teacherData = new TeacherData();
                        teacherData.extractFromResultSet(resultSet);
                        unifiedID = teacherData.id;
                    }
                    else {
                        isOk = false;
                    }
                }
                else {
                    isOk = false;
                }
            }
            statement.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
            isOk = false;
        }

        return unifiedID;
    }

    public void requestCheckInviationValid(final int unifiedCode, final String regCode, final String genre,
                                           final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean isOk;
                int unifiedId = checkInvitation(unifiedCode, regCode, genre);
                if (unifiedId > 0){
                    isOk = true;
                }
                else {
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

    public void requestRegistration(final int unifiedCode, final String regCode, final String genre,
                            final String name, final String password,
                            final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    Statement statement;
                    ResultSet resultSet;
                    int studend_id = -1;
                    int teacher_id = -1;

                    sql = "START TRANSACTION;";
                    statement = jdbcMgrUtils.createStatement();
                    resultSet = statement.executeQuery(sql);
                    if (resultSet == null) {
                        isOk = false;
                    }

                    int unifiedId = checkInvitation(unifiedCode, regCode, genre);
                    if (unifiedId > 0) {
                        if (genre.equals(AuthUserData.GENRE_STUDENT)) {
                            studend_id = unifiedId;
                        }
                        else {
                            teacher_id = unifiedId;
                        }
                    }
                    else {
                        isOk = false;
                    }

                    if (isOk) {
                        sql = "INSERT " + TBL_USER_LOGIN
                                + " SET "
                                + AuthUserData.COL_NAME     + "=" + toValue(name)
                                + ","
                                + AuthUserData.COL_PASSWORD + "=" + toValue(password)
                                + ","
                                + AuthUserData.COL_GENRE    + "=" + toValue(genre)
                                + ","
                                + AuthUserData.COL_STUDENT_ID + "=" + toValue(studend_id)
                                + ","
                                + AuthUserData.COL_TEACHER_ID + "=" + toValue(teacher_id)
                                + ","
                                + COL_STATUS + "=" + STATUS_VALID
                                + ";";

                        int affect =statement.executeUpdate(sql);
                        if (affect == 1) {
                            authUser.name       = name;
                            authUser.password   = password;
                            authUser.genre      = genre;
                            authUser.studend_id = studend_id;
                            authUser.teacher_id = teacher_id;
                        }
                        else {
                            isOk = false;
                        }
                    }

                    if (isOk) {
                        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
                            sql = "UPDATE " + TBL_STUDENT_REGISTATION
                                    + " SET "
                                    + StudentData.COL_REG_CODE + "=" + AuthUserData.REG_CODE_INVALID
                                    + " WHERE "
                                    + StudentData.COL_REG_CODE + "=" + toValue(regCode)
                                    + " AND "
                                    + COL_STATUS + "=" + STATUS_VALID
                                    + ";";
                        }
                        else {
                            sql = "UPDATE " + TBL_TEACHER_REGISTATION
                                    + " SET "
                                    + TeacherData.COL_REG_CODE + "=" + AuthUserData.REG_CODE_INVALID
                                    + " WHERE "
                                    + TeacherData.COL_REG_CODE + "=" + toValue(regCode)
                                    + " AND "
                                    + COL_STATUS + "=" + STATUS_VALID
                                    + ";";
                        }
                        int affect =statement.executeUpdate(sql);
                        if (affect != 1) {
                            isOk = false;
                        }
                    }

                    if (isOk) {
                        sql = "COMMIT;";
                    }
                    else {
                        sql = "ROLLBACK;";
                    }
                    statement.executeQuery(sql);
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

    public void requestLogin(final String userName, final String password,
                             final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql = "SELECT * FROM " + TBL_USER_LOGIN
                        + " WHERE "
                        + AuthUserData.COL_NAME     + "=" + toValue(userName)
                        + " AND "
                        + AuthUserData.COL_PASSWORD + "=" + toValue(password)
                        + " AND "
                        + COL_STATUS + "=" + STATUS_VALID
                        + ";";

                boolean isOk = true;
                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null && resultSet.next()) {
                        authUser.extractFromResultSet(resultSet);
                    }
                    else {
                        isOk = false;
                    }

                    if (isOk) {
                        if (authUser.genre.equals(AuthUserData.GENRE_STUDENT)) {
                            sql = "SELECT * FROM " + TBL_STUDENT_REGISTATION
                                    + " WHERE "
                                    + StudentData.COL_ID     + "=" + toValue(authUser.studend_id)
                                    + ";";
                            resultSet = statement.executeQuery(sql);
                            if (resultSet != null && resultSet.next()) {
                                StudentData studentData = new StudentData();
                                studentData.extractFromResultSet(resultSet);
                                authUser.studentData = studentData;
                            }
                            else {
                                isOk = false;
                            }
                        }
                        else {
                            sql = "SELECT * FROM " + TBL_TEACHER_REGISTATION
                                    + " WHERE "
                                    + TeacherData.COL_ID     + "=" + toValue(authUser.teacher_id)
                                    + ";";
                            resultSet = statement.executeQuery(sql);
                            if (resultSet != null && resultSet.next()) {
                                TeacherData teacherData = new TeacherData();
                                teacherData.extractFromResultSet(resultSet);
                                authUser.teacherData = teacherData;
                            }
                            else {
                                isOk = false;
                            }
                        }
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
