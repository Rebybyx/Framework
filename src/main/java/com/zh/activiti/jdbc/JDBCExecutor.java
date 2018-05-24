package com.zh.activiti.jdbc;

import com.zh.activiti.util.ConfigUtil;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

/**
 * Created by Administrator on 2017/7/14.
 */
public class JDBCExecutor {
    private static String DRIVER = ConfigUtil.get("sqlServer.driverClassName");// "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private static String URL = ConfigUtil.get("sqlServer.url");//"jdbc:sqlserver://192.168.0.134:1433;DatabaseName=IMSDB";

    private static String USER = ConfigUtil.get("sqlServer.USER");//"sa";

    private static String PASS = ConfigUtil.get("sqlServer.PASS");//"sa!1";

    private Connection connection;

    private static JDBCExecutor jdbcExecutor;

    private Statement stmt;

    InputStream is = null;

    private JDBCExecutor() {
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USER, PASS);
            stmt = connection.createStatement();
        } catch (Exception e) {
            throw new JDBCException(e.getMessage());
        }
    }

    public static JDBCExecutor getJDBCExecutor() {
        if (jdbcExecutor == null) {
            jdbcExecutor = new JDBCExecutor();
        }
        return jdbcExecutor;
    }

    public String executeQuery(String sql) {
        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                is = rs.getBinaryStream("vqdImage");
                String uuid = UUID.randomUUID().toString();
//                fileupload.save(is, StaticUtil.FILEUSERID, uuid);
                return uuid;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
