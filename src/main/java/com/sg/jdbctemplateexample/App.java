/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.jdbctemplateexample;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author kylerudy
 */
@SpringBootApplication
public class App implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbc;
    private static Scanner sc;

    public static void main(String args[]) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        sc = new Scanner(System.in);

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
                    default:
                        System.out.println("I don't understand");
                }
            } catch (Exception ex) {
                System.out.println("Error communicating with database");
                System.out.println(ex.getMessage());
                System.exit(0);
            }

        } while (true);
    }

    private void displayList() throws SQLException {
        String sql = "SELECT * FROM todo";
        List<ToDo> todos = jdbc.query(sql, new ToDoMapper());

        for (ToDo td : todos) {
            System.out.printf("%s: %s -- %s -- %s\n",
                    td.getId(),
                    td.getTodo(),
                    td.getNote(),
                    td.isFinished());
        }
        System.out.println();
    }

    private void addItem() throws SQLException {
        System.out.println("Add Item");
        System.out.println("What is the task?");
        String task = sc.nextLine();
        System.out.println("Any additional notes?");
        String note = sc.nextLine();

        String sql = "INSERT INTO todo(todo, note) VALUES(?,?)";
        jdbc.update(sql, task, note);
        System.out.println("Added successfully\n");
    }

    private void updateItem() throws SQLException {
        System.out.println("Enter the item ID to be updated:");
        String id = sc.nextLine();

        String sql = "SELECT * FROM todo WHERE id = ?";
        ToDo todoObj = jdbc.queryForObject(sql, new ToDoMapper(), id);

        //Allow user to update multiple items
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

        sql = "UPDATE todo SET todo = ?, note = ?, finished = ? WHERE id = ?";
        jdbc.update(sql, todoObj.getTodo(), todoObj.getNote(), todoObj.isFinished(), todoObj.getId());
        System.out.println("Updated successfully\n");
    }

    private void removeItem() throws SQLException {
        System.out.println("Enter the item ID to be deleted:");
        String id = sc.nextLine();

        String sql = "DELETE FROM todo WHERE id = ?";
        jdbc.update(sql, id);
        System.out.println("Deleted successfully\n");
    }

    private static final class ToDoMapper implements RowMapper<ToDo> {
        @Override
        public ToDo mapRow(ResultSet rs, int i) throws SQLException {
            ToDo td = new ToDo();
            td.setId(rs.getInt("id"));
            td.setTodo(rs.getString("todo"));
            td.setNote(rs.getString("note"));
            td.setFinished(rs.getBoolean("finished"));
            return td;
        }
    }
}


