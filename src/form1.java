import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/* UPDATES 17.02.2025
* Generalized update logic using connect.updateDatabase(clientName, columns, newValues),
* making it possible to update any column dynamically instead of being limited to a specific one.*/

public class form1 extends JFrame {
    // GUI components
    private JPanel panel1;
    private JTable table1;
    private JComboBox<String> comboBox1;

    private DefaultTableModel model; // Table model to manage data dynamically
    private ArrayList<String[]> clients; // List to store client data retrieved from the database
    // Variable to store the old value before editing


    // Constructor to initialize the GUI
    public form1() {
        setSize(700, 400);
        setContentPane(panel1);

        setVisible(true);

        // Initialize the table model with column names
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"ClientName", "ContactEmail", "PhoneNumber"}); // Updated the Column Names to match the database exactly.
        table1.setModel(model); // Attach model to table

        // Execute initial database query to fetch client data
        clients = connect.executeQuery("SELECT * FROM SimpleCompany.clients", "ClientName", "ContactEmail", "PhoneNumber");
        // Populate the table with the retrieved data
        updateTable();

        //ActionListener for the ComboBox (sorting options)
        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check which sorting option is selected
                if (comboBox1.getSelectedIndex() == 0) {
                    // Sort clients by Contact Email in ascending order
                    clients = connect.executeQuery("SELECT * FROM SimpleCompany.clients ORDER BY ClientName ASC", "ClientName", "ContactEmail", "PhoneNumber");
                } else if (comboBox1.getSelectedIndex() == 1) {
                    // Sort clients by Contact Email in descending order
                    clients = connect.executeQuery("SELECT * FROM SimpleCompany.clients ORDER BY ContactEmail DESC", "ClientName", "ContactEmail", "PhoneNumber");
                }
                updateTable(); // Refresh the table to display sorted data
            }
        });


        // Add a custom cell editor to capture the old value
        table1.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()) {
            private String oldValue; // Store the old value before editing

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                oldValue = (value != null) ? value.toString() : ""; // Capture the old value
                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }

            @Override
            public boolean stopCellEditing() {
                // Capture the new value entered by the user
                String newValue = getCellEditorValue().toString();

                // Get the row and column indices
                int row = table1.getEditingRow();
                int column = table1.getEditingColumn();

                // Get the name of the column being edited
                String columnName = table1.getColumnName(column);

                // Get the ClientName from the first column as an identifier
                String clientName = (String) table1.getValueAt(row, 0);

                // Only update if the value has changed
                if (!newValue.equals(oldValue)) {
                    // Call the updated updateDatabase method with a single column update
                    String[] columns = {columnName};
                    String[] newValues = {newValue};
                    connect.updateDatabase(clientName, columns, newValues);
                }

                return super.stopCellEditing();
            }
        });
    }

    private void updateTable () {
        model.setRowCount(0); // Clear all existing rows in the table
        for (String[] client : clients) {
            model.addRow(client); // Add each client's data as a new row
        }
    }

}