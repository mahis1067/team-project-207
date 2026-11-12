package interface_adapters.refresh;

import app_business_rules.refresh.RefreshOutputData;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RefreshPresenterTest {

    @Test
    void testSuccessView() {
        RefreshViewModel viewModel = new RefreshViewModel();
        RefreshPresenter presenter = new RefreshPresenter(viewModel);

        UUID userId = UUID.randomUUID();
        RefreshOutputData.UserSnapshot user =
                new RefreshOutputData.UserSnapshot(
                        userId, "testuser", "Test", "User");

        presenter.prepareSuccessView(new RefreshOutputData(
                Collections.singletonList(user)));

        assertTrue(viewModel.isSuccessful());
        assertNull(viewModel.getErrorMessage());
        assertEquals(1, viewModel.getUsers().size());
        assertEquals(userId, viewModel.getUsers().get(0).getUserId());
    }

    @Test
    void testFailureView() {
        RefreshViewModel viewModel = new RefreshViewModel();
        RefreshPresenter presenter = new RefreshPresenter(viewModel);

        presenter.prepareFailView("User not found.");

        assertFalse(viewModel.isSuccessful());
        assertEquals("User not found.", viewModel.getErrorMessage());
    }

    @Test
    void testSuccessAfterFailure() {
        RefreshViewModel viewModel = new RefreshViewModel();
        RefreshPresenter presenter = new RefreshPresenter(viewModel);

        presenter.prepareFailView("Previous error");
        presenter.prepareSuccessView(
                new RefreshOutputData(Collections.emptyList()));

        assertTrue(viewModel.isSuccessful());
        assertNull(viewModel.getErrorMessage());
        assertTrue(viewModel.getUsers().isEmpty());
    }

    @Test
    void testFailurePreservesPreviousData() {
        RefreshViewModel viewModel = new RefreshViewModel();
        RefreshPresenter presenter = new RefreshPresenter(viewModel);

        RefreshOutputData.UserSnapshot user =
                new RefreshOutputData.UserSnapshot(
                        UUID.randomUUID(), "testuser", "Test", "User");

        presenter.prepareSuccessView(new RefreshOutputData(
                Arrays.asList(user)));

        presenter.prepareFailView("Temporary refresh failure");

        assertFalse(viewModel.isSuccessful());
        assertEquals("Temporary refresh failure",
                viewModel.getErrorMessage());
        assertEquals(1, viewModel.getUsers().size());
    }
}
