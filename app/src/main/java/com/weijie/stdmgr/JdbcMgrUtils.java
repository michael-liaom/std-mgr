package com.weijie.stdmgr;

import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class DBHandlerService {
    final public static String COL_STATUS       = "status";
    final public static String STATUS_VALID      = "'1'";

    protected void processHandler(Handler handler, int what, String tag) {
        if (handler != null) {
            Message msg = Message.obtain();
            msg.what = what;
            msg.obj = tag;
            handler.sendMessage(msg);
        }
    }
}

public class JdbcMgrUtils extends DBHandlerService{
    final public static int DB_REQUEST_SUCCESS  = 1;
    final public static int DB_REQUEST_FAILURE  = 0;
    final public static String TAG_DB_CONNECT   = "TAG_CONNECT_DB";
    static private WeakReference<JdbcMgrUtils> instance = null;

    private String hostAddress;
    private String userName;
    private String password;
    private String dataBaseName;
    private Connection connection;
    //private Statement statement;
    private boolean isTringConnection;

    private JdbcMgrUtils() {
        //databaseMgrUtils.connect("192.168.0.104");
        //databaseMgrUtils.connect("192.168.136.1");
        //databaseMgrUtils.connect("192.168.80.1");

        //hostAddress     = "192.168.80.1";
        //hostAddress     = "192.168.136.1";
        //hostAddress     = "192.168.0.104";
        hostAddress     = "10.0.2.2";   //for simulator
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

    public synchronized static JdbcMgrUtils getInstance(){
        if (instance == null){
            instance = new WeakReference<>(new JdbcMgrUtils());
        }
        return instance.get();
    }


    public boolean connect(final Handler handler, final String tag) {
        if (!isTringConnection) {
            isTringConnection = true;
            if (connection == null) {
                //String stringUrl = "jdbc:mysql://" + hostAddress + ":3306/" + dataBaseName
                //        + "/?autoReconnect=true&failOverReadOnly=false&useSSL=false";
                final String stringUrl = "jdbc:mysql://" + hostAddress + ":3306/" + dataBaseName;
//                    + "?autoReconnect=true&useSSL=false";

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Class.forName("com.mysql.jdbc.Driver");// 加载驱动程序
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        try {
                            connection = DriverManager.getConnection(stringUrl, userName, password);
                            //connection = DriverManager.getConnection(stringUrl);
                            //statement = connection.createStatement();//创建Statement
                            Log.d(this.toString(), "connect successed!");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        if (connection != null) {
                            processHandler(handler, DB_REQUEST_SUCCESS, tag);
                        }
                        else{
                            processHandler(handler, DB_REQUEST_FAILURE, tag);
                        }
                        isTringConnection = false;
                    }
                }).start();
            }
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

    public Statement createStatement() {
        if (connection != null) {
            try {
                return connection.createStatement();
            }
            catch (SQLException e) {
                return null;
            }
        }
        else{
            return null;
        }
    }

    public PreparedStatement preparedStatement(String sql) {
        if (connection != null) {
            try {
                return connection.prepareStatement(sql);
            }
            catch (SQLException e) {
                return null;
            }
        }
        else{
            return null;
        }
    }

    /*
    public ResultSet executeSQL(final String sql) {
        //String sql = "SELECT * FROM table_test";//查询表名为“table_test”的所有内容

        if (statement != null) {
            ResultSet resultSet = null;
            try {
                resultSet = statement.executeQuery(sql);
            } catch (SQLException e) {
                Log.e("executeSQL error:", e.toString());
                e.printStackTrace();
            }
            return resultSet;
        }
        else {
            return null;
        }
    }
    */
}
