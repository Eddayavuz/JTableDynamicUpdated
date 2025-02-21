import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

/* UPDATES 20.02.2025
 * Public Static DefaultTableModel:
 *      The DefaultTableModel was made public and static to make it accessible from the connect class.
 *      This allows other classes or components that need to interact with the table model to do so directly
 *      without needing to create an instance of the form.

 * Add Column Button:
 *      An event listener was added for the "add column" button.
 *      When clicked, it prompts the user to input the name of a new column,
 *      then adds that column to the table model and updates the database to reflect the new column.
 *      The table is refreshed to show the newly added column.*/


public class form1 extends JFrame {
    private JPanel panel1;
    private JTable table1;
    private JComboBox<String> comboBox1;
    private JButton addColumnButton;
    private JComboBox comboBox2;

    public static DefaultTableModel model;
    private ArrayList<String[]> actors;

    public form1() {
        setSize(700, 400);
        setContentPane(panel1);
        setVisible(true);

        model = new DefaultTableModel();
        table1.setModel(model);

        actors = connect.executeQuery("SELECT * FROM sakila.actor");
        updateTable();

        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comboBox1.getSelectedIndex() == 0) {
                } else if (comboBox1.getSelectedIndex() == 1) {
                }
                updateTable();
            }
        });

        table1.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()) {
            private String oldValue;

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                oldValue = (value != null) ? value.toString() : "";
                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }

            @Override
            public boolean stopCellEditing() {
                String newValue = getCellEditorValue().toString();
                int row = table1.getEditingRow();
                int column = table1.getEditingColumn();
                String columnName = table1.getColumnName(column);
                String clientName = (String) table1.getValueAt(row, 0);

                if (!newValue.equals(oldValue)) {
                    String[] columns = {columnName};
                    String[] newValues = {newValue};
                    connect.updateDatabase(clientName, columns, newValues);
                }

                return super.stopCellEditing();
            }
        });
        addColumnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!comboBox2.getSelectedItem().toString().equals("select")){
                String newColumn = JOptionPane.showInputDialog("Enter the name of the new column");
                String dataType = comboBox2.getSelectedItem().toString();
                connect.addColumn(newColumn, dataType);
                model.addColumn(newColumn);
                updateTable();
                }
            }
        });
    }

    private void updateTable () {
        model.setRowCount(0);
        for (String[] actor : actors) {
            model.addRow(actor);
        }
    }

}
