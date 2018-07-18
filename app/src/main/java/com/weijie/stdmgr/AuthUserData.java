package com.weijie.stdmgr;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;

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

    int id;
    String name;
    String password;
    String genre;
    int studend_id;
    int teacher_id;
    StudentData studentData;
    TeacherData teacherData;
    Context context;

    AuthUserData(Context context) {
        this.context = context;
        restoreFromLocal();
    }

    public void backupToLocal(){
        SharedPreferences preferences = context.getSharedPreferences("STDMGR", MODE_PRIVATE);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(COL_NAME, name);
            editor.putString(COL_PASSWORD, password);
            editor.commit();
        }

    }

    private void restoreFromLocal(){
        SharedPreferences preferences = context.getSharedPreferences("STDMGR", MODE_PRIVATE);
        if (preferences != null) {
            name        = preferences.getString(COL_NAME, "");
            password    = preferences.getString(COL_PASSWORD, "");
        }
    }
}
