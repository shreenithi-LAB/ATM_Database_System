import java.util.*;

// ---- BankAccount class (Encapsulation) ----
class BankAccount {
    private String accountNumber;
    private String pin;
    private double balance;
    private List<String> transactionHistory;

    public BankAccount(String accountNumber, String pin, double balance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
        this.transactionHistory = new ArrayList<>();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public boolean validatePin(String enteredPin) {
        return this.pin.equals(enteredPin);
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
        transactionHistory.add("Deposited: " + amount + " | Balance: " + balance);
    }

    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            transactionHistory.add("Withdrew: " + amount + " | Balance: " + balance);
            return true;
        } else {
            transactionHistory.add("Failed Withdrawal Attempt of: " + amount);
            return false;
        }
    }

    public void showTransactions() {
        if (transactionHistory.isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            for (String t : transactionHistory) {
                System.out.println(t);
            }
        }
    }
}

// ---- Abstract ATM (Abstraction) ----
abstract class ATM {
    protected BankAccount account;

    public ATM(BankAccount account) {
        this.account = account;
    }

    public abstract void deposit(double amount);
    public abstract void withdraw(double amount);
    public abstract void checkBalance();
    public abstract void showTransactions();
}

// ---- ATM Implementation ----
class ATMImpl extends ATM {

    public ATMImpl(BankAccount account) {
        super(account);
    }

    @Override
    public void deposit(double amount) {
        account.deposit(amount);
        System.out.println("Deposited " + amount + ". New Balance: " + account.getBalance());
    }

    @Override
    public void withdraw(double amount) {
        if (account.withdraw(amount)) {
            System.out.println("Withdrew " + amount + ". New Balance: " + account.getBalance());
        } else {
            System.out.println("Insufficient funds!");
        }
    }

    @Override
    public void checkBalance() {
        System.out.println("Your Balance: " + account.getBalance());
    }

    @Override
    public void showTransactions() {
        System.out.println("Transaction History:");
        account.showTransactions();
    }
}

// ---- ATM Database ----
class ATMDatabase {
    private Map<String, BankAccount> accounts;

    public ATMDatabase() {
        accounts = new HashMap<>();
        // Pre-loaded accounts
        accounts.put("1001", new BankAccount("1001", "1234", 5000));
        accounts.put("1002", new BankAccount("1002", "5678", 3000));
    }

    public BankAccount login(String accNo, String pin) {
        BankAccount account = accounts.get(accNo);
        if (account != null && account.validatePin(pin)) {
            return account;
        }
        return null;
    }
}

// ---- Main Class ----
public class ATMSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ATMDatabase db = new ATMDatabase();

        System.out.println("----- Welcome to ATM -----");
        System.out.print("Enter Account Number: ");
        String accNo = sc.nextLine().trim();
        System.out.print("Enter PIN: ");
        String pin = sc.nextLine().trim();

        BankAccount account = db.login(accNo, pin);

        if (account == null) {
            System.out.println("Invalid Account or PIN!");
            return;
        }

        ATM atm = new ATMImpl(account);
        int choice = 0;

        while (choice != 5) {
            System.out.println("\n--- ATM Menu ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transaction History");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            // Handle input safely
            if (sc.hasNextInt()) {
                choice = sc.nextInt();
            } else {
                System.out.println("Invalid input! Please enter a number.");
                sc.next(); // clear invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    atm.checkBalance();
                    break;
                case 2:
                    System.out.print("Enter deposit amount: ");
                    double dep = sc.nextDouble();
                    atm.deposit(dep);
                    break;
                case 3:
                    System.out.print("Enter withdraw amount: ");
                    double wit = sc.nextDouble();
                    atm.withdraw(wit);
                    break;
                case 4:
                    atm.showTransactions();
                    break;
                case 5:
                    System.out.println("Thank you! Goodbye.");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }

        sc.close();
    }
}