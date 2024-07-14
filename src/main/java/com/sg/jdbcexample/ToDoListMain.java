/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.jdbcexample;

import com.mysql.cj.jdbc.MysqlDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 *
 * @author kylerudy
 */
public class ToDoListMain {

    private static Scanner sc;
    private static DataSource ds;

    public static void main(String[] args) {

        sc = new Scanner(System.in);

        try {
            ds = getDataSource();
        }
        catch(SQLException e) {
            System.out.println("Error connecting to database");
            System.out.println(e.getMessage());
            System.exit(0);
        }

        do {
            System.out.println("To-Do List");
            System.out.println("1. Display List");
            System.out.println("2. Add Item");
            System.out.println("3. Update Item");
            System.out.println("4. Remove Item");
            System.out.println("5. Exit");

            System.out.println("Enter an option:");
            String option = sc.nextLine();
            try {
                switch (option) {
                    case "1":
                        displayList();
                        break;
                    case "2":
                        addItem();
                        break;
                    case "3":
                        updateItem();
                        break;
                    case "4":
                        removeItem();
                        break;
                    case "5":
                        System.out.println("Exiting");
                        System.exit(0);
                }
            } catch (SQLException ex) {
                System.out.println("Error communicating with database");
                System.out.println(ex.getMessage());
                System.exit(0);
            }

        } while (true);
    }

    private static void displayList() throws SQLException {
        try (Connection conn = ds.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM todo");
            while (rs.next()) {
                System.out.printf("%s: %s -- %s -- %s\n",
                        rs.getString("id"),
                        rs.getString("todo"),
                        rs.getString("note"),
                        rs.getString("finished"));
            }
        }
    }

    private static void addItem() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void updateItem() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void removeItem() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static DataSource getDataSource() throws SQLException {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("todoDB");
        ds.setUser("jdbc-practice");
        ds.setPassword("jdbc-practice");
        ds.setUseSSL(false);
        ds.setAllowPublicKeyRetrieval(true);

        return ds;
    }
}
