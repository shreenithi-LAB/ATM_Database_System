import java.sql.*;

public class dbhelper {

    private static final String URL = "jdbc:mysql://localhost:3306/atmdb"; // your DB name
    private static final String USER = "root";       // MySQL username
    private static final String PASSWORD = "Root"; // MySQL password

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Test DB connection
    public static void testConnection() {
        try (Connection con = getConnection()) {
            if (con != null) System.out.println("✅ Database connected successfully!");
            else System.out.println("❌ Database connection failed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- BankAccount Methods ----------------

    public static BankAccount getAccount(String accNo) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE accountNumber=?";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, accNo);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new BankAccount(
                        rs.getString("accountNumber"),
                        rs.getString("pin"),
                        rs.getString("holderName"),
                        rs.getDouble("balance")
                );
            }
        }
        return null;
    }

    public static void addAccount(String accNo, String name, String pin, double balance) throws SQLException {
        String sql = "INSERT INTO accounts(accountNumber, holderName, pin, balance) VALUES(?,?,?,?)";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, accNo);
            pst.setString(2, name);
            pst.setString(3, pin);
            pst.setDouble(4, balance);
            pst.executeUpdate(); // <-- insert row
        }
    }

    public static void updateBalance(String accNo, double balance) throws SQLException {
        String sql = "UPDATE accounts SET balance=? WHERE accountNumber=?";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setDouble(1, balance);
            pst.setString(2, accNo);
            pst.executeUpdate();
        }
    }

    public static void insertTransaction(String accNo, String type, double amount) throws SQLException {
        String sql = "INSERT INTO transactions(accountNumber, type, amount, date) VALUES(?,?,?,NOW())";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, accNo);
            pst.setString(2, type);
            pst.setDouble(3, amount);
            pst.executeUpdate();
        }
    }

    public static ResultSet getTransactions(String accNo) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE accountNumber=? ORDER BY date DESC";
        Connection con = getConnection();
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, accNo);
        return pst.executeQuery(); // caller must close ResultSet & Connection
    }
}
