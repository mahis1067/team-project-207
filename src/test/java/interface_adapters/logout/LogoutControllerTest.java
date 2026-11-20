package interface_adapters.logout;

import app_business_rules.logout.LogoutInputBoundary;
import app_business_rules.logout.LogoutInputData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LogoutControllerTest {

    @Test
    void testExecute() {
        FakeLogoutInteractor interactor = new FakeLogoutInteractor();
        LogoutController controller = new LogoutController(interactor);

        UUID userId = UUID.randomUUID();
        controller.execute(userId);

        assertNotNull(interactor.receivedInput);
        assertEquals(userId, interactor.receivedInput.getUserId());
    }

    private static class FakeLogoutInteractor
            implements LogoutInputBoundary {

        private LogoutInputData receivedInput;

        @Override
        public void execute(LogoutInputData inputData) {
            receivedInput = inputData;
        }
    }
}
