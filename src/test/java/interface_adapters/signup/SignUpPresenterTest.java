
package interface_adapters.signup;

import app_business_rules.signup.SignUpOutputData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the sign-up presenter and view model updates.
 */
class SignUpPresenterTest {

    /**
     * Verifies that a successful sign-up updates the view model.
     */
    @Test
    void testSuccessView() {
        SignUpViewModel viewModel = new SignUpViewModel();
        SignUpPresenter presenter = new SignUpPresenter(viewModel);

        UUID userId = UUID.randomUUID();
        SignUpOutputData outputData =
                new SignUpOutputData(userId, "testuser");

        presenter.prepareSuccessView(outputData);

        assertTrue(viewModel.isSuccessful());
        assertEquals(userId, viewModel.getUserId());
        assertEquals("testuser", viewModel.getUsername());
        assertNull(viewModel.getErrorMessage());
    }

    /**
     * Verifies that failed sign-up updates the view model.
     */
    @Test
    void testFailView() {
        SignUpViewModel viewModel = new SignUpViewModel();
        SignUpPresenter presenter = new SignUpPresenter(viewModel);

        presenter.prepareFailView("Username already exists.");

        assertFalse(viewModel.isSuccessful());
        assertEquals("Username already exists.",
                viewModel.getErrorMessage());
        assertNull(viewModel.getUserId());
        assertNull(viewModel.getUsername());
    }

    /**
     * Verifies that failure clears an earlier successful result.
     */
    @Test
    void testFailureAfterSuccess() {
        SignUpViewModel viewModel = new SignUpViewModel();
        SignUpPresenter presenter = new SignUpPresenter(viewModel);

        presenter.prepareSuccessView(
                new SignUpOutputData(UUID.randomUUID(), "testuser"));

        presenter.prepareFailView("All fields are required.");

        assertFalse(viewModel.isSuccessful());
        assertEquals("All fields are required.",
                viewModel.getErrorMessage());
        assertNull(viewModel.getUserId());
        assertNull(viewModel.getUsername());
    }
}
