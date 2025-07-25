package library.management.system1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame {

    private JLabel backgroundLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton closeButton;
    private JLabel titleLabel;

    public Login() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        resizeBackground();
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resizeBackground();
            }
        });
    }

    private void resizeBackground() {
        ImageIcon icon = new ImageIcon(getClass().getResource("login.jpg")); // no leading slash
        Image img = icon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(img));
        backgroundLabel.setBounds(0, 0, getWidth(), getHeight());
        if (titleLabel != null) {
        titleLabel.setBounds(0, 0, getWidth(), 50); // Always at top
      }
    }

    private void initComponents() {
        setLayout(null);
        
        titleLabel = new JLabel("WELCOME TO THE DIGITAL LIBRARY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true); // Allow background color
        titleLabel.setBackground(new Color(0, 102, 153)); // Optional dark blue background
        titleLabel.setBounds(0, 0, getWidth(), 50); // At top of screen, full width
        add(titleLabel);


        usernameLabel = new JLabel("   Username");
        usernameLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        usernameLabel.setForeground(Color.BLACK);
        usernameLabel.setOpaque(true);
        //usernameLabel.setBackground(Color.BLUE);
        usernameLabel.setBounds(100, 300, 100, 30);
        add(usernameLabel);

        passwordLabel = new JLabel("   Password");
        passwordLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        passwordLabel.setForeground(Color.BLACK);
        passwordLabel.setOpaque(true);
        //passwordLabel.setBackground(Color.BLUE);
        passwordLabel.setBounds(100, 340, 100, 30);
        add(passwordLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        usernameField.setBackground(Color.WHITE);
        usernameField.setBounds(200, 300, 200, 30);
        add(usernameField);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        passwordField.setBackground(Color.WHITE);
        passwordField.setBounds(200, 340, 200, 30);
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(200, 390, 90, 30);
        loginButton.addActionListener(e -> loginAction());
        add(loginButton);

        closeButton = new JButton("Close");
        closeButton.setBounds(310, 390, 90, 30);
        closeButton.addActionListener(e -> System.exit(0));
        add(closeButton);

        backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, 1920, 1080); // Initial bounds
        add(backgroundLabel);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void loginAction() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.equals("admin") && password.equals("admin")) {
            setVisible(false);
            new home().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect Username or Password");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}
