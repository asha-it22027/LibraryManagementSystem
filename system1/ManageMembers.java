package library.management.system1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageMembers extends JFrame {

    JTable memberTable;
    DefaultTableModel model;
    JLabel imageLabel;

    public ManageMembers() {
        setTitle("Manage Members");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Main panel with sky blue background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(173, 216, 230)); // Light sky blue

        JLabel title = new JLabel("Manage Members", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.DARK_GRAY);
        mainPanel.add(title, BorderLayout.NORTH);
        
         // Search panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search a member: ");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.BEFORE_FIRST_LINE);


        // Left panel for image
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(400, 500));
        setImage("C:/images/member.jpg"); // <-- Replace with your actual image path
        mainPanel.add(imageLabel, BorderLayout.EAST);

        // Center panel for table
        model = new DefaultTableModel(new Object[]{"ID", "Name", "Phone", "Email"}, 0);
        memberTable = new JTable(model);
        JScrollPane tableScroll = new JScrollPane(memberTable);
        mainPanel.add(tableScroll, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(173, 216, 230));

        JButton addButton = new JButton("Add Member");
        JButton editButton = new JButton("Edit Member");
        JButton deleteButton = new JButton("Delete Member");
        JButton viewAllButton = new JButton("View All");
        JButton backButton = new JButton("Back");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewAllButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Button Actions
        addButton.addActionListener(e -> {
            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField phoneField = new JTextField();
            JTextField emailField = new JTextField();

            JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            inputPanel.add(new JLabel("ID:"));
            inputPanel.add(idField);
            inputPanel.add(new JLabel("Name:"));
            inputPanel.add(nameField);
            inputPanel.add(new JLabel("Phone:"));
            inputPanel.add(phoneField);
            inputPanel.add(new JLabel("Email:"));
            inputPanel.add(emailField);

            JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
            dialogPanel.add(inputPanel, BorderLayout.CENTER);
            dialogPanel.setPreferredSize(new Dimension(400, 200));  

            JLabel dialogImage = new JLabel();
            dialogImage.setHorizontalAlignment(SwingConstants.CENTER);
            dialogImage.setIcon(new ImageIcon(new ImageIcon("C:/images/member_form.jpg").getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH)));
            dialogPanel.add(dialogImage, BorderLayout.EAST);

            int option = JOptionPane.showConfirmDialog(this, dialogPanel, "Add Member", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "INSERT INTO members (id, name, phone, email) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, Integer.parseInt(idField.getText()));
                    stmt.setString(2, nameField.getText());
                    stmt.setString(3, phoneField.getText());
                    stmt.setString(4, emailField.getText());
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Member added successfully!");
                    viewAllMembers();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        editButton.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a member to edit.");
                return;
            }

            int id = (int) model.getValueAt(selectedRow, 0);
            String name = (String) model.getValueAt(selectedRow, 1);
            String phone = (String) model.getValueAt(selectedRow, 2);
            String email = (String) model.getValueAt(selectedRow, 3);

            JTextField idField = new JTextField(String.valueOf(id));
            idField.setEditable(false);
            JTextField nameField = new JTextField(name);
            JTextField phoneField = new JTextField(phone);
            JTextField emailField = new JTextField(email);

            JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            inputPanel.add(new JLabel("ID:"));
            inputPanel.add(idField);
            inputPanel.add(new JLabel("Name:"));
            inputPanel.add(nameField);
            inputPanel.add(new JLabel("Phone:"));
            inputPanel.add(phoneField);
            inputPanel.add(new JLabel("Email:"));
            inputPanel.add(emailField);

            JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
            dialogPanel.add(inputPanel, BorderLayout.CENTER);

            JLabel dialogImage = new JLabel();
            dialogImage.setHorizontalAlignment(SwingConstants.CENTER);
            dialogImage.setIcon(new ImageIcon(new ImageIcon("C:/images/member_form.jpg").getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH)));
            dialogPanel.add(dialogImage, BorderLayout.EAST);

            int option = JOptionPane.showConfirmDialog(this, dialogPanel, "Edit Member", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "UPDATE members SET name=?, phone=?, email=? WHERE id=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, nameField.getText());
                    stmt.setString(2, phoneField.getText());
                    stmt.setString(3, emailField.getText());
                    stmt.setInt(4, Integer.parseInt(idField.getText()));
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Member updated successfully!");
                    viewAllMembers();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });
        // Search member
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            model.setRowCount(0);
            String query = "SELECT * FROM members WHERE name LIKE ? OR id LIKE ? OR email LIKE ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                String like = "%" + keyword + "%";
                stmt.setString(1, like);
                stmt.setString(2, like);
                stmt.setString(3, like);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage());
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a member to delete.");
                return;
            }

            int id = (int) model.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this member?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "DELETE FROM members WHERE id=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Member deleted successfully!");
                    viewAllMembers();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        viewAllButton.addActionListener(e -> viewAllMembers());

        backButton.addActionListener(e -> dispose());

        add(mainPanel);
        viewAllMembers(); // Show all members when frame opens
        setVisible(true);
    }

    // Load and set image
    private void setImage(String path) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage().getScaledInstance(400, 550, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            imageLabel.setText("Image not found");
        }
    }

    // Method to load all members into the table
    private void viewAllMembers() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM members";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
