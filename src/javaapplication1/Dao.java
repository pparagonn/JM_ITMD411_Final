package javaapplication1;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
			while ((line = br.readLine()) != null) {
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
	public int updateRecords(int id) {
		System.out.println("Updating the Records...");
		try {
			statement = getConnection().createStatement();
			String sql = "UPDATE jmelc_tickets " + "SET ticket_description = NULL WHERE ticket_id = " + id;
			int count = statement.executeUpdate(sql);

			System.out.println(count + "Record(s) has/have been updated");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
	// continue coding for deleteRecords implementation
	public int deleteRecords(int ticketNum) {
		System.out.println("Deleting Records from data...");
		try {
			statement = connect.createStatement();
			String sql = "DELETE FROM jmelc_tickets " + "WHERE ticket_id = " + ticketNum;

			int input = JOptionPane.showConfirmDialog(null, "Are you sure you'd like to delete ticket #" + ticketNum + "?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (input == JOptionPane.YES_OPTION) {
				statement.executeUpdate(sql);
				System.out.println("The ticket #" + ticketNum + "is deleted");
			} else if (input == JOptionPane.NO_OPTION) {
				System.out.println("Record Not Deleted");
			} else if (input == JOptionPane.CLOSED_OPTION) {
				System.out.println("Nothing Deleted.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ticketNum;
	}
}