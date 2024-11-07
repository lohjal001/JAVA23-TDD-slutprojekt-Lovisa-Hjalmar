import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ATMTest {


    private BankInterface mockBank;
    private ATM atm;
    private User mockUser;

    @BeforeEach
    void setup() {
        mockBank = mock(BankInterface.class);
        atm = new ATM(mockBank);
        mockUser = new User("1234", "5678", 1000.0);
    }



    //test for insert card not locked
    @Test
    @DisplayName("check card valid id and not locked")
    void testInsertCardSuccess() {
        when(mockBank.getUserById("1234")).thenReturn(mockUser);
        when(mockBank.isCardLocked("1234")).thenReturn(false);

        assertTrue(atm.insertCard("1234"), "Card insertion correct");
    }

    //test for wrong id
    @Test
    @DisplayName("check card with wrong id")
    void testInsertCardInvalidId() {
        when(mockBank.getUserById("9999")).thenReturn(null);

        assertFalse(atm.insertCard("9999"), "Card with invalid id");
    }

    //test for locked card
    @Test
    @DisplayName("check card when locked")
    void testInsertCardWhenLocked() {
        when(mockBank.getUserById("1234")).thenReturn(mockUser);
        when(mockBank.isCardLocked("1234")).thenReturn(true);

        assertTrue(atm.insertCard("1234"), "Card is locked");
    }



    //card login correct pin
    @Test
    @DisplayName("check login correct pin")
    void testEnterPinCorrect() {
        when(mockBank.getUserById("1234")).thenReturn(mockUser);
        when(mockBank.verifyPin(mockUser, "5678")).thenReturn(true);

        atm.insertCard("1234");

        assertTrue(atm.enterPin("5678"));
        verify(mockBank).verifyPin(mockUser, "5678");
        verify(mockBank).resetFailedAttempts(mockUser);
    }

    //test increase attempts wrong pin
    @Test
    @DisplayName("check incorrect pin more attempts")
    void testEnterPinIncorrect() {
        when(mockBank.getUserById("1234")).thenReturn(mockUser);
        when(mockBank.verifyPin(mockUser, "0000")).thenReturn(false);

        atm.insertCard("1234");
        assertFalse(atm.enterPin("0000"));
        verify(mockBank).incrementFailedAttempts(mockUser);
    }

    //test for locking card after 3 attempts
    @Test
    @DisplayName("check card locking after three attempts")
    void testEnterPinIncorrectThreeTimesLocksCard() {
        int[] failedAttempts = {0};

        when(mockBank.getUserById("1234")).thenReturn(mockUser);
        when(mockBank.verifyPin(mockUser, "0000")).thenReturn(false);

        // increase attempts  in the mock
        doAnswer(invocation -> {
            failedAttempts[0]++;
            return null;
        }).when(mockBank).incrementFailedAttempts(mockUser);
        when(mockBank.getFailedAttempts(mockUser)).thenAnswer(invocation -> failedAttempts[0]);

        atm.insertCard("1234");

        // enters incorrect pin 3 times
        for (int i = 0; i < 3; i++) {
            atm.enterPin("0000");
        }

        verify(mockBank, times(3)).incrementFailedAttempts(mockUser);
        verify(mockBank).lockCard(mockUser);
    }



    //test for checking balance
    @Test
    @DisplayName("checking balance")
    void testCheckBalanceLoggedIn() {
        when(mockBank.getUserById("1234")).thenReturn(mockUser);
        when(mockBank.verifyPin(mockUser, "5678")).thenReturn(true);
        when(mockBank.getBalance(mockUser)).thenReturn(1000.0);

        atm.insertCard("1234");
        atm.enterPin("5678");

        assertEquals(1000.0, atm.checkBalance());
    }



        //test for checking balance not logged in
    @Test
    @DisplayName("Check balance without logging in")
    void testCheckBalanceWithoutLogin() {
        assertEquals(0.0, atm.checkBalance(), "Balance should be 0 when not logged in");
    }

    //test for depositing money while logged in
    @Test
    @DisplayName("Deposit money while logged in")
    void testDepositLoggedIn() {
        when(mockBank.getUserById("1234")).thenReturn(mockUser);

        atm.insertCard("1234");
        atm.deposit(200.0);

        verify(mockBank).deposit(mockUser, 200.0);
    }

    //test for depositing money while logged out
    @Test
    @DisplayName("Acheck depositing not logged in")
    void testDepositWithoutLogin() {
        atm.deposit(200.0);
        verify(mockBank, never()).deposit(mockUser, 200.0);
    }


        //test for withdrawing money sufficient balance
    @Test
    @DisplayName("check withdrawing money with sufficient balance")
    void testWithdrawWithSufficientBalance() {
        when(mockBank.getUserById("1234")).thenReturn(mockUser);
        when(mockBank.verifyPin(mockUser, "5678")).thenReturn(true);
        when(mockBank.withdraw(mockUser, 100.0)).thenReturn(true);

        atm.insertCard("1234");
        atm.enterPin("5678");

        assertTrue(atm.withdraw(100.0));
        verify(mockBank).withdraw(mockUser, 100.0);
    }

    //test withdrawing money with insufficient balance
    @Test
    @DisplayName("checking withdraw money with insufficient balance")
    void testWithdrawWithInsufficientBalance() {
        when(mockBank.getUserById("1234")).thenReturn(mockUser);
        when(mockBank.verifyPin(mockUser, "5678")).thenReturn(true);
        when(mockBank.withdraw(mockUser, 1500.0)).thenReturn(false);

        atm.insertCard("1234");
        atm.enterPin("5678");

        assertFalse(atm.withdraw(1500.0));
        verify(mockBank).withdraw(mockUser, 1500.0);
    }


    //test withdrawing money not logged in
    @Test
    @DisplayName("check attempt withdrawing money not logging in")
    void testWithdrawWithoutLogin() {
        assertFalse(atm.withdraw(100.0), "Withdrawal should fail when not logged in");
        verify(mockBank, never()).withdraw(mockUser, 100.0);
    }
}
