package com.zh.activiti.jdbc;

/**
 * Created by Administrator on 2017/7/14.
 */
public class Main {
    public static void main(String[] args){

        String sql="SELECT pic FROM VDiagRealResult WHERE ID=51";
        System.out.println(JDBCExecutor.getJDBCExecutor().executeQuery(sql));

    }
}
