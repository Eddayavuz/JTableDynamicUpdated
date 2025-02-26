import org.mindrot.jbcrypt.BCrypt;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;

/* UPDATES 24.02.2025
 * addActor method
 *   This method inserts a new actor into the "actor" table in the database.
 *   It uses a prepared statement to insert first name, last name, hashed password, and image as a binary stream.
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

    public static void addColumn(String columnName, String dataType) {
        Connection connection = null;
        Statement stmt = null;
        String query = "ALTER TABLE sakila.actor ADD COLUMN " + columnName + " " + dataType; // hardcoded int

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(false);
            stmt = connection.createStatement();
            stmt.executeUpdate(query);
            connection.commit();
            System.out.println("Column '" + columnName + " with datatype: " + dataType + "' added successfully.");
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

    public static void addActor(String firstName, String lastName, InputStream img, String hashPassword) {
        // SQL query for inserting actor data into the database
        String query = "INSERT INTO actor (first_name, last_name, img, password) VALUES (?, ?, ?, ?)";

// Declare variables for database connection and prepared statement
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            // Establish a connection to the database
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Disable auto-commit to manually handle transactions
            connection.setAutoCommit(false);

            // Prepare the SQL query
            pstmt = connection.prepareStatement(query);

            // Set the values for the query placeholders using the input data
            pstmt.setString(1, firstName);  // Set first name
            pstmt.setString(2, lastName);   // Set last name
            pstmt.setBinaryStream(3, img);  // Set the image as a binary stream
            pstmt.setString(4, hashPassword);  // Set the hashed password

            // Execute the query and get the number of rows affected
            int rowsAffected = pstmt.executeUpdate();

            // If the insertion is successful, commit the transaction
            if (rowsAffected > 0) {
                connection.commit();
                System.out.println("Insertion committed successfully for actor: " + firstName);
            } else {
                // If insertion fails, roll back the transaction
                connection.rollback();
                System.out.println("Insertion failed. Transaction rolled back.");
            }
        } catch (SQLException ex) {
            // Handle SQL exceptions
            try {
                if (connection != null) {
                    // If an error occurs, roll back the transaction
                    connection.rollback();
                }
                System.out.println("SQL Error: " + ex.getMessage());
            } catch (SQLException rollbackEx) {
                // Print stack trace if there is an error during rollback
                rollbackEx.printStackTrace();
            }
        }

    }

    public static User login(String id, String password) {
        User user = null;
        String query = "SELECT actor_id, first_name, img, password FROM sakila.actor WHERE actor_id = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query);
        ) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        // Get the image as a binary stream and store it
                        InputStream imgStream = rs.getBinaryStream("img");
                        user = new User(rs.getString("actor_id"), rs.getString("first_name"), imgStream);
                    }
                }
            }
            return user;
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return user;
    }
}
class User {
    private String actor_id;
    private String first_name;
    private InputStream img;  // Use InputStream for image data

    public User(String id, String first_name, InputStream img){
        this.actor_id = id;
        this.first_name = first_name;
        this.img = img;
    }

    public String getFirst_name(){
        return first_name;
    }


    public ImageIcon getImageIcon() {
        try {
            // Convert InputStream to BufferedImage
            BufferedImage image = ImageIO.read(img);
            // Return ImageIcon from the BufferedImage
            return new ImageIcon(image);
        } catch (IOException e) {
            System.out.println("Error loading image: " + e.getMessage());
            return null;
        }
    }
}

