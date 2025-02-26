import javax.swing.*;

public class Welcome extends JFrame{

    private JLabel img;
    private JLabel first_name;
    private JPanel panel;

    public Welcome(User user){
        setSize(500, 500);
        setContentPane(panel);
        setVisible(true);

        first_name.setText("Welcome " + user.getFirst_name());

        // Load image
        ImageIcon icon = user.getImageIcon(); // Ensure user.getImg() returns a valid path
       // Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        img.setIcon(icon);
    }
}
