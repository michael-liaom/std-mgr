package com.weijie.stdmgr;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AbsenceApplyUtils extends DBHandlerService{
    final public static String TAG_FETCH_ALLMY_APPLY    = "TAG_FETCH_STUDENT_REG";

    private static WeakReference<AbsenceApplyUtils> instance = null;

    private JdbcMgrUtils jdbcMgrUtils;
    private AuthUserData authUser;

    private AbsenceApplyUtils() {
        jdbcMgrUtils = JdbcMgrUtils.getInstance();
        authUser = MyApplication.getInstance().authUser;
    }

    public synchronized static AbsenceApplyUtils getInstance(){
        if (instance == null){
            instance = new WeakReference<>(new AbsenceApplyUtils());
        }
        return instance.get();
    }

    public void requestFetchAllMyApply(final ArrayList<AbsenceApplyData> arrayList,
                                       final Handler handler, final String tag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String sql;
                boolean isOk = true;

                try {
                    sql = "SELECT "
                            + AbsenceApplyData.TBL_NAME + "." + AbsenceApplyData.COL_ID
                            + ","
                            + AbsenceApplyData.TBL_NAME + "." + AbsenceApplyData.COL_BEGIN
                            + ","
                            + AbsenceApplyData.TBL_NAME + "." + AbsenceApplyData.COL_END
                            + ","
                            + AbsenceApplyData.TBL_NAME + "." + AbsenceApplyData.COL_TYPE
                            + ","
                            + AbsenceApplyData.TBL_NAME + "." + AbsenceApplyData.COL_CAUSE
                            + ","
                            + AbsenceApplyData.TBL_NAME + "." + AbsenceApplyData.COL_APPROVAL
                            + ","
                            + StudentData.TBL_NAME + "." + StudentData.COL_CODE
                            + " AS " + AbsenceApplyData.COL_AS_CODE
                            + ","
                            + StudentData.TBL_NAME + "." + StudentData.COL_NAME
                            + " AS " + AbsenceApplyData.COL_AS_NAME
                            + ","
                            + TeacherData.TBL_NAME + "." + TeacherData.COL_NAME
                            + " AS " + AbsenceApplyData.COL_AS_TEACHER
                            + " FROM " + AbsenceApplyData.TBL_NAME + "," + StudentData.TBL_NAME
                            + " WHERE "
                            + AbsenceApplyData.TBL_NAME + "." + AbsenceApplyData.COL_STUDENT_ID  + "='" + authUser.studend_id
                            + "' AND"
                            + StudentData.TBL_NAME + "." + StudentData.COL_ID  + "='" + authUser.studend_id
                            + "' ORDER BY "
                            + AbsenceApplyData.COL_ID
                            + " DESC "
                            + ";";
                    Statement statement = jdbcMgrUtils.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    if (resultSet != null) {
                        if (resultSet.next()) {
                            AbsenceApplyData absenceApplyData = new AbsenceApplyData();
                            absenceApplyData.extractFromResultSet(resultSet);
                            absenceApplyData.extractAsFromResultSet(resultSet);
                            arrayList.add(absenceApplyData);
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
