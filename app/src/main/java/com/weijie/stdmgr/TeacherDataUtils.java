package com.weijie.stdmgr;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TeacherDataUtils extends DBHandlerService {
    final public static String TAG_FETCH_CLASS_DATA = "TAG_FETCH_CLASS_DATA";

    private static WeakReference<TeacherDataUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;
    private AuthUserData authUser;

    private TeacherDataUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
        authUser = MyApplication.getInstance().authUser;
    }

    public synchronized static TeacherDataUtils getInstance(){
        if (instance == null){
            instance = new WeakReference<>(new TeacherDataUtils());
        }
        return instance.get();
    }

    private String toValue(String value) {
        return "'" + value + "'";
    }

    private String toValue(int value) {
        return toValue(Integer.toString(value));
    }

    public boolean fetchTeachData(int teacherId, TeacherData teacherData) {
        String sql = "SELECT * FROM " + TeacherData.TBL_NAME
                + " WHERE "
                + TeacherData.COL_ID + "=" + toValue(teacherId)
                + " AND "
                + COL_STATUS + "=" + STATUS_VALID
                + ";";
        boolean isOk = true;

        try {
            Statement statement = jdbcMgrUtils.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet != null && resultSet.next()) {
                teacherData.extractFromResultSet(resultSet);
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
}
