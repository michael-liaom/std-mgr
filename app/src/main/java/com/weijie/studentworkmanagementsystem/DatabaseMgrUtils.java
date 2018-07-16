package com.weijie.studentworkmanagementsystem;

import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class DatabaseRequestResult {
    String tag;
    ResultSet resultSet;
}

public class DatabaseMgrUtils {
    final public static int DB_REQUEST_SUCCESS = 1;
    final public static int DB_REQUEST_FAILURE = 0;
    static private WeakReference<DatabaseMgrUtils> instance = null;

    private String hostAddress;
    private String userName;
    private String password;
    private String dataBaseName;
    private Connection connection;
    private Statement statement;

    private DatabaseMgrUtils() {
        //databaseMgrUtils.connect("192.168.0.104");
        //databaseMgrUtils.connect("192.168.136.1");
        //databaseMgrUtils.connect("192.168.80.1");

        //hostAddress     = "192.168.80.1";
        //hostAddress     = "192.168.136.1";
        hostAddress     = "192.168.0.104";
        //hostAddress     = "10.0.2.2";   //for simulator
        dataBaseName    = "stdmgr";
        userName        = "stdmgr";
        password        = "1440706";

        StrictMode.ThreadPolicy threadPolicy
                = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(threadPolicy);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    public synchronized static DatabaseMgrUtils getInstance(){
        if (instance == null){
            instance = new WeakReference<>(new DatabaseMgrUtils());
        }
        return instance.get();
    }


    public boolean connect() {
        if (connection == null) {

            //String stringUrl = "jdbc:mysql://" + hostAddress + ":3306/" + dataBaseName
            //        + "/?autoReconnect=true&failOverReadOnly=false&useSSL=false";
            final String stringUrl = "jdbc:mysql://" + hostAddress + ":3306/"
                    + dataBaseName + "?autoReconnect=true&useSSL=false";


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Class.forName("com.mysql.jdbc.Driver");// 加载驱动程序
                    }
                    catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    try {
                        connection = DriverManager.getConnection(stringUrl, userName, password);
                        //connection = DriverManager.getConnection(stringUrl);
                        statement = connection.createStatement();//创建Statement
                        Log.d(this.toString(), "connect successed!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }}).start();
        }
        return  true;
    }

    public void close(){
        if (connection != null) {
            try {
                connection.close();
            }
            catch (SQLException e) {
                Log.e(this.toString(), e.toString());
                e.printStackTrace();
            }
            connection = null;
        }
    }

    public Statement getStatement() {
        return statement;
    }

    public boolean executeSQL(String sql, Handler handler, String tag) {
        //String sql = "SELECT * FROM table_test";//查询表名为“table_test”的所有内容

        if (statement != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ResultSet resultSet = statement.executeQuery(sql);
                    } catch (SQLException e) {
                        Log.e(this.toString(), e.toString());
                        e.printStackTrace();
                    }
                }
            }).start();
            return true;
        }
        else {
            return false;
        }
    }

    private void processHandler(Handler handler, ResultSet resultSet, String tag) {
        if (handler != null) {
            Message msg = Message.obtain();
            if (resultSet != null) {
                DatabaseRequestResult requestResult = new DatabaseRequestResult();

                msg.what = DB_REQUEST_SUCCESS;
                requestResult.resultSet = resultSet;
                if (tag != null) {
                    requestResult.tag = tag;
                }
                msg.obj = requestResult;
            }
            else {
                msg.what = DB_REQUEST_FAILURE;
            }
            handler.sendMessage(msg);
        }
    }
}
