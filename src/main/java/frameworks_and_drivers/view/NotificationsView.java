package frameworks_and_drivers.view;

import interface_adapters.view_manager.ViewManagerModel;
import interface_adapters.view_manager.ViewNames;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Screen listing friend requests and expenses that need the user's
 * answer, plus answers the user already received.
 */
public class NotificationsView extends JPanel {

    /** Used to send the user to the right screen. */
    private final ViewManagerModel viewManagerModel;
    /** Width of every notification button, so the cards line up. */
    private static final int BUTTON_WIDTH = 110;
    /** Width of the Confirmed / Denied pills, so the messages line up. */
    private static final int PILL_WIDTH = 100;
    /** Notifications that still need an answer. */
    private final JPanel actionCards = verticalPanel();
    /** Notifications that were already answered. */
    private final JPanel earlierCards = verticalPanel();
    /** Shown when nothing needs an answer. */
    private final JLabel emptyLabel =
            Theme.label("You're all caught up.", Theme.BODY, Theme.MUTED);
    /** Heading above the answered notifications. */
    private final JLabel earlierLabel =
            Theme.label("Earlier", Theme.HEADING, Theme.MUTED);
    /** Number of notifications that still need an answer. */
    private int pendingCount;

    /**
     * Builds the notifications screen.
     *
     * @param viewManagerModel the model used to switch screens
     */
    public NotificationsView(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        add(new NavigationBar(viewManagerModel, ViewNames.NOTIFICATIONS),
                BorderLayout.WEST);

        earlierLabel.setVisible(false);
        earlierLabel.setBorder(
                BorderFactory.createEmptyBorder(Theme.PAD, 0, Theme.GAP, 0));
        emptyLabel.setAlignmentX(LEFT_ALIGNMENT);
        earlierLabel.setAlignmentX(LEFT_ALIGNMENT);
        actionCards.setAlignmentX(LEFT_ALIGNMENT);
        earlierCards.setAlignmentX(LEFT_ALIGNMENT);

        JPanel body = verticalPanel();
        body.add(emptyLabel);
        body.add(actionCards);
        body.add(earlierLabel);
        body.add(earlierCards);

        JPanel holder = new JPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.add(body, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(holder);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        JPanel content = new JPanel(new BorderLayout(0, Theme.PAD));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(
                Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD));
        content.add(Theme.label("Notifications", Theme.TITLE, Theme.TEXT),
                BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
    }

    /**
     * Shows a friend request. Both buttons open the Friends screen.
     *
     * @param name the person who sent the request
     */
    public void addFriendRequest(String name) {
        addActionCard("Friend request",
                bold(name) + " wants to add you as a friend.",
                "Accept", "Deny", ViewNames.FRIENDS);
    }

    /**
     * Shows an expense the user needs to confirm. Both buttons open the
     * Expense screen.
     *
     * @param name the person who added the expense
     * @param description what the expense was for
     * @param share the user's share, e.g. "12.50"
     */
    public void addExpense(String name, String description, String share) {
        addActionCard("Expense",
                bold(name) + " added \u201C" + description
                        + "\u201D. Your share is $" + share + ".",
                "Confirm", "Deny", ViewNames.EXPENSE);
    }

    /**
     * Shows an answer the user already received, under "Earlier".
     *
     * @param name the person who answered
     * @param message the rest of the message
     * @param confirmed true for a green Confirmed tag, false for red Denied
     */
    public void addEarlier(String name, String message, boolean confirmed) {
        JComponent pill;
        if (confirmed) {
            pill = Theme.tag("Confirmed", Theme.GREEN, Theme.TEXT);
        } else {
            pill = Theme.tag("Denied", Theme.RED, Theme.WHITE);
        }
        sizeToWidth(pill, PILL_WIDTH);
        JPanel pillHolder = new JPanel(new GridBagLayout());
        pillHolder.setOpaque(false);
        pillHolder.add(pill);

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(Theme.PAD, 0));
        card.add(pillHolder, BorderLayout.WEST);
        card.add(html(bold(name) + " " + message), BorderLayout.CENTER);
        addCard(earlierCards, card);
        earlierLabel.setVisible(true);
    }

    /**
     * Returns how many notifications still need an answer.
     *
     * @return the number of pending notifications
     */
    public int getPendingCount() {
        return pendingCount;
    }

    private void addActionCard(String type, String message, String yesText,
                               String noText, String targetView) {
        JPanel tagRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tagRow.setOpaque(false);
        tagRow.add(Theme.tag(type, Theme.CARD_BORDER, Theme.MUTED));

        JPanel text = new JPanel(new BorderLayout(0, Theme.GAP));
        text.setOpaque(false);
        text.add(tagRow, BorderLayout.NORTH);
        text.add(html(message), BorderLayout.CENTER);

        JButton yesButton = Theme.primaryButton(yesText);
        JButton noButton = Theme.secondaryButton(noText);
        sizeToWidth(yesButton, BUTTON_WIDTH);
        sizeToWidth(noButton, BUTTON_WIDTH);
        // TODO: once the confirm expense / friend request use cases are
        //  wired here, call their controllers instead of only navigating.
        yesButton.addActionListener(
                e -> viewManagerModel.setActiveView(targetView));
        noButton.addActionListener(
                e -> viewManagerModel.setActiveView(targetView));

        JPanel buttons = new JPanel(new GridLayout(1, 2, Theme.GAP, 0));
        buttons.setOpaque(false);
        buttons.add(yesButton);
        buttons.add(noButton);
        JPanel buttonHolder = new JPanel(new GridBagLayout());
        buttonHolder.setOpaque(false);
        buttonHolder.add(buttons);

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(Theme.PAD, 0));
        card.add(text, BorderLayout.CENTER);
        card.add(buttonHolder, BorderLayout.EAST);
        addCard(actionCards, card);

        pendingCount++;
        emptyLabel.setVisible(false);
    }

    private static void addCard(JPanel list, JPanel card) {
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, card.getPreferredSize().height));
        if (list.getComponentCount() > 0) {
            list.add(Box.createVerticalStrut(Theme.GAP));
        }
        list.add(card);
        list.revalidate();
        list.repaint();
    }

    private static void sizeToWidth(JComponent component, int width) {
        component.setPreferredSize(new Dimension(
                width, component.getPreferredSize().height));
    }

    private static JPanel verticalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        return panel;
    }

    private static JLabel html(String text) {
        return Theme.label("<html>" + text + "</html>",
                Theme.BODY, Theme.TEXT);
    }

    private static String bold(String text) {
        return "<b>" + text + "</b>";
    }
}