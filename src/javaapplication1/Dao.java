package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dao {
    // instance fields
    static Connection connect = null;
    Statement statement = null;

    // constructor
    public Dao() {

    }

    public Connection getConnection() {
        // Setup the connection with the DB
        try {
            connect = DriverManager
                    .getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
                            + "&user=fp411&password=411");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return connect;
    }

    // CRUD implementation

    public void createTables() {
        // variables for SQL Query table creations
        final String createTicketsTable = "CREATE TABLE jmelc_tickets(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200))";
        final String createUsersTable = "CREATE TABLE jmelc_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";

        try {

            // execute queries to create tables

            statement = getConnection().createStatement();

            statement.executeUpdate(createTicketsTable);
            statement.executeUpdate(createUsersTable);
            System.out.println("Created tables in given database...");

            // end create table
            // close connection/statement object
            statement.close();
            connect.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // add users to user table
        addUsers();
    }

    public void addUsers() {
        // add list of users from userlist.csv file to users table

        // variables for SQL Query inserts
        String sql;

        Statement statement;
        BufferedReader br;
        List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

        // read data from file
        try {
            br = new BufferedReader(new FileReader(new File("./userlist.csv")));

            String line;
            while (( line = br.readLine() ) != null) {
                array.add(Arrays.asList(line.split(",")));
            }
        } catch (Exception e) {
            System.out.println("There was a problem loading the file");
        }

        try {

            // Setup the connection with the DB

            statement = getConnection().createStatement();

            // create loop to grab each array index containing a list of values
            // and PASS (insert) that data into your User table
            for (List<String> rowData : array) {

                sql = "insert into jmelc_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
                        + rowData.get(1) + "','" + rowData.get(2) + "');";
                statement.executeUpdate(sql);
            }
            System.out.println("Inserts completed in the given database...");

            // close statement object
            statement.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public int insertRecords(String ticketName, String ticketDesc) {
        int id = 0;
        try {
            statement = getConnection().createStatement();
            statement.executeUpdate("Insert into jmelc_tickets" + "(ticket_issuer, ticket_description) values(" + " '"
                    + ticketName + "','" + ticketDesc + "')", Statement.RETURN_GENERATED_KEYS);

            // retrieve ticket id number newly auto generated upon record insertion
            ResultSet resultSet = null;
            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                // retrieve first field in table
                id = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return id;

    }

    public ResultSet readRecords() {

        ResultSet results = null;
        try {
            statement = connect.createStatement();
            results = statement.executeQuery("SELECT * FROM jmelc_tickets");
            //connect.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return results;
    }

    // continue coding for updateRecords implementation
    public void updateRecords(String id, String oldContent, String newContent) {
        System.out.println("Updating the Records...");
        try {
            // Begin connection
            statement = getConnection().createStatement();

            // SQL to get updated
            String sql = "UPDATE jmelc_tickets " + "SET " + oldContent + " = '" + newContent + "' WHERE ticket_id = " + id;
            statement.executeUpdate(sql);

            // Report to console which ticket was updated
            System.out.println("Record #" + id + "was updated");

            // Close connection not needed (?)
            //statement.close();
            //connect.close();
        } catch (SQLException e) {
            System.out.println("The record could not be opened. Please check that record exists.");
            e.printStackTrace();
        }
    }

    // continue coding for deleteRecords implementation
    public void deleteRecords(String ticketNum) {
        System.out.println("Deleting Records from data...");
        try {
            // Begin connection
            statement = connect.createStatement();

            // SQL: Delete specific ticket from database
            String sql = "DELETE FROM jmelc_tickets " + "WHERE ticket_id = " + ticketNum;
            statement.executeUpdate(sql);

            // Report to console which ticket was deleted
            System.out.println("Record #" + ticketNum + "was updated");

        } catch (SQLException e) {
            System.out.println("Could not delete record.");
            e.printStackTrace();
        }
    }

    public void closeRecords(String ticketNum, String newStatus) {
        System.out.println("Closing the Record...");
        try {
            // Begin connection
            statement = getConnection().createStatement();

            // SQL: Modify status paramater for specific ticket
            String sql = "UPDATE tickets.jmelc_tickets SET status = '" + newStatus + "' WHERE ticket_id = " + ticketNum;
            statement.executeUpdate(sql);

            // Report to console which ticket was closed/opened
            System.out.println("Record #" + ticketNum + "is now set to" + newStatus);

            // Close connection not needed (?)
            // statement.close();
            // connect.close();
        } catch (SQLException e) {
            System.out.println("The record could not be closed/opened. Please check that record exists.");
            e.printStackTrace();
        }
    }
}
