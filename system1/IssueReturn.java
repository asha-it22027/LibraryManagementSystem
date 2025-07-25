package library.management.system1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class IssueReturn extends JFrame {

    private final JTextField issueMemberIdField = new JTextField();
    private final JTextField issueBookIdField = new JTextField();
    private final JTextField issueDateField = new JTextField();
    private final JTextField dueDateField = new JTextField();

    private final JTextField returnMemberIdField = new JTextField();
    private final JTextField returnBookIdField = new JTextField();
    private final JTextField returnDateField = new JTextField();

    private final JTextField searchField = new JTextField();

    private final JTable recordTable = new JTable();
    private final DefaultTableModel model = new DefaultTableModel();

    public IssueReturn() {
        setTitle("Issue / Return Books");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Issue Book", createIssuePanel());
        tabbedPane.addTab("Return Book", createReturnPanel());
        tabbedPane.addTab("View/Edit/Delete", createViewPanel());

        add(tabbedPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose());
        add(backButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createIssuePanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        panel.add(new JLabel("Member ID:"));
        panel.add(issueMemberIdField);
        panel.add(new JLabel("Book ID:"));
        panel.add(issueBookIdField);
        panel.add(new JLabel("Issue Date (YYYY-MM-DD):"));
        panel.add(issueDateField);
        panel.add(new JLabel("Due Date (YYYY-MM-DD):"));
        panel.add(dueDateField);

        JButton issueBtn = new JButton("Issue Book");
        JButton clearBtn = new JButton("Clear");

        issueBtn.addActionListener(e -> issueBook());
        clearBtn.addActionListener(e -> {
            issueMemberIdField.setText("");
            issueBookIdField.setText("");
            issueDateField.setText("");
            dueDateField.setText("");
        });

        panel.add(issueBtn);
        panel.add(clearBtn);

        return panel;
    }

    private JPanel createReturnPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        panel.add(new JLabel("Member ID:"));
        panel.add(returnMemberIdField);
        panel.add(new JLabel("Book ID:"));
        panel.add(returnBookIdField);
        panel.add(new JLabel("Return Date (YYYY-MM-DD):"));
        panel.add(returnDateField);

        JButton returnBtn = new JButton("Return Book");
        JButton clearBtn = new JButton("Clear");

        returnBtn.addActionListener(e -> returnBook());
        clearBtn.addActionListener(e -> {
            returnMemberIdField.setText("");
            returnBookIdField.setText("");
            returnDateField.setText("");
        });

        panel.add(returnBtn);
        panel.add(clearBtn);

        return panel;
    }

    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton editBtn = new JButton("Edit Selected");
        JButton searchBtn = new JButton("Search");

        topPanel.add(new JLabel("Search (Member ID or Book ID):"));
        searchField.setColumns(10);
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(refreshBtn);
        topPanel.add(editBtn);
        topPanel.add(deleteBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(recordTable), BorderLayout.CENTER);

        // Set table model
        model.setColumnIdentifiers(new String[]{"ID", "Member ID", "Book ID", "Issue Date", "Due Date", "Return Date"});
        recordTable.setModel(model);

        // Actions
        refreshBtn.addActionListener(e -> loadRecords());
        deleteBtn.addActionListener(e -> deleteSelectedRecord());
        editBtn.addActionListener(e -> editSelectedRecord());
        searchBtn.addActionListener(e -> searchRecords());

        loadRecords();
        return panel;
    }

    private void issueBook() {
        String memberId = issueMemberIdField.getText().trim();
        String bookId = issueBookIdField.getText().trim();
        String issueDate = issueDateField.getText().trim();
        String dueDate = dueDateField.getText().trim();

        if (memberId.isEmpty() || bookId.isEmpty() || issueDate.isEmpty() || dueDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO issued_books (member_id, book_id, issue_date, due_date) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, memberId);
            stmt.setString(2, bookId);
            stmt.setDate(3, Date.valueOf(issueDate));
            stmt.setDate(4, Date.valueOf(dueDate));
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book issued successfully!");
            issueMemberIdField.setText("");
            issueBookIdField.setText("");
            issueDateField.setText("");
            dueDateField.setText("");
            loadRecords();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Issue Error: " + ex.getMessage());
        }
    }

    private void returnBook() {
        String memberId = returnMemberIdField.getText().trim();
        String bookId = returnBookIdField.getText().trim();
        String returnDate = returnDateField.getText().trim();

        if (memberId.isEmpty() || bookId.isEmpty() || returnDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE issued_books SET return_date = ? WHERE member_id = ? AND book_id = ? AND return_date IS NULL";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDate(1, Date.valueOf(returnDate));
            stmt.setString(2, memberId);
            stmt.setString(3, bookId);
            int updated = stmt.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Book returned successfully!");
                returnMemberIdField.setText("");
                returnBookIdField.setText("");
                returnDateField.setText("");
                loadRecords();
            } else {
                JOptionPane.showMessageDialog(this, "No active issued book found.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Return Error: " + ex.getMessage());
        }
    }

    private void loadRecords() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM issued_books";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("member_id"),
                        rs.getString("book_id"),
                        rs.getDate("issue_date"),
                        rs.getDate("due_date"),
                        rs.getDate("return_date")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Load Error: " + ex.getMessage());
        }
    }

    private void deleteSelectedRecord() {
        int row = recordTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM issued_books WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Record deleted.");
            loadRecords();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete Error: " + ex.getMessage());
        }
    }

    private void editSelectedRecord() {
        int row = recordTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to edit.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String newIssueDate = JOptionPane.showInputDialog(this, "Enter new Issue Date (YYYY-MM-DD):");
        String newDueDate = JOptionPane.showInputDialog(this, "Enter new Due Date (YYYY-MM-DD):");

        if (newIssueDate != null && newDueDate != null) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE issued_books SET issue_date = ?, due_date = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setDate(1, Date.valueOf(newIssueDate));
                stmt.setDate(2, Date.valueOf(newDueDate));
                stmt.setInt(3, id);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Record updated.");
                loadRecords();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Update Error: " + ex.getMessage());
            }
        }
    }

    private void searchRecords() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Member ID or Book ID to search.");
            return;
        }

        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM issued_books WHERE member_id LIKE ? OR book_id LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("member_id"),
                        rs.getString("book_id"),
                        rs.getDate("issue_date"),
                        rs.getDate("due_date"),
                        rs.getDate("return_date")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search Error: " + ex.getMessage());
        }
    }
}
