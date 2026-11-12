package interface_adapters.logout;

import app_business_rules.logout.LogoutOutputData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LogoutPresenterTest {

    @Test
    void testSuccessView() {
        LogoutViewModel viewModel = new LogoutViewModel();
        LogoutPresenter presenter = new LogoutPresenter(viewModel);
        UUID userId = UUID.randomUUID();

        presenter.prepareSuccessView(new LogoutOutputData(userId));

        assertTrue(viewModel.isSuccessful());
        assertEquals(userId, viewModel.getLoggedOutUserId());
        assertNull(viewModel.getErrorMessage());
    }

    @Test
    void testFailView() {
        LogoutViewModel viewModel = new LogoutViewModel();
        LogoutPresenter presenter = new LogoutPresenter(viewModel);

        presenter.prepareFailView("No matching active session.");

        assertFalse(viewModel.isSuccessful());
        assertNull(viewModel.getLoggedOutUserId());
        assertEquals("No matching active session.",
                viewModel.getErrorMessage());
    }

    @Test
    void testFailureAfterSuccess() {
        LogoutViewModel viewModel = new LogoutViewModel();
        LogoutPresenter presenter = new LogoutPresenter(viewModel);

        presenter.prepareSuccessView(
                new LogoutOutputData(UUID.randomUUID()));
        presenter.prepareFailView("User ID is required.");

        assertFalse(viewModel.isSuccessful());
        assertNull(viewModel.getLoggedOutUserId());
        assertEquals("User ID is required.",
                viewModel.getErrorMessage());
    }

    @Test
    void testSuccessAfterFailure() {
        LogoutViewModel viewModel = new LogoutViewModel();
        LogoutPresenter presenter = new LogoutPresenter(viewModel);
        UUID userId = UUID.randomUUID();

        presenter.prepareFailView("Previous error");
        presenter.prepareSuccessView(new LogoutOutputData(userId));

        assertTrue(viewModel.isSuccessful());
        assertEquals(userId, viewModel.getLoggedOutUserId());
        assertNull(viewModel.getErrorMessage());
    }
}
