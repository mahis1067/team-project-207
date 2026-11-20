
package interface_adapters.signup;

import app_business_rules.signup.SignUpInputBoundary;
import app_business_rules.signup.SignUpInputData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests the sign-up controller.
 */
class SignUpControllerTest {

    /**
     * Verifies that the controller forwards registration data.
     */
    @Test
    void testExecute() {
        FakeSignUpInteractor interactor = new FakeSignUpInteractor();
        SignUpController controller = new SignUpController(interactor);

        controller.execute("testuser", "password123", "Test", "User");

        assertNotNull(interactor.receivedInput);
        assertEquals("testuser", interactor.receivedInput.getUsername());
        assertEquals("password123", interactor.receivedInput.getPassword());
        assertEquals("Test", interactor.receivedInput.getFirstName());
        assertEquals("User", interactor.receivedInput.getLastName());
    }

    /**
     * Records the input received from the controller.
     */
    private static class FakeSignUpInteractor implements SignUpInputBoundary {

        private SignUpInputData receivedInput;

        @Override
        public void execute(SignUpInputData inputData) {
            this.receivedInput = inputData;
        }
    }
}
