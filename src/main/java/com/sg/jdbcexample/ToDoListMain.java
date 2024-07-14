/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.jdbcexample;

import com.mysql.cj.jdbc.MysqlDataSource;
import javax.sql.DataSource;
import java.sql.*;
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
            System.out.println();
        }
    }

    private static void addItem() throws SQLException {
        System.out.println("Add Item");
        System.out.println("What is the task?");
        String task = sc.nextLine();
        System.out.println("Any additional notes?");
        String note = sc.nextLine();

        try (Connection conn = ds.getConnection()) {
            String sql = "INSERT INTO todo (todo, note) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, task);
            stmt.setString(2, note);

            stmt.executeUpdate();
            System.out.println("Added successfully\n");
        }
    }

    private static void updateItem() throws SQLException {
        System.out.println("Enter the item ID to be updated:");
        String id = sc.nextLine();

        try (Connection conn = ds.getConnection()) {
            //Get the desired item to be updated & map to an object
            String sql = "SELECT * FROM todo WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);

            ResultSet rs = stmt.executeQuery();
            rs.next();

            ToDo todoObj = new ToDo();
            todoObj.setId(rs.getInt("id"));
            todoObj.setTodo(rs.getString("todo"));
            todoObj.setNote(rs.getString("note"));
            todoObj.setFinished(rs.getBoolean("finished"));

            //Get desired change from user
            boolean finished = false;
            while (!finished) {
                System.out.println();
                System.out.println("1. ToDo - " + todoObj.getTodo());
                System.out.println("2. Note - " + todoObj.getNote());
                System.out.println("3. Finished - " + todoObj.isFinished());
                System.out.println("Select an item to change or press enter when changes are complete");

                String choice = sc.nextLine();
                switch (choice) {
                    case "1":
                        System.out.println("Enter new ToDo:");
                        String todo = sc.nextLine();
                        todoObj.setTodo(todo);
                        break;
                    case "2":
                        System.out.println("Enter new Note:");
                        String note = sc.nextLine();
                        todoObj.setNote(note);
                        break;
                    case "3":
                        System.out.println("Toggling Finished to " + !todoObj.isFinished());
                        todoObj.setFinished(!todoObj.isFinished());
                        break;
                    default:
                        finished = true;
                        break;
                }
            }

            //Update item in database
            sql = "UPDATE todo SET todo = ?, note = ?, finished = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, todoObj.getTodo());
            stmt.setString(2, todoObj.getNote());
            stmt.setBoolean(3, todoObj.isFinished());
            stmt.setInt(4, todoObj.getId());

            stmt.executeUpdate();
            System.out.println("Updated successfully\n");
        }
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
