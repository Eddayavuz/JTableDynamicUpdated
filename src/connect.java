import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;

/* UPDATES 17.02.2024
* Added more functionality to the updateDatabase method while ensuring database integrity and consistency with Transaction management:
* Transaction management ensures that multiple database operations execute as a single unit, maintaining data integrity.
* If all operations succeed, commit() permanently saves the changes; if any operation fails, rollback() undoes all changes,
* preventing partial updates and keeping the database consistent.
* */


public class connect {
    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/SimpleCompany"; // Path to the database
    private static final String USER = "root"; // Database username
    private static final String PASSWORD = "root1234!"; // Database password

    // Method for executing ANY QUERY. (see the parameters list)
    public static ArrayList<String[]> executeQuery(String query, String column1, String column2, String column3) {
        ArrayList<String[]> results = new ArrayList<>(); // ArrayList to hold the results of the query.

        // Try-with-resources block to automatically close resources (Connection, Statement, ResultSet)
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD); // Step 1: Establish connection
             PreparedStatement pstmt = connection.prepareStatement(query); // Step 2: Create a statement
             ResultSet rs = pstmt.executeQuery()) { // Step 3: Execute the query (No need to pass `query` again)

            // Loop through each row in the ResultSet
            while (rs.next()) {
                // Create a new row to store data for this record
                String[] row = new String[3]; // Assumes that each row has three columns to store.
                // Get the data for the first column (column1) and store it in the row
                row[0] = rs.getString(column1);
                // Get the data for the second column (column2) and store it in the row
                row[1] = rs.getString(column2);

                row[2] = rs.getString(column3);
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

    // Method to update MULTIPLE COLUMNS in the database using transaction management
    public static void updateDatabase(String clientName, String[] columns, String[] newValues) {
        if (columns.length != newValues.length) {
            System.out.println("Error: Column count does not match value count.");
            return;
        }

        // Construct the SQL query dynamically based on the number of columns to update
        StringBuilder queryBuilder = new StringBuilder("UPDATE SimpleCompany.clients SET ");
        for (int i = 0; i < columns.length; i++) {
            queryBuilder.append(columns[i]).append(" = ?");
            if (i < columns.length - 1) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(" WHERE ClientName = ?");

        String query = queryBuilder.toString(); // Convert StringBuilder to a SQL string

        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(false); // Start transaction

            pstmt = connection.prepareStatement(query);

            // Set the new values dynamically
            for (int i = 0; i < newValues.length; i++) {
                pstmt.setString(i + 1, newValues[i]);
            }
            pstmt.setString(newValues.length + 1, clientName); // Set the WHERE condition

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                connection.commit(); // Commit only if update was successful
                System.out.println("Update committed successfully for client: " + clientName);
            } else {
                connection.rollback(); // Rollback if no rows were affected
                System.out.println("Update failed. Transaction rolled back.");
            }

        } catch (SQLException ex) {
            try {
                if (connection != null) {
                    connection.rollback(); // Rollback on error
                }
                System.out.println("SQL Error: " + ex.getMessage());
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }

}
