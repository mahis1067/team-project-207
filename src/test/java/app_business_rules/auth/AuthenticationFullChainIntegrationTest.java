package app_business_rules.auth;

import app_business_rules.PasswordHasher;
import app_business_rules.login.LoginInteractor;
import app_business_rules.logout.LogoutInteractor;
import app_business_rules.refresh.RefreshFailureException;
import entities.users.User;
import interface_adapters.login.LoginController;
import interface_adapters.login.LoginPresenter;
import interface_adapters.login.LoginViewModel;
import interface_adapters.logout.LogoutController;
import interface_adapters.logout.LogoutPresenter;
import interface_adapters.logout.LogoutViewModel;
import interface_adapters.session.InMemorySessionState;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticationFullChainIntegrationTest {

    @Test
    void signupRefreshFailureReportsPartialSuccess() {
        UUID persistedId = UUID.randomUUID();
        AtomicInteger saveCalls = new AtomicInteger();
        AtomicInteger refreshCalls = new AtomicInteger();
        AtomicInteger stateEvents = new AtomicInteger();

        app_business_rules.signup.SignUpDataAccessInterface dataAccess =
                new app_business_rules.signup.SignUpDataAccessInterface() {
                    @Override
                    public boolean existsByUsername(final String username) {
                        return false;
                    }

                    @Override
                    public UUID saveUser(final User user) {
                        saveCalls.incrementAndGet();
                        return persistedId;
                    }
                };

        PasswordHasher hasher = createTestHasher();

        interface_adapters.signup.SignUpViewModel signupModel =
                new interface_adapters.signup.SignUpViewModel();

        signupModel.addPropertyChangeListener(
                event -> stateEvents.incrementAndGet());

        interface_adapters.signup.SignUpController signup =
                new interface_adapters.signup.SignUpController(
                        new app_business_rules.signup.SignUpInteractor(
                                dataAccess,
                                new interface_adapters.signup.SignUpPresenter(
                                        signupModel),
                                hasher,
                                input -> {
                                    refreshCalls.incrementAndGet();
                                    throw new RefreshFailureException(
                                            "Simulated refresh failure");
                                }));

        signup.execute("newuser", "password123", "Test", "User");

        assertFalse(signupModel.isSuccessful());
        assertNull(signupModel.getUserId());
        assertNull(signupModel.getUsername());
        assertEquals(
                "Account created, but data refresh failed. "
                        + "Please sign in again.",
                signupModel.getErrorMessage());

        assertEquals(1, saveCalls.get());
        assertEquals(1, refreshCalls.get());
        assertEquals(1, stateEvents.get());
    }

    @Test
    void duplicateSignupDoesNotSaveAnotherUser() {
        AtomicInteger saveCalls = new AtomicInteger();
        AtomicInteger refreshCalls = new AtomicInteger();
        AtomicInteger stateEvents = new AtomicInteger();

        app_business_rules.signup.SignUpDataAccessInterface dataAccess =
                new app_business_rules.signup.SignUpDataAccessInterface() {
                    @Override
                    public boolean existsByUsername(final String username) {
                        return "existinguser".equals(username);
                    }

                    @Override
                    public UUID saveUser(final User user) {
                        saveCalls.incrementAndGet();
                        return UUID.randomUUID();
                    }
                };

        PasswordHasher hasher = createTestHasher();

        interface_adapters.signup.SignUpViewModel signupModel =
                new interface_adapters.signup.SignUpViewModel();

        signupModel.addPropertyChangeListener(
                event -> stateEvents.incrementAndGet());

        interface_adapters.signup.SignUpController signup =
                new interface_adapters.signup.SignUpController(
                        new app_business_rules.signup.SignUpInteractor(
                                dataAccess,
                                new interface_adapters.signup.SignUpPresenter(
                                        signupModel),
                                hasher,
                                input -> refreshCalls.incrementAndGet()));

        signup.execute("existinguser", "password123", "Test", "User");

        assertFalse(signupModel.isSuccessful());
        assertNull(signupModel.getUserId());
        assertNull(signupModel.getUsername());
        assertEquals("Username already exists.",
                signupModel.getErrorMessage());
        assertEquals(0, saveCalls.get());
        assertEquals(0, refreshCalls.get());
        assertEquals(1, stateEvents.get());
    }

    @Test
    void signupLoginLogoutUsesPersistedUserId() {
        UUID persistedId = UUID.randomUUID();

        class FakeAuthDataAccess implements
                app_business_rules.signup.SignUpDataAccessInterface,
                app_business_rules.login.LoginDataAccessInterface {

            private User storedUser;

            @Override
            public boolean existsByUsername(final String username) {
                return storedUser != null
                        && storedUser.getUsername().equals(username);
            }

            @Override
            public UUID saveUser(final User user) {
                storedUser = new User(
                        persistedId,
                        user.getUsername(),
                        user.getPasswordHash(),
                        user.getFirstName(),
                        user.getLastName(),
                        new ArrayList<>());
                return persistedId;
            }

            @Override
            public User getUserByUsername(final String username) {
                return existsByUsername(username) ? storedUser : null;
            }
        }

        FakeAuthDataAccess dataAccess = new FakeAuthDataAccess();

        PasswordHasher hasher = createTestHasher();

        java.util.List<java.util.List<UUID>> refreshedUserIds =
                new ArrayList<>();
        app_business_rules.refresh.RefreshInputBoundary refresh =
                input -> refreshedUserIds.add(input.getUserIds());

        InMemorySessionState session = new InMemorySessionState();

        interface_adapters.signup.SignUpViewModel signupModel =
                new interface_adapters.signup.SignUpViewModel();
        LoginViewModel loginModel = new LoginViewModel();
        LogoutViewModel logoutModel = new LogoutViewModel();

        interface_adapters.signup.SignUpController signup =
                new interface_adapters.signup.SignUpController(
                        new app_business_rules.signup.SignUpInteractor(
                                dataAccess,
                                new interface_adapters.signup.SignUpPresenter(
                                        signupModel),
                                hasher,
                                refresh));

        LoginController login = new LoginController(
                new LoginInteractor(
                        dataAccess,
                        new LoginPresenter(loginModel),
                        hasher,
                        refresh,
                        session));

        LogoutController logout = new LogoutController(
                new LogoutInteractor(
                        session, new LogoutPresenter(logoutModel)));

        signup.execute("newuser", "password123", "Test", "User");

        assertTrue(signupModel.isSuccessful());
        assertEquals(persistedId, signupModel.getUserId());
        assertEquals("newuser", signupModel.getUsername());
        assertEquals(persistedId, dataAccess.storedUser.getUserId());
        assertEquals("hashed_password123",
                dataAccess.storedUser.getPasswordHash());
        assertNull(session.getCurrentUserId());

        login.execute("newuser", "password123");

        assertTrue(loginModel.isSuccessful());
        assertEquals(persistedId, loginModel.getUserId());
        assertEquals(persistedId, session.getCurrentUserId());

        logout.execute(persistedId);

        assertTrue(logoutModel.isSuccessful());
        assertEquals(persistedId, logoutModel.getLoggedOutUserId());
        assertNull(session.getCurrentUserId());
        assertEquals(2, refreshedUserIds.size());
        assertEquals(
                java.util.Collections.singletonList(persistedId),
                refreshedUserIds.get(0));
        assertEquals(
                java.util.Collections.singletonList(persistedId),
                refreshedUserIds.get(1));
    }

    @Test
    void wrongUserLogoutPreservesSession() {
        UUID activeUserId = UUID.randomUUID();
        UUID differentUserId = UUID.randomUUID();

        InMemorySessionState session = new InMemorySessionState();
        session.setCurrentUserId(activeUserId);

        LogoutViewModel logoutModel = new LogoutViewModel();
        AtomicInteger stateEvents = new AtomicInteger();

        logoutModel.addPropertyChangeListener(
                event -> stateEvents.incrementAndGet());

        LogoutController logout = new LogoutController(
                new LogoutInteractor(
                        session, new LogoutPresenter(logoutModel)));

        logout.execute(differentUserId);

        assertFalse(logoutModel.isSuccessful());
        assertNull(logoutModel.getLoggedOutUserId());
        assertEquals("No matching active session.",
                logoutModel.getErrorMessage());
        assertEquals(activeUserId, session.getCurrentUserId());
        assertEquals(1, stateEvents.get());
    }

    @Test
    void refreshFailureDoesNotCreateSession() {
        UUID userId = UUID.randomUUID();
        User user = new User(
                userId, "testuser", "hashed_password123",
                "Test", "User", new ArrayList<>());

        PasswordHasher hasher = createTestHasher();

        InMemorySessionState session = new InMemorySessionState();
        LoginViewModel loginModel = new LoginViewModel();
        AtomicInteger refreshCalls = new AtomicInteger();
        AtomicInteger stateEvents = new AtomicInteger();

        loginModel.addPropertyChangeListener(
                event -> stateEvents.incrementAndGet());

        LoginController login = new LoginController(
                new LoginInteractor(
                        username -> user.getUsername().equals(username)
                                ? user : null,
                        new LoginPresenter(loginModel),
                        hasher,
                        input -> {
                            refreshCalls.incrementAndGet();
                            throw new RefreshFailureException(
                                    "Simulated refresh failure");
                        },
                        session));

        login.execute("testuser", "password123");

        assertFalse(loginModel.isSuccessful());
        assertNull(loginModel.getUserId());
        assertNull(loginModel.getUsername());
        assertEquals(
                "Login failed because user data could not be refreshed.",
                loginModel.getErrorMessage());
        assertNull(session.getCurrentUserId());
        assertEquals(1, refreshCalls.get());
        assertEquals(1, stateEvents.get());
    }

    @Test
    void incorrectPasswordDoesNotCreateSession() {
        User user = new User(
                UUID.randomUUID(), "testuser", "hashed_password123",
                "Test", "User", new ArrayList<>());

        PasswordHasher hasher = createTestHasher();

        InMemorySessionState session = new InMemorySessionState();
        LoginViewModel loginModel = new LoginViewModel();
        AtomicInteger refreshCalls = new AtomicInteger();
        AtomicInteger stateEvents = new AtomicInteger();

        loginModel.addPropertyChangeListener(
                event -> stateEvents.incrementAndGet());

        LoginController login = new LoginController(
                new LoginInteractor(
                        username -> user.getUsername().equals(username)
                                ? user : null,
                        new LoginPresenter(loginModel),
                        hasher,
                        input -> refreshCalls.incrementAndGet(),
                        session));

        login.execute("testuser", "wrong_password");

        assertFalse(loginModel.isSuccessful());
        assertNull(loginModel.getUserId());
        assertNull(loginModel.getUsername());
        assertEquals("Invalid username or password.",
                loginModel.getErrorMessage());
        assertNull(session.getCurrentUserId());
        assertEquals(0, refreshCalls.get());
        assertEquals(1, stateEvents.get());
    }

    @Test
    void loginAndLogoutUpdateSessionAndViewModels() {
        UUID userId = UUID.randomUUID();
        User user = new User(
                userId, "testuser", "hashed_password123",
                "Test", "User", new ArrayList<>());

        PasswordHasher hasher = createTestHasher();

        InMemorySessionState session = new InMemorySessionState();
        LoginViewModel loginModel = new LoginViewModel();
        LogoutViewModel logoutModel = new LogoutViewModel();

        AtomicInteger loginEvents = new AtomicInteger();
        AtomicInteger logoutEvents = new AtomicInteger();
        AtomicInteger refreshCalls = new AtomicInteger();

        loginModel.addPropertyChangeListener(
                event -> loginEvents.incrementAndGet());
        logoutModel.addPropertyChangeListener(
                event -> logoutEvents.incrementAndGet());

        LoginController login = new LoginController(
                new LoginInteractor(
                        username -> user.getUsername().equals(username)
                                ? user : null,
                        new LoginPresenter(loginModel),
                        hasher,
                        input -> refreshCalls.incrementAndGet(),
                        session));

        LogoutController logout = new LogoutController(
                new LogoutInteractor(
                        session, new LogoutPresenter(logoutModel)));

        login.execute("testuser", "password123");

        assertTrue(loginModel.isSuccessful());
        assertEquals(userId, loginModel.getUserId());
        assertEquals("testuser", loginModel.getUsername());
        assertNull(loginModel.getErrorMessage());
        assertEquals(userId, session.getCurrentUserId());
        assertEquals(1, loginEvents.get());
        assertEquals(1, refreshCalls.get());

        logout.execute(userId);

        assertTrue(logoutModel.isSuccessful());
        assertEquals(userId, logoutModel.getLoggedOutUserId());
        assertNull(logoutModel.getErrorMessage());
        assertNull(session.getCurrentUserId());
        assertEquals(1, logoutEvents.get());
    }
    private static PasswordHasher createTestHasher() {
        return new PasswordHasher() {
            @Override
            public String hash(final String password) {
                return "hashed_" + password;
            }

            @Override
            public boolean verify(
                    final String password, final String hash) {
                return hash(password).equals(hash);
            }
        };
    }


}
