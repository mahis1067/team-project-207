package app_business_rules.logout;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the logout use case.
 */
class LogoutInteractorTest {

    @Test
    void testSuccessfulLogout() {
        TestContext context = new TestContext();

        context.interactor.execute(
                new LogoutInputData(context.userId));

        assertEquals(1, context.session.clearCount);
        assertNull(context.session.currentUserId);
        assertNotNull(context.presenter.successData);
        assertEquals(context.userId,
                context.presenter.successData.getUserId());
        assertNull(context.presenter.errorMessage);
    }

    @Test
    void testNullInput() {
        TestContext context = new TestContext();

        context.interactor.execute(null);

        assertFailure(context, "User ID is required.");
    }

    @Test
    void testNullUserId() {
        TestContext context = new TestContext();

        context.interactor.execute(new LogoutInputData(null));

        assertFailure(context, "User ID is required.");
    }

    @Test
    void testNoActiveSession() {
        TestContext context = new TestContext();
        context.session.currentUserId = null;

        context.interactor.execute(
                new LogoutInputData(context.userId));

        assertFailure(context, "No matching active session.");
    }

    @Test
    void testDifferentUser() {
        TestContext context = new TestContext();

        context.interactor.execute(
                new LogoutInputData(UUID.randomUUID()));

        assertFailure(context, "No matching active session.");
        assertEquals(context.userId,
                context.session.currentUserId);
    }

    @Test
    void testSessionClearFailure() {
        TestContext context = new TestContext();
        context.session.failOnClear = true;

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> context.interactor.execute(
                        new LogoutInputData(context.userId)));

        assertEquals("Simulated session failure",
                exception.getMessage());
        assertNull(context.presenter.successData);
        assertEquals(context.userId,
                context.session.currentUserId);
    }

    private static void assertFailure(
            TestContext context, String expectedMessage) {
        assertEquals(expectedMessage,
                context.presenter.errorMessage);
        assertNull(context.presenter.successData);
        assertEquals(0, context.session.clearCount);
    }

    private static class TestContext {
        private final UUID userId = UUID.randomUUID();

        private final FakeSession session =
                new FakeSession(userId);

        private final FakePresenter presenter =
                new FakePresenter();

        private final LogoutInteractor interactor =
                new LogoutInteractor(session, presenter);
    }

    private static class FakeSession implements SessionState {
        private UUID currentUserId;
        private int clearCount;
        private boolean failOnClear;

        FakeSession(UUID userId) {
            currentUserId = userId;
        }

        @Override
        public UUID getCurrentUserId() {
            return currentUserId;
        }

        @Override
        public void setCurrentUserId(UUID userId) {
            currentUserId = userId;
        }

        @Override
        public void clear() {
            clearCount++;

            if (failOnClear) {
                throw new IllegalStateException(
                        "Simulated session failure");
            }

            currentUserId = null;
        }
    }

    private static class FakePresenter
            implements LogoutOutputBoundary {

        private LogoutOutputData successData;
        private String errorMessage;

        @Override
        public void prepareSuccessView(LogoutOutputData outputData) {
            successData = outputData;
        }

        @Override
        public void prepareFailView(String message) {
            errorMessage = message;
        }
    }
}
