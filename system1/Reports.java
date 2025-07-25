package library.management.system1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Reports extends JFrame {

    JTable reportTable;
    DefaultTableModel model;

    public Reports() {
        setTitle("Reports");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Main Panel with background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(255, 228, 235));
        add(mainPanel);

        // Title
        JLabel title = new JLabel("📚 Library Reports", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 26));
        title.setOpaque(true);
        title.setBackground(new Color(255, 192, 203));
        title.setForeground(Color.DARK_GRAY);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel();
        reportTable = new JTable(model);
        reportTable.setFillsViewportHeight(true);
        JScrollPane tableScroll = new JScrollPane(reportTable);
        reportTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        mainPanel.add(tableScroll, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton viewIssuedBtn = new JButton("View Issued Books");
        JButton viewReturnedBtn = new JButton("View Returned Books");
        JButton noticeBtn = new JButton("Overdue Notices");
        JButton printNoticeBtn = new JButton("Print Selected Notice");
        JButton backBtn = new JButton("Back");

        buttonPanel.add(viewIssuedBtn);
        buttonPanel.add(viewReturnedBtn);
        buttonPanel.add(noticeBtn);
        buttonPanel.add(printNoticeBtn);
        buttonPanel.add(backBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        viewIssuedBtn.addActionListener(e -> loadIssuedBooks());
        viewReturnedBtn.addActionListener(e -> loadReturnedBooks());
        noticeBtn.addActionListener(e -> showOverdueNotices());
        printNoticeBtn.addActionListener(e -> printSelectedNotice());
        backBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void loadIssuedBooks() {
        model.setRowCount(0);
        model.setColumnIdentifiers(new Object[]{"Issue ID", "Member ID", "Book ID", "Issue Date", "Due Date", "Return Date"});

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM issued_books";
            PreparedStatement stmt = conn.prepareStatement(sql);
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
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching issued books: " + ex.getMessage());
        }
    }

    private void loadReturnedBooks() {
    model.setRowCount(0);
    model.setColumnIdentifiers(new Object[]{"Return ID", "Member ID", "Book ID", "Issue Date", "Return Date", "Fine (BDT)"});

    try (Connection conn = DBConnection.getConnection()) {

        // 🛠️ STEP 1: Insert non-duplicate returned books into returned_books
        String insertSQL = """
            INSERT INTO returned_books (member_id, book_id, issue_date, return_date, fine)
            SELECT 
                ib.member_id,
                ib.book_id,
                ib.issue_date,
                ib.return_date,
                GREATEST(DATEDIFF(ib.return_date, ib.due_date) * 10, 0) AS fine
            FROM issued_books ib
            WHERE ib.return_date IS NOT NULL
              AND ib.id IN (
                  SELECT MAX(id)
                  FROM issued_books
                  WHERE return_date IS NOT NULL
                  GROUP BY member_id, book_id
              )
              AND NOT EXISTS (
                  SELECT 1 FROM returned_books rb
                  WHERE rb.member_id = ib.member_id AND rb.book_id = ib.book_id AND rb.return_date = ib.return_date
              )
        """;
        PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
        insertStmt.executeUpdate();  // insert executed

        // ✅ STEP 2: Fetch from returned_books to display
        String sql = "SELECT * FROM returned_books";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("member_id"),
                    rs.getString("book_id"),
                    rs.getDate("issue_date"),
                    rs.getDate("return_date"),
                    rs.getDouble("fine")
            });
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error fetching returned books: " + ex.getMessage());
    }
}

    private void showOverdueNotices() {
        model.setRowCount(0);
        model.setColumnIdentifiers(new Object[]{
                "Member ID", "Book ID", "Due Date", "Days Late", "Fine (BDT)", "Notice"
        });

        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                SELECT member_id, book_id, due_date,
                       DATEDIFF(CURDATE(), due_date) AS days_late,
                       DATEDIFF(CURDATE(), due_date) * 10 AS fine
                FROM issued_books
                WHERE return_date IS NULL AND CURDATE() > due_date
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int daysLate = rs.getInt("days_late");
                int fine = rs.getInt("fine");
                String notice = "Return book immediately. Fine: " + fine + " BDT";

                model.addRow(new Object[]{
                        rs.getString("member_id"),
                        rs.getString("book_id"),
                        rs.getDate("due_date"),
                        daysLate,
                        fine,
                        notice
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No currently overdue books.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading overdue notices: " + ex.getMessage());
        }
    }

   private void printSelectedNotice() {
    int row = reportTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select a row from the notice table.");
        return;
    }

    try {
        String memberId = model.getValueAt(row, 0).toString();
        String bookId = model.getValueAt(row, 1).toString();
        String dueDate = model.getValueAt(row, 2).toString();
        String daysLate = model.getValueAt(row, 3).toString();
        String fine = model.getValueAt(row, 4).toString();

        // Title Label
        JLabel titleLabel = new JLabel("OVERDUE NOTICE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.RED);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Notice Text
        JTextArea noticeArea = new JTextArea();
        noticeArea.setText(
                "---------------------- NOTICE LETTER ----------------------\n\n" +
                "Member ID      : " + memberId + "\n" +
                "Book ID        : " + bookId + "\n" +
                "Due Date       : " + dueDate + "\n" +
                "Days Overdue   : " + daysLate + " days\n" +
                "Total Fine     : " + fine + " BDT\n\n" +
                "You are requested to return the book immediately\n" +
                "and pay the mentioned fine at the library counter.\n\n" +
                "Failure to do so may result in further action.\n\n" +
                "Thank you.\n\n" +
                "------------------------------------------------------------"
        );
        noticeArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        noticeArea.setEditable(false);
        noticeArea.setBackground(Color.WHITE);
        noticeArea.setForeground(Color.BLACK);
        noticeArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(noticeArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        // Custom Panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Show Dialog
        JOptionPane.showMessageDialog(this, panel, "Overdue Notice", JOptionPane.PLAIN_MESSAGE);

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error printing notice: " + ex.getMessage());
    }
}

}
