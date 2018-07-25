package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ClassDataUtils extends DBHandlerService {
    final public static String TAG_FETCH_CLASS_DATA         = "TAG_FETCH_CLASS_DATA";
    final public static String TAG_FETCH_LIST_OF_TEACHER    = "TAG_FETCH_LIST_OF_TEACHER";

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

    public boolean fetchClassData(int classId, ClassData classData) {
        String sql;
        boolean isOk = true;

        try {
            Statement statement = jdbcMgrUtils.createStatement();
            sql = "SELECT "
                    + ClassData.getDomainColums()
                    + ","
                    + ClassData.getJointDomainColums()
                    + " FROM "
                    + ClassData.TBL_NAME
                    + ","
                    + ClassData.getJointTables()
                    + " WHERE "
                    + ClassData.getJointCondition()
                    + " AND "
                    + ClassData.toDomain(COL_ID) + "=" + toValue(classId)
                    + " AND "
                    + ClassData.toDomain(COL_STATUS) + "=" + STATUS_VALID
                    + ";";
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet != null && resultSet.next()) {
                classData.extractFromResultSet(resultSet);
                classData.extractJointFromResultSet(resultSet);
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
            TeacherData teacherData = new TeacherData();
            isOk = TeacherDataUtils.getInstance().fetchTeachData(classData.teacherId, teacherData);
            if (isOk) {
                classData.teacherData = teacherData;
            }
        }

        return isOk;
    }

    public void requestFetchClassData(final int classId, final ClassData classData,
                                                final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isOk = fetchClassData(classId, classData);

                if (isOk) {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_SUCCESS, tag);
                }
                else {
                    processHandler(handler, JdbcMgrUtils.DB_REQUEST_FAILURE, tag);
                }
            }
        }).start();
    }

    public void requestFetchListOfTeacher(final int teacherId,
                                           final ArrayList<ClassData> arrayList,
                                           final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    Statement statement = jdbcMgrUtils.createStatement();
                    sql = "SELECT "
                            + ClassData.getDomainColums()
                            + ","
                            + ClassData.getJointDomainColums()
                            + " FROM "
                            + ClassData.TBL_NAME
                            + ","
                            + ClassData.getJointTables()
                            + " WHERE "
                            + ClassData.toDomain(ClassData.COL_TEACHER_ID) + "=" + toValue(teacherId)
                            + " AND "
                            + ClassData.getJointCondition()
                            + ";";

                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            ClassData classData = new ClassData();
                            classData.extractFromResultSet(resultSet);
                            classData.extractJointFromResultSet(resultSet);
                            arrayList.add(classData);
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
