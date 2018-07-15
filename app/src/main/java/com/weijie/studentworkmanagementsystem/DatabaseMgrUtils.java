package com.weijie.studentworkmanagementsystem;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseMgrUtils {
    static private WeakReference<DatabaseMgrUtils> instance = null;

    private String hostAddress;
    private String userName;
    private String password;
    private String dataBaseName;
    private Connection connection;
    private Statement statement;

    private DatabaseMgrUtils() {
        dataBaseName    = "stdmgr";
        userName        = "stdmgr";
        password        = "1440706";
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


    public boolean connect(String hostAddress) {
        if (connection == null) {
            this.hostAddress = hostAddress;

            String stringUrl = "jdbc:mysql://" + this.hostAddress + ":3306/" + dataBaseName;
            //+ "/?autoReconnect=true&failOverReadOnly=false";

            try {
                Class.forName("com.mysql.jdbc.Driver");// 加载驱动程序
                connection = DriverManager.getConnection(stringUrl, userName, password);
                statement = connection.createStatement();//创建Statement
                return true;
            } catch (ClassNotFoundException | SQLException e) {
                Log.e(this.toString(), stringUrl + " " + e.toString());
                e.printStackTrace();
                return false;
            }
        }
        else {
            return  true;
        }
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

    public ResultSet executeSQL(String sql) {
        //String sql = "SELECT * FROM table_test";//查询表名为“table_test”的所有内容

        try {
            Statement stmt = connection.createStatement();//创建Statement
            return stmt.executeQuery(sql);
        }
        catch (SQLException e) {
            Log.e(this.toString(), e.toString());
            e.printStackTrace();
            return  null;
        }
    }
}
