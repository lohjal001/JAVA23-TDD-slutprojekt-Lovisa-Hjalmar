public interface BankInterface {
    User getUserById(String id);
    boolean verifyPin(User user, String pin);
    double getBalance(User user);
    boolean isCardLocked(String userId);
    void incrementFailedAttempts(User user);
    void resetFailedAttempts(User user);
    void lockCard(User user);
    void deposit(User user, double amount);
    boolean withdraw(User user, double amount);
    int getFailedAttempts(User user); // change to int
    boolean isCardLocked(User user);
}
