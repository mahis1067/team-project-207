package interface_adapters.login;

import app_business_rules.login.LoginInputBoundary;
import app_business_rules.login.LoginInputData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the login controller.
 */
class LoginControllerTest {

    @Test
    void testExecute() {
        FakeLoginInteractor interactor = new FakeLoginInteractor();
        LoginController controller = new LoginController(interactor);

        controller.execute("testuser", "password123");

        assertNotNull(interactor.receivedInput);
        assertEquals("testuser", interactor.receivedInput.getUsername());
        assertEquals("password123", interactor.receivedInput.getPassword());
    }

    private static class FakeLoginInteractor implements LoginInputBoundary {

        private LoginInputData receivedInput;

        @Override
        public void execute(LoginInputData inputData) {
            receivedInput = inputData;
        }
    }
}
