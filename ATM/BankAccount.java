public class BankAccount {
    String accountNumber;
    String holderName;
    String pin;
    double balance;

    public BankAccount(String accountNumber, String holderName, String pin, double balance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.pin = pin;
        this.balance = balance;
    }
}
