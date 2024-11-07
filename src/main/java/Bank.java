import java.util.HashMap;
import java.util.Map;

public class Bank implements BankInterface {
    private Map<String, User> users = new HashMap<>();

    @Override
    public User getUserById(String id) {
        return users.get(id);
    }

    @Override
    public boolean verifyPin(User user, String pin) {
        return user.getPin().equals(pin);
    }

    @Override
    public int getFailedAttempts(User user) {
        return user.getFailedAttempts();
    }

    @Override
    public double getBalance(User user) {
        return user.getBalance();
    }

    @Override
    public boolean isCardLocked(String userId) {
        User user = users.get(userId);
        return user != null && user.isLocked();
    }

    @Override
    public void incrementFailedAttempts(User user) {
        user.incrementFailedAttempts();
    }

    @Override
    public void resetFailedAttempts(User user) {
        user.resetFailedAttempts();
    }

    @Override
    public void lockCard(User user) {
        user.lockCard();
    }


    @Override
    public boolean isCardLocked(User user) {
        return user.isLocked();
    }

    @Override
    public void deposit(User user, double amount) {
        user.deposit(amount);
    }

    @Override
    public boolean withdraw(User user, double amount) {
        if (user.getBalance() >= amount) {
            user.withdraw(amount);
            return true;
        }
        return false;
    }

    public static String getBankName() {
        return "MockBank";
    }
}