package library.management.system1;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageBooks extends JFrame {

    JTable bookTable;
    DefaultTableModel model;
    JLabel bookImageLabel;

    public ManageBooks() {
        setTitle("Manage Books");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(173, 216, 230));

        JLabel titleLabel = new JLabel("Manage Library Books", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Search panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search a Book: ");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Center panel with table and image
        model = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Category", "Quantity", "Image Path"}, 0);
        bookTable = new JTable(model);
        bookTable.setRowHeight(25);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setBackground(new Color(173, 216, 230));

        // Custom renderer for row highlight
        CustomTableCellRenderer renderer = new CustomTableCellRenderer();
        for (int i = 0; i < bookTable.getColumnCount(); i++) {
            bookTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane tableScroll = new JScrollPane(bookTable);
        tableScroll.getViewport().setBackground(new Color(173, 216, 230));

        JLabel bookTitleLabel = new JLabel("Selected Book Title", SwingConstants.CENTER);
        bookTitleLabel.setFont(new Font("Serif", Font.BOLD, 16));
        bookTitleLabel.setOpaque(true);
        bookTitleLabel.setBackground(new Color(173, 216, 230));

        bookImageLabel = new JLabel("Book Cover", SwingConstants.CENTER);
        bookImageLabel.setPreferredSize(new Dimension(150, 200));
        bookImageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        bookImageLabel.setOpaque(true);
        bookImageLabel.setBackground(new Color(173, 216, 230));

        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
        imagePanel.setPreferredSize(new Dimension(180, 250));
        imagePanel.setBackground(new Color(173, 216, 230));
        imagePanel.add(bookTitleLabel);
        imagePanel.add(bookImageLabel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, imagePanel);
        splitPane.setResizeWeight(0.75);
        splitPane.setDividerSize(4);
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);

        // Bottom panel with buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Book");
        JButton editButton = new JButton("Edit Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton viewAllButton = new JButton("View All");
        JButton backButton = new JButton("Back");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewAllButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add Book
        addButton.addActionListener(e -> {
            JTextField idField = new JTextField();
            JTextField titleField = new JTextField();
            JTextField authorField = new JTextField();
            JTextField categoryField = new JTextField();
            JTextField quantityField = new JTextField();
            JTextField imagePathField = new JTextField();

            JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
            inputPanel.add(new JLabel("ID:"));
            inputPanel.add(idField);
            inputPanel.add(new JLabel("Title:"));
            inputPanel.add(titleField);
            inputPanel.add(new JLabel("Author:"));
            inputPanel.add(authorField);
            inputPanel.add(new JLabel("Category:"));
            inputPanel.add(categoryField);
            inputPanel.add(new JLabel("Quantity:"));
            inputPanel.add(quantityField);
            inputPanel.add(new JLabel("Image Path:"));
            inputPanel.add(imagePathField);

            JLabel imageLabel = new JLabel();
            try {
                ImageIcon icon = new ImageIcon("C:/images/add_book.jpg"); // Change as needed
                Image scaled = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
            } catch (Exception ex) {
                imageLabel.setText("No image");
            }

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.add(inputPanel, BorderLayout.CENTER);
            mainPanel.add(imageLabel, BorderLayout.EAST);
            mainPanel.setPreferredSize(new Dimension(500, 250));

            int option = JOptionPane.showConfirmDialog(this, mainPanel, "Add Book", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "INSERT INTO books (id, title, author, category, quantity, image_path) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, Integer.parseInt(idField.getText()));
                    stmt.setString(2, titleField.getText());
                    stmt.setString(3, authorField.getText());
                    stmt.setString(4, categoryField.getText());
                    stmt.setInt(5, Integer.parseInt(quantityField.getText()));
                    stmt.setString(6, imagePathField.getText());
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Book added successfully!");
                    viewAllButton.doClick();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        // Edit Book
        editButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a book to edit.");
                return;
            }

            int id = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
            String title = model.getValueAt(selectedRow, 1).toString();
            String author = model.getValueAt(selectedRow, 2).toString();
            String category = model.getValueAt(selectedRow, 3).toString();
            String quantity = model.getValueAt(selectedRow, 4).toString();
            String imagePath = model.getValueAt(selectedRow, 5).toString();

            JTextField idField = new JTextField(String.valueOf(id));
            idField.setEditable(false);
            JTextField titleField = new JTextField(title);
            JTextField authorField = new JTextField(author);
            JTextField categoryField = new JTextField(category);
            JTextField quantityField = new JTextField(quantity);
            JTextField imagePathField = new JTextField(imagePath);

            JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
            inputPanel.add(new JLabel("ID:"));
            inputPanel.add(idField);
            inputPanel.add(new JLabel("Title:"));
            inputPanel.add(titleField);
            inputPanel.add(new JLabel("Author:"));
            inputPanel.add(authorField);
            inputPanel.add(new JLabel("Category:"));
            inputPanel.add(categoryField);
            inputPanel.add(new JLabel("Quantity:"));
            inputPanel.add(quantityField);
            inputPanel.add(new JLabel("Image Path:"));
            inputPanel.add(imagePathField);

            JLabel imageLabel = new JLabel();
            try {
                ImageIcon icon = new ImageIcon(imagePath);
                Image scaled = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
            } catch (Exception ex) {
                imageLabel.setText("No image");
            }

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.add(inputPanel, BorderLayout.CENTER);
            mainPanel.add(imageLabel, BorderLayout.EAST);
            mainPanel.setPreferredSize(new Dimension(500, 250));

            int option = JOptionPane.showConfirmDialog(this, mainPanel, "Edit Book", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "UPDATE books SET title=?, author=?, category=?, quantity=?, image_path=? WHERE id=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, titleField.getText());
                    stmt.setString(2, authorField.getText());
                    stmt.setString(3, categoryField.getText());
                    stmt.setInt(4, Integer.parseInt(quantityField.getText()));
                    stmt.setString(5, imagePathField.getText());
                    stmt.setInt(6, id);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Book updated successfully!");
                    viewAllButton.doClick();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        // Delete Book
        deleteButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a book to delete.");
                return;
            }

            int id = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?", "Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE id=?");
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Book deleted Successfully!");
                    viewAllButton.doClick();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        // Search Book
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            model.setRowCount(0);
            String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR category LIKE ?";
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
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("category"),
                            rs.getInt("quantity"),
                            rs.getString("image_path")
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage());
            }
        });

        // View All
        viewAllButton.addActionListener(e -> {
            model.setRowCount(0);
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("category"),
                            rs.getInt("quantity"),
                            rs.getString("image_path")
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading books: " + ex.getMessage());
            }
        });

        // Back
        backButton.addActionListener(e -> {
            dispose();
            new home();
        });

        // Book image preview
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bookTable.getSelectedRow() != -1) {
                String path = model.getValueAt(bookTable.getSelectedRow(), 5).toString();
                try {
                    ImageIcon icon = new ImageIcon(path);
                    Image scaled = icon.getImage().getScaledInstance(350, 450, Image.SCALE_SMOOTH);
                    bookImageLabel.setIcon(new ImageIcon(scaled));
                    bookImageLabel.setText("");
                } catch (Exception ex) {
                    bookImageLabel.setText("Invalid Image");
                    bookImageLabel.setIcon(null);
                }
            }
        });

        // Load books
        viewAllButton.doClick();
        setVisible(true);
    }

    // Custom Renderer for selected row
    class CustomTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (isSelected) {
                c.setBackground(new Color(0, 0, 128)); // Dark Blue
                c.setForeground(Color.WHITE);
            } else {
                c.setBackground(new Color(173, 216, 230));
                c.setForeground(Color.BLACK);
            }
            return c;
        }
    }
}
