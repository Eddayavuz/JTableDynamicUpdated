import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;

/* UPDATES 20.02.2025
 * The logic of the executeQuery has been updated:
 *   It used to get the column names from the form1.java as hardcoded,
 *   now it gets the column names automatically by retrieving metadata from the ResultSet,
 *   ensuring flexibility for different queries and database tables.

 * addColumn method
 *   This method dynamically adds a new column to the "actor" table in the database.
 *   It constructs and executes an ALTER TABLE query, ensuring database schema modifications can be made programmatically.
 * */



public class connect {
    private static final String URL = "jdbc:mysql://localhost:3306/sakila";
    private static final String USER = "root";
    private static final String PASSWORD = "0000";

    public static ArrayList<String[]> executeQuery(String query) {
        ArrayList<String[]> results = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // ResultSetMetaData object provides detailed information about the columns in the result set.
            // This includes column names, types, and other attributes like whether a column is nullable, its size, etc.
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // to add colums automatically
            for (int i = 1; i <= columnCount; i++) {
                form1.model.addColumn(metaData.getColumnName(i)); // model from form1.java
            }

            // to add rows.
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getString(i + 1);
                }
                results.add(row);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return results;
    }

    public static void addColumn(String columnName) {
        Connection connection = null;
        Statement stmt = null;
        String query = "ALTER TABLE sakila.actor ADD COLUMN " + columnName + " INT"; // hardcoded int

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(false);
            stmt = connection.createStatement();
            stmt.executeUpdate(query);
            connection.commit();
            System.out.println("Column '" + columnName + "' added successfully.");
        } catch (SQLException ex) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
                System.out.println("SQL Error: " + ex.getMessage());
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    public static void updateDatabase(String actorName, String[] columns, String[] newValues) {
        if (columns.length != newValues.length) {
            System.out.println("Error: Column count does not match value count.");
            return;
        }

        StringBuilder queryBuilder = new StringBuilder("UPDATE sakila.actor SET ");
        for (int i = 0; i < columns.length; i++) {
            queryBuilder.append(columns[i]).append(" = ?");
            if (i < columns.length - 1) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(" WHERE actor_id = ?");
        String query = queryBuilder.toString();

        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(query);

            for (int i = 0; i < newValues.length; i++) {
                pstmt.setString(i + 1, newValues[i]);
            }
            pstmt.setString(newValues.length + 1, actorName);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                connection.commit();
                System.out.println("Update committed successfully for actor: " + actorName);
            } else {
                connection.rollback();
                System.out.println("Update failed. Transaction rolled back.");
            }
        } catch (SQLException ex) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
                System.out.println("SQL Error: " + ex.getMessage());
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }
}
