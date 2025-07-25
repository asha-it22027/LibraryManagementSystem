package library.management.system1;

import javax.swing.*;
import java.awt.*;

public class home extends JFrame {

    public home() {
        setTitle("Library Management System - Home");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Load background image
        ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/library/management/system1/background2.png"));
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, 1920, 1080);
        setContentPane(backgroundLabel);
        backgroundLabel.setLayout(null);

       // Screen size
       Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
       int screenHeight = screenSize.height;
       int screenWidth = screenSize.width;

       // Button layout settings
       int buttonWidth = 250;
       int buttonHeight = 50;
       int gapY = 30;
       int numberOfButtons = 5;
       int totalHeight = numberOfButtons * buttonHeight + (numberOfButtons - 1) * gapY;
       int startY = (screenHeight - totalHeight) / 2;

       // Align to right side
       int marginFromRight = 250;
       int startX = screenWidth - buttonWidth - marginFromRight;
       Font buttonFont = new Font("Arial", Font.BOLD, 16);

        // Manage Books Button
        JButton manageBooksButton = new JButton("Manage Books");
        manageBooksButton.setBounds(startX, startY, buttonWidth, buttonHeight);
        manageBooksButton.setFont(buttonFont);
        manageBooksButton.addActionListener(e -> new ManageBooks());
        backgroundLabel.add(manageBooksButton);

        // Manage Members Button
        JButton manageMembersButton = new JButton("Manage Members");
        manageMembersButton.setBounds(startX, startY + 1 * (buttonHeight + gapY), buttonWidth, buttonHeight);
        manageMembersButton.setFont(buttonFont);
        manageMembersButton.addActionListener(e -> new ManageMembers());
        backgroundLabel.add(manageMembersButton);

        // Issue/Return Books Button
        JButton issueReturnButton = new JButton("Issue/Return Books");
        issueReturnButton.setBounds(startX, startY + 2 * (buttonHeight + gapY), buttonWidth, buttonHeight);
        issueReturnButton.setFont(buttonFont);
        issueReturnButton.addActionListener(e -> new IssueReturn());
        backgroundLabel.add(issueReturnButton);

        // View Reports Button
        JButton reportsButton = new JButton("View Reports");
        reportsButton.setBounds(startX, startY + 3 * (buttonHeight + gapY), buttonWidth, buttonHeight);
        reportsButton.setFont(buttonFont);
        reportsButton.addActionListener(e -> new Reports());
        backgroundLabel.add(reportsButton);

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(startX, startY + 4 * (buttonHeight + gapY), buttonWidth, buttonHeight);
        logoutButton.setFont(buttonFont);
        logoutButton.addActionListener(e -> {
            dispose();
            new Login(); // Assuming Login class exists
        });
        backgroundLabel.add(logoutButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        new home();
    }
}
