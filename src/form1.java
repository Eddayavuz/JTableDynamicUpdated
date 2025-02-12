import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/*
!!!!!UPDATES!!!!!
Added a feature to update the cell value in the database when it's updated on the table.

HOW?
1. Capture the old and new values when a cell is edited.
2. Update the database with the new value when editing is finished.

*/


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
        model.setColumnIdentifiers(new Object[]{"ClientName", "ContactEmail"}); // Updated the Column Names to match the database exactly.
        table1.setModel(model); // Attach model to table

        // Execute initial database query to fetch client data
        clients = connect.executeQuery("SELECT ClientName, ContactEmail FROM SimpleCompany.clients", "ClientName", "ContactEmail");
        // Populate the table with the retrieved data
        updateTable();

        //ActionListener for the ComboBox (sorting options)
        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check which sorting option is selected
                if (comboBox1.getSelectedIndex() == 0) {
                    // Sort clients by Contact Email in ascending order
                    clients = connect.executeQuery("SELECT ClientName, ContactEmail FROM SimpleCompany.clients ORDER BY ContactName ASC", "ClientName", "ContactEmail");
                } else if (comboBox1.getSelectedIndex() == 1) {
                    // Sort clients by Contact Email in descending order
                    clients = connect.executeQuery("SELECT ClientName, ContactEmail FROM SimpleCompany.clients ORDER BY ContactEmail DESC", "ClientName", "ContactEmail");
                }
                updateTable(); // Refresh the table to display sorted data
            }
        });


        // Add a custom cell editor to capture the old value
        table1.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                // Capture the old value when editing starts
                return super.getTableCellEditorComponent(table, value, isSelected, row, column); // this method initializes the editor component (e.g., a JTextField) with the current cell value.
            }
            @Override
            // The stopCellEditing method is called when editing ends (e.g., when the user presses Enter or clicks outside the cell).
            public boolean stopCellEditing() {
                // Capture the new value entered by the user in the cell editor
                String newValue = getCellEditorValue().toString();

                // Get the row index of the cell currently being edited
                int row = table1.getEditingRow();

                // Get the column index of the cell currently being edited
                int column = table1.getEditingColumn();

                // Get the name of the column being edited
                String columnName = table1.getColumnName(column);

                // Get the value of the first column (ClientName) in the same row
                // This is used as a unique identifier for the database update
                String clientName = (String) table1.getValueAt(row, 0);

                // Call the updateDatabase method to save the new value to the database
                connect.updateDatabase(clientName, columnName, newValue);

                // Call the superclass method to finalize the editing process
                return super.stopCellEditing();
            }
        });

    }

    // Method to update the table with the latest client data
    private void updateTable() {
        model.setRowCount(0); // Clear all existing rows in the table
        for (String[] client : clients) {
            model.addRow(client); // Add each client's data as a new row
        }
    }
}
