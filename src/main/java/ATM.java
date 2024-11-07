public class ATM {
    private BankInterface bank;
    private User currentUser;

    public ATM(BankInterface bank) {
        this.bank = bank;
    }

    public boolean insertCard(String userId) {
        currentUser = bank.getUserById(userId);
        return currentUser != null && !currentUser.isLocked();
    }

    public boolean enterPin(String pin) {
        if (bank.verifyPin(currentUser, pin)) {
            bank.resetFailedAttempts(currentUser);  // reset attempts on success
            return true;
        } else {
            bank.incrementFailedAttempts(currentUser);
            if (bank.getFailedAttempts(currentUser) >= 3) {
                bank.lockCard(currentUser);
            }
            return false;
        }
    }


    public double checkBalance() {
        return currentUser != null ? bank.getBalance(currentUser) : 0.0;
    }

    public void deposit(double amount) {
        if (currentUser != null) {
            bank.deposit(currentUser, amount);
        }
    }

    public boolean withdraw(double amount) {
        return currentUser != null && bank.withdraw(currentUser, amount);
    }
}