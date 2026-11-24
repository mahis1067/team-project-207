package frameworks_and_drivers.view;

import interface_adapters.view_manager.ViewManagerModel;
import interface_adapters.view_manager.ViewNames;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/** Screen for sending and answering friend requests. */
public class FriendsView extends JPanel {

    /** Username of the friend to add. */
    private final JTextField usernameField = Theme.textField();
    /** Incoming friend requests, one name per row. */
    private final DefaultListModel<String> requests =
            new DefaultListModel<>();
    /** The user's friends, one per row. */
    private final DefaultListModel<String> friends =
            new DefaultListModel<>();
    /** Shows feedback after a button is clicked. */
    private final JLabel statusLabel =
            Theme.label(" ", Theme.SMALL, Theme.MUTED);

    /**
     * Builds the friends screen.
     *
     * @param viewManagerModel the model used to switch screens
     */
    public FriendsView(ViewManagerModel viewManagerModel) {
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        add(new NavigationBar(viewManagerModel, ViewNames.FRIENDS),
                BorderLayout.WEST);

        JPanel top = new JPanel(new BorderLayout(0, Theme.PAD));
        top.setOpaque(false);
        top.add(Theme.label("Friends", Theme.TITLE, Theme.TEXT),
                BorderLayout.NORTH);
        top.add(buildAddFriendCard(), BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout(0, Theme.PAD));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(
                Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD));
        content.add(top, BorderLayout.NORTH);
        content.add(buildLists(), BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
    }

    private JPanel buildAddFriendCard() {
        JButton sendButton = Theme.primaryButton("Send request");
        sendButton.addActionListener(e -> sendRequest());

        JPanel buttonHolder = new JPanel(new BorderLayout());
        buttonHolder.setOpaque(false);
        buttonHolder.add(sendButton, BorderLayout.SOUTH);

        JPanel row = new JPanel(new BorderLayout(Theme.GAP, 0));
        row.setOpaque(false);
        row.add(new LabelledField("Their username", usernameField),
                BorderLayout.CENTER);
        row.add(buttonHolder, BorderLayout.EAST);

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, Theme.GAP));
        card.add(Theme.label("Add a friend", Theme.HEADING, Theme.TEXT),
                BorderLayout.NORTH);
        card.add(row, BorderLayout.CENTER);
        card.add(statusLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildLists() {
        JButton acceptButton = Theme.primaryButton("Accept");
        JButton denyButton = Theme.secondaryButton("Deny");
        acceptButton.addActionListener(e -> acceptRequest());
        denyButton.addActionListener(e -> denyRequest());

        JPanel answerButtons = new JPanel(new GridLayout(1, 2, Theme.GAP, 0));
        answerButtons.setOpaque(false);
        answerButtons.add(acceptButton);
        answerButtons.add(denyButton);

        JPanel requestsCard = listCard("Incoming requests", requests);
        requestsCard.add(answerButtons, BorderLayout.SOUTH);

        JPanel lists = new JPanel(new GridLayout(1, 2, Theme.PAD, 0));
        lists.setOpaque(false);
        lists.add(requestsCard);
        lists.add(listCard("Your friends", friends));
        return lists;
    }

    private void sendRequest() {
        // TODO (friend requests use case): call
        //  sendFriendRequestController.execute(currentUserId, username)
        statusLabel.setText("Sending friend requests is coming soon.");
    }

    private void acceptRequest() {
        // TODO (friend requests use case): call
        //  acceptFriendRequestController.execute(friendshipId)
        statusLabel.setText("Accepting requests is coming soon.");
    }

    private void denyRequest() {
        // TODO (friend requests use case): call
        //  denyFriendRequestController.execute(userId, requesterId)
        statusLabel.setText("Denying requests is coming soon.");
    }

    private static JPanel listCard(String title,
                                   DefaultListModel<String> items) {
        JList<String> list = new JList<>(items);
        list.setFont(Theme.BODY);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, Theme.GAP));
        card.add(Theme.label(title, Theme.HEADING, Theme.TEXT),
                BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }
}
