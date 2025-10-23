import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;

// BankAccount class
class BankAccount {
    public String accountNumber;
    public String pin;
    public String holderName;
    public double balance;

    public BankAccount(String accountNumber, String pin, String holderName, double balance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.holderName = holderName;
        this.balance = balance;
    }
}

public class ATMGUI extends JFrame {

    private BankAccount currentUser;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public ATMGUI() {
        initGUI();
    }

    private void initGUI() {
        setTitle("ATM System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(loginPanel(), "login");
        mainPanel.add(registerPanel(), "register");
        mainPanel.add(atmMenuPanel(), "menu");

        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel loginPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1));
        JTextField accField = new JTextField();
        JPasswordField pinField = new JPasswordField();

        JButton loginBtn = new JButton("Login");
        JButton toRegister = new JButton("Register");

        panel.add(new JLabel("Account Number:"));
        panel.add(accField);
        panel.add(new JLabel("PIN:"));
        panel.add(pinField);
        panel.add(loginBtn);
        panel.add(toRegister);

        loginBtn.addActionListener(e -> {
            String acc = accField.getText().trim();
            String pin = new String(pinField.getPassword()).trim();

            try {
                BankAccount accObj = dbhelper.getAccount(acc);
                if (accObj != null && accObj.pin.equals(pin)) {
                    currentUser = accObj;
                    JOptionPane.showMessageDialog(this, "Welcome " + currentUser.holderName);
                    cardLayout.show(mainPanel, "menu");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        toRegister.addActionListener(e -> cardLayout.show(mainPanel, "register"));

        return panel;
    }

    private JPanel registerPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1));
        JTextField accField = new JTextField();
        JTextField nameField = new JTextField();
        JPasswordField pinField = new JPasswordField();
        JTextField depositField = new JTextField();

        JButton registerBtn = new JButton("Create Account");

        panel.add(new JLabel("New Account Number:"));
        panel.add(accField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("PIN:"));
        panel.add(pinField);
        panel.add(new JLabel("Initial Deposit:"));
        panel.add(depositField);
        panel.add(registerBtn);

        registerBtn.addActionListener(e -> {
            String accNo = accField.getText().trim();
            String name = nameField.getText().trim();
            String pin = new String(pinField.getPassword()).trim();
            double deposit;

            try {
                deposit = Double.parseDouble(depositField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid deposit amount.");
                return;
            }

            try {
                dbhelper.addAccount(accNo, name, pin, deposit);
                JOptionPane.showMessageDialog(this, "Account created!");
                cardLayout.show(mainPanel, "login");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error creating account: " + ex.getMessage());
            }
        });

        return panel;
    }

    private JPanel atmMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1));

        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton balanceBtn = new JButton("Check Balance");
        JButton historyBtn = new JButton("Transaction History");
        JButton logoutBtn = new JButton("Logout");

        panel.add(depositBtn);
        panel.add(withdrawBtn);
        panel.add(balanceBtn);
        panel.add(historyBtn);
        panel.add(logoutBtn);

        depositBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter deposit amount:");
            try {
                double amt = Double.parseDouble(input);
                currentUser.balance += amt;
                dbhelper.updateBalance(currentUser.accountNumber, currentUser.balance);
                dbhelper.insertTransaction(currentUser.accountNumber, "Deposit", amt);
                JOptionPane.showMessageDialog(this, "Deposited successfully!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Invalid input!");
            }
        });

        withdrawBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter withdrawal amount:");
            try {
                double amt = Double.parseDouble(input);
                if (amt <= currentUser.balance) {
                    currentUser.balance -= amt;
                    dbhelper.updateBalance(currentUser.accountNumber, currentUser.balance);
                    dbhelper.insertTransaction(currentUser.accountNumber, "Withdraw", amt);
                    JOptionPane.showMessageDialog(this, "Withdrawn successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient balance!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Invalid input!");
            }
        });

        balanceBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Current Balance: ₹" + currentUser.balance)
        );

        historyBtn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("Transaction History:\n");
            try {
                ResultSet rs = dbhelper.getTransactions(currentUser.accountNumber);
                boolean found = false;
                while (rs.next()) {
                    sb.append(rs.getString("type"))
                      .append(" : ₹")
                      .append(rs.getDouble("amount"))
                      .append(" on ")
                      .append(rs.getTimestamp("date"))
                      .append("\n");
                    found = true;
                }
                if (!found) sb.append("No transactions yet.");
            } catch (Exception ex) {
                ex.printStackTrace();
                sb.append("Error fetching transactions.");
            }
            JOptionPane.showMessageDialog(this, sb.toString());
        });

        logoutBtn.addActionListener(e -> {
            currentUser = null;
            cardLayout.show(mainPanel, "login");
        });

        return panel;
    }

    public static void main(String[] args) {
        dbhelper.testConnection();
        SwingUtilities.invokeLater(ATMGUI::new);
    }
}
