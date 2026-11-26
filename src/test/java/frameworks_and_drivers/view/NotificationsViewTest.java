package frameworks_and_drivers.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import interface_adapters.view_manager.ViewManagerModel;
import interface_adapters.view_manager.ViewNames;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class NotificationsViewTest {

    @Test
    void emptyPageShowsCaughtUpMessage() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            NotificationsView view =
                    new NotificationsView(new ViewManagerModel());

            assertEquals(0, view.getPendingCount());
            JLabel empty = findLabel(view, "You're all caught up.");
            assertNotNull(empty);
            assertTrue(empty.isVisible());
        });
    }

    @Test
    void addingNotificationHidesCaughtUpMessage() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            NotificationsView view =
                    new NotificationsView(new ViewManagerModel());

            view.addFriendRequest("Yuntian");

            assertFalse(findLabel(view, "You're all caught up.").isVisible());
        });
    }

    @Test
    void acceptFriendRequestOpensFriends() throws Exception {
        assertButtonOpens(true, "Accept", ViewNames.FRIENDS);
    }

    @Test
    void denyFriendRequestOpensFriends() throws Exception {
        assertButtonOpens(true, "Deny", ViewNames.FRIENDS);
    }

    @Test
    void confirmExpenseOpensExpense() throws Exception {
        assertButtonOpens(false, "Confirm", ViewNames.EXPENSE);
    }

    @Test
    void denyExpenseOpensExpense() throws Exception {
        assertButtonOpens(false, "Deny", ViewNames.EXPENSE);
    }

    @Test
    void pendingCountIgnoresEarlierNotifications() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            NotificationsView view =
                    new NotificationsView(new ViewManagerModel());

            view.addExpense("Mahi", "Pizza night", "12.50");
            view.addFriendRequest("Yuntian");
            view.addEarlier("Jax", "confirmed your expense.", true);

            assertEquals(2, view.getPendingCount());
        });
    }

    @Test
    void earlierHeadingAppearsOnlyAfterEarlierNotification()
            throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            NotificationsView view =
                    new NotificationsView(new ViewManagerModel());
            JLabel heading = findLabel(view, "Earlier");

            assertFalse(heading.isVisible());
            view.addEarlier("Mahi", "denied your expense.", false);
            assertTrue(heading.isVisible());
        });
    }

    /**
     * Adds one friend request or expense, clicks a button on it and
     * checks which screen opens.
     */
    private static void assertButtonOpens(boolean friendRequest,
                                          String buttonText,
                                          String expectedView)
            throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ViewManagerModel navigation = new ViewManagerModel();
            navigation.setActiveView(ViewNames.NOTIFICATIONS);
            NotificationsView view = new NotificationsView(navigation);
            if (friendRequest) {
                view.addFriendRequest("Yuntian");
            } else {
                view.addExpense("Mahi", "Pizza night", "12.50");
            }

            JButton button = findButton(view, buttonText);
            assertNotNull(button);
            button.doClick();

            assertEquals(expectedView, navigation.getActiveView());
        });
    }

    private static JButton findButton(Component component, String text) {
        if (component instanceof JButton button
                && text.equals(button.getText())) {
            return button;
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                JButton found = findButton(child, text);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static JLabel findLabel(Component component, String text) {
        if (component instanceof JLabel label
                && text.equals(label.getText())) {
            return label;
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                JLabel found = findLabel(child, text);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}