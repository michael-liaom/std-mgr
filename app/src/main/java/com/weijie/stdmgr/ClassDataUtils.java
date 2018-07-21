package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClassDataUtils extends DBHandlerService {
    final static String TBL_NAME        = "class_master";
    final static String COL_CLASS_ID    = "class_id";
    final static String COL_TEACHER_ID  = "teacher_id";
    final public static String TAG_FETCH_CLASS_DATA = "TAG_FETCH_CLASS_DATA";

    private static WeakReference<ClassDataUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;

    private ClassDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
    }

    public synchronized static ClassDataUtils getInstance(){
        if (instance == null || instance.get() == null){
            instance = new WeakReference<>(new ClassDataUtils());
        }

        return instance.get();
    }

    static String toDomain(String col) {
        return TBL_NAME + "." + col;
    }

    static String toDomainAs(String col) {
        return TBL_NAME + "." + col + " AS " + TBL_NAME + "_" + col;
    }

    static String getJointCol(String col) {
        return TBL_NAME + "_" + col;
    }

    public void requestFetchClassData(final int classId, final ClassData classData,
                                                final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    sql = "SELECT * FROM " + ClassData.TBL_NAME
                            + " WHERE "
                            + ClassData.COL_ID + "=" + toValue(classId)
                            + " AND "
                            + COL_STATUS + "=" + STATUS_VALID
                            + ";";
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null && resultSet.next()) {
                        classData.extractFromResultSet(resultSet);
                    }
                    else {
                        isOk = false;
                    }

                    if (isOk) {
                        sql = "SELECT "
                                + TeacherData.getDomainColums()
                                + " FROM "
                                + TBL_NAME
                                +","
                                + TeacherData.TBL_NAME
                                + " WHERE "
                                + toDomain(COL_CLASS_ID) + "=" +toValue(classId)
                                + " AND "
                                + toDomain(COL_TEACHER_ID) + "=" + TeacherData.toDomain(COL_ID)
                                +";";
                        resultSet = statement.executeQuery(sql);
                        if (resultSet != null) {
                            while (resultSet.next()) {
                                TeacherData teacherData = new TeacherData();
                                teacherData.extractFromResultSet(resultSet);
                                classData.materList.add(teacherData);
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
