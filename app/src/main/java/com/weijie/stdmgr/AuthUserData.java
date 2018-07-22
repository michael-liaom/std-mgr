package com.weijie.stdmgr;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;

import static android.content.Context.MODE_PRIVATE;

public class AuthUserData {
    final static int MIN_NAME_LEN       = 6;
    final static int MIN_PASSWORD_LEN   = 6;
    final static String COL_ID          = "id";
    final static String COL_NAME        = "name";
    final static String COL_PASSWORD    = "password";
    final static String COL_GENRE       = "genre";
    final static String COL_STUDENT_ID  = "student_id";
    final static String COL_TEACHER_ID  = "teacher_id";

    final public static String GENRE_STUDENT    = "student";
    final public static String GENRE_TEACHER    = "teacher";
    final public static String REG_CODE_INVALID = "'-1'";

    final static String HOST_SERVER     = "HOST_SERVER";

    int id;
    String name;
    String password;
    String genre;
    int studend_id;
    int teacher_id;
    StudentData studentData;
    TeacherData teacherData;
    Context context;

    String hostName;
    String defaultHost;

    AuthUserData(Context context) {
        defaultHost = JdbcMgrUtils.getInstance().hostAddress;
        this.context = context;
        restoreFromLocal();
    }

    public void reset() {
        name        = "";
        password    = "";
        backupToLocal();
    }

    public void backupToLocal(){
        SharedPreferences preferences = context.getSharedPreferences("STDMGR", MODE_PRIVATE);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(COL_NAME, name);
            editor.putString(COL_PASSWORD, password);
            editor.putString(HOST_SERVER, hostName);
            editor.commit();
        }

    }

    private void restoreFromLocal(){
        SharedPreferences preferences = context.getSharedPreferences("STDMGR", MODE_PRIVATE);
        if (preferences != null) {
            name        = preferences.getString(COL_NAME, "");
            password    = preferences.getString(COL_PASSWORD, "");
            hostName    = preferences.getString(HOST_SERVER, defaultHost);
        }
    }

    public void extractFromResultSet(ResultSet resultSet) throws SQLException {
        id = resultSet.getInt(AuthUserData.COL_ID);
        name = resultSet.getString(AuthUserData.COL_NAME);
        genre = resultSet.getString(AuthUserData.COL_GENRE);
        studend_id = resultSet.getInt(AuthUserData.COL_STUDENT_ID);
        teacher_id = resultSet.getInt(AuthUserData.COL_TEACHER_ID);
    }
}
