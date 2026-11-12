package app_business_rules.refresh;

import entities.users.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RefreshInteractorTest {

    @Test
    void testSuccessfulRefresh() {
        TestContext context = new TestContext();

        context.interactor.execute(new RefreshInputData(
                Collections.singletonList(context.user.getUserId())));

        assertNotNull(context.presenter.successData);
        assertNull(context.presenter.errorMessage);
        assertEquals(1,
                context.presenter.successData.getUsers().size());

        RefreshOutputData.UserSnapshot snapshot =
                context.presenter.successData.getUsers().get(0);

        assertEquals(context.user.getUserId(), snapshot.getUserId());
        assertEquals("testuser", snapshot.getUsername());
        assertEquals("Test", snapshot.getFirstName());
        assertEquals("User", snapshot.getLastName());
    }

    @Test
    void testNullInput() {
        TestContext context = new TestContext();

        assertThrows(RefreshFailureException.class,
                () -> context.interactor.execute(null));

        assertFailure(context, "At least one user ID is required.");
    }

    @Test
    void testEmptyUserList() {
        TestContext context = new TestContext();

        assertThrows(RefreshFailureException.class,
                () -> context.interactor.execute(
                        new RefreshInputData(Collections.emptyList())));

        assertFailure(context, "At least one user ID is required.");
    }

    @Test
    void testNullUserId() {
        TestContext context = new TestContext();

        assertThrows(RefreshFailureException.class,
                () -> context.interactor.execute(
                        new RefreshInputData(Arrays.asList((UUID) null))));

        assertFailure(context, "User ID cannot be null.");
    }

    @Test
    void testUnknownUser() {
        TestContext context = new TestContext();

        assertThrows(RefreshFailureException.class,
                () -> context.interactor.execute(new RefreshInputData(
                        Collections.singletonList(UUID.randomUUID()))));

        assertFailure(context, "User not found.");
    }

    @Test
    void testMultipleUsers() {
        TestContext context = new TestContext();

        UUID secondId = UUID.randomUUID();
        User secondUser = new User(
                secondId, "second", "hash",
                "Second", "User", new ArrayList<UUID>());

        RefreshDataAccessInterface dao = id -> {
            if (id.equals(context.user.getUserId())) {
                return context.user;
            }
            if (id.equals(secondId)) {
                return secondUser;
            }
            return null;
        };

        RefreshInteractor interactor =
                new RefreshInteractor(dao, context.presenter);

        interactor.execute(new RefreshInputData(
                Arrays.asList(context.user.getUserId(), secondId)));

        assertNotNull(context.presenter.successData);
        assertEquals(2,
                context.presenter.successData.getUsers().size());
        assertEquals("second",
                context.presenter.successData.getUsers()
                        .get(1).getUsername());
    }

    private static void assertFailure(
            TestContext context, String expectedError) {
        assertEquals(expectedError, context.presenter.errorMessage);
        assertNull(context.presenter.successData);
    }

    private static class TestContext {
        private final User user = new User(
                UUID.randomUUID(),
                "testuser",
                "secret_hash",
                "Test",
                "User",
                new ArrayList<UUID>());

        private final FakePresenter presenter = new FakePresenter();

        private final RefreshInteractor interactor =
                new RefreshInteractor(
                        id -> user.getUserId().equals(id) ? user : null,
                        presenter);
    }

    private static class FakePresenter
            implements RefreshOutputBoundary {

        private RefreshOutputData successData;
        private String errorMessage;

        @Override
        public void prepareSuccessView(RefreshOutputData outputData) {
            successData = outputData;
        }

        @Override
        public void prepareFailView(String message) {
            errorMessage = message;
        }
    }
}
