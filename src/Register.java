import org.mindrot.jbcrypt.BCrypt;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

/* UPDATES 24.02.2025
 * this form allows the user to add a new record(row) into the actor table.
 * 1. I updated my table to add an img column and a password column.
 * 2. I added necessary fields to my form design
 * 3. I created an event listener for the button to read information from those fields.
 * 4. I passed the information to the method called addActor in my connect.java
 *
 * Before we explore the addActor method, we will talk about how to handle password and image.
 * Scroll down to see the event listener for both selectImage and register buttons.
 * */


public class Register extends JFrame {
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JButton registerButton;
    private JButton selectImageButton;
    private JLabel imageLabel;
    private JPanel panel;
    private JButton loginInsteadButton;

    public Register() {
        setSize(500, 500);
        setContentPane(panel);
        setVisible(true);

        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a file chooser window for selecting a file
                JFileChooser fileChooser = new JFileChooser();

                // Show the open dialog to the user and store the result (APPROVE_OPTION if a file is selected)
                int result = fileChooser.showOpenDialog(null);

                // If the user selects a file, proceed with loading and displaying the image
                if (result == JFileChooser.APPROVE_OPTION) {
                    // Get the selected file
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        // Read the image from the selected file
                        Image image = ImageIO.read(selectedFile);

                        // Scale the image to 100x100 pixels and create an ImageIcon
                        ImageIcon icon = new ImageIcon(image.getScaledInstance(100, 100, Image.SCALE_SMOOTH));

                        // Set the ImageIcon to the label and clear any existing text
                        imageLabel.setIcon(icon);
                        imageLabel.setText("");
                    } catch (IOException ex) {
                        // Show an error message if there is an issue loading the image
                        JOptionPane.showMessageDialog(null, "Error loading image.");
                    }
                }

            }
        });

        registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    // Retrieve text input from the text fields for first name, last name, and password
                    String firstName = textField1.getText();
                    String lastName = textField2.getText();
                    String password = textField3.getText();

                    // Hash the password using BCrypt -we have to import the BCrypt library with Maven. (See the video tutorial in the assignment)
                    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                    // Convert the icon from JLabel to an InputStream (for image storage in the DB)
                    ImageIcon icon = (ImageIcon) imageLabel.getIcon();
                    InputStream imageInputStream = null;

                    if (icon != null) {
                        // Create a BufferedImage to hold the icon
                        BufferedImage bufferedImage = new BufferedImage(
                                icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

                        // Create a Graphics object to draw the icon onto the BufferedImage
                        Graphics g = bufferedImage.createGraphics();
                        icon.paintIcon(null, g, 0, 0);
                        g.dispose();  // Dispose of the Graphics object once done

                        // Convert the BufferedImage to an InputStream
                        try {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                            imageInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                        } catch (IOException ex) {
                            // Show an error message if there's an issue converting the image to a binary stream
                            JOptionPane.showMessageDialog(null, "Error converting image to binary stream.");
                            return;  // Exit the method if there's an error
                        }
                    }

                    // Check if all required fields are filled (first name, last name, password, and image)
                    boolean allFieldsFilled = !textField1.getText().isEmpty() &&
                            !textField2.getText().isEmpty() &&
                            !textField3.getText().isEmpty() &&
                            imageLabel.getIcon() != null;

                    // If all fields are filled, proceed to add the actor, otherwise show a warning
                    if (allFieldsFilled) {
                        connect.addActor(firstName, lastName, imageInputStream, hashedPassword);
                    } else {
                        // Show a message prompting the user to fill in all the fields
                        JOptionPane.showMessageDialog(null, "Fill all the fields!");
                    }
                }
            });
        loginInsteadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new login();
            }
        });
    }
    }
