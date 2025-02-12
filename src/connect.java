import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;

                /*
                !!!!!UPDATES!!!!!
                Added updateDatabase Method to update a specific cell in the database.

                Parameters:
                clientName: The unique identifier for the row to update.
                columnName: The column to update.
                newValue: The new value to set in the specified cell.

                SQL Query:
                UPDATE SimpleCompany.clients SET columnName = ? WHERE ClientName = ?

                Features:
                Uses PreparedStatement for safe and efficient execution.
                Handles dynamic column names and values.
                Includes error handling for SQL exceptions.
                */

public class connect {
    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/SimpleCompany"; // Path to the database
    private static final String USER = "root"; // Database username
    private static final String PASSWORD = "root1234!"; // Database password

    // Method for executing ANY QUERY. (see the parameters list)
    public static ArrayList<String[]> executeQuery(String query, String column1, String column2) {
        ArrayList<String[]> results = new ArrayList<>(); // ArrayList to hold the results of the query.

        // Try-with-resources block to automatically close resources (Connection, Statement, ResultSet)
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD); // Step 1: Establish connection
             PreparedStatement pstmt = connection.prepareStatement(query); // Step 2: Create a statement
             ResultSet rs = pstmt.executeQuery(query)) { // Step 3: Execute the query

            // Loop through each row in the ResultSet
            while (rs.next()) {
                // Create a new row to store data for this record
                String[] row = new String[2]; // Assumes that each row has two columns to store.
                // Get the data for the first column (column1) and store it in the row
                row[0] = rs.getString(column1);
                // Get the data for the second column (column2) and store it in the row
                row[1] = rs.getString(column2);
                // Add this row to the results list
                results.add(row);
            }
        } catch (SQLException e) {
            // If there's an exception (e.g., a connection issue or query issue), print the error message.
            System.out.println("SQL Error: " + e.getMessage());
        }
        // Return the results ArrayList containing all rows from the result set
        return results;
    }

    // Method to update a specific cell in the database
    public static void updateDatabase(String clientName, String columnName, String newValue) {
        // SQL query to update the database. Uses placeholders (?) for dynamic values.
        String query = "UPDATE SimpleCompany.clients SET " + columnName + " = ? WHERE ClientName = ?";

        // Try-with-resources block to automatically close the connection and PreparedStatement (Reason: Performance: Faster for repeated queries, safer bc prevents SQL injection)
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // Set the first placeholder (?) to the new value
            pstmt.setString(1, newValue);
            // Set the second placeholder (?) to the client name (used to identify the row to update)
            pstmt.setString(2, clientName);

            // Execute the update query
            pstmt.executeUpdate();

            // Debugging: Print a success message
            System.out.println("Database updated successfully for client: " + clientName);
        } catch (SQLException ex) {
            // If there's an exception (e.g., a syntax error or connection issue), print the error message.
            System.out.println("SQL Error: " + ex.getMessage());
        }
    }
}