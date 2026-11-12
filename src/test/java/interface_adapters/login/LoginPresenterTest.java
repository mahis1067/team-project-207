package interface_adapters.login;

import app_business_rules.login.LoginOutputData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the login presenter and view model updates.
 */
class LoginPresenterTest {

    @Test
    void testSuccessView() {
        LoginViewModel viewModel = new LoginViewModel();
        LoginPresenter presenter = new LoginPresenter(viewModel);

        UUID userId = UUID.randomUUID();
        presenter.prepareSuccessView(
                new LoginOutputData(userId, "testuser"));

        assertTrue(viewModel.isSuccessful());
        assertEquals(userId, viewModel.getUserId());
        assertEquals("testuser", viewModel.getUsername());
        assertNull(viewModel.getErrorMessage());
    }

    @Test
    void testFailView() {
        LoginViewModel viewModel = new LoginViewModel();
        LoginPresenter presenter = new LoginPresenter(viewModel);

        presenter.prepareFailView("Invalid username or password.");

        assertFalse(viewModel.isSuccessful());
        assertEquals("Invalid username or password.",
                viewModel.getErrorMessage());
        assertNull(viewModel.getUserId());
        assertNull(viewModel.getUsername());
    }

    @Test
    void testFailureAfterSuccess() {
        LoginViewModel viewModel = new LoginViewModel();
        LoginPresenter presenter = new LoginPresenter(viewModel);

        presenter.prepareSuccessView(
                new LoginOutputData(UUID.randomUUID(), "testuser"));

        presenter.prepareFailView("Username and password are required.");

        assertFalse(viewModel.isSuccessful());
        assertEquals("Username and password are required.",
                viewModel.getErrorMessage());
        assertNull(viewModel.getUserId());
        assertNull(viewModel.getUsername());
    }
}
