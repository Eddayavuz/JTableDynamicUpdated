import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class login extends JFrame{
    private JTextField textField2;
    private JTextField textField1;
    private JButton loginButton;
    private JPanel panel;

    public login() {
        setSize(500, 500);
        setContentPane(panel);
        setVisible(true);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User user = connect.login(textField1.getText(), textField2.getText());
                if (user != null) {
                    JOptionPane.showMessageDialog(null, "Successfully logged in as " + user.getFirst_name(), "Login Success", JOptionPane.INFORMATION_MESSAGE);
                    new Welcome(user);
                } else {
                    JOptionPane.showMessageDialog(null, "Login failed. Invalid credentials.", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
