package frameworks_and_drivers.view;

import interface_adapters.view_manager.ViewManagerModel;
import interface_adapters.view_manager.ViewNames;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Left-hand menu shared by the logged-in screens. */
class NavigationBar extends JPanel {

    /** Width of the menu in pixels. */
    private static final int MENU_WIDTH = 200;

    /** Used to switch screens when a menu item is clicked. */
    private final ViewManagerModel viewManagerModel;
    /** Name of the screen this menu sits on, shown highlighted. */
    private final String activeView;

    /**
     * Builds the menu.
     *
     * @param viewManagerModel the model used to switch screens
     * @param activeView the screen this menu is shown on
     */
    NavigationBar(ViewManagerModel viewManagerModel, String activeView) {
        this.viewManagerModel = viewManagerModel;
        this.activeView = activeView;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Theme.WHITE);
        setPreferredSize(new Dimension(MENU_WIDTH, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER),
                BorderFactory.createEmptyBorder(
                        Theme.PAD, Theme.GAP, Theme.PAD, Theme.GAP)));

        JLabel appName = Theme.label("Budget Tracker",
                Theme.HEADING, Theme.BLUE);
        appName.setBorder(
                BorderFactory.createEmptyBorder(0, Theme.GAP, Theme.PAD, 0));
        appName.setAlignmentX(LEFT_ALIGNMENT);
        add(appName);

        addItem("Dashboard", ViewNames.DASHBOARD);
        addItem("Friends", ViewNames.FRIENDS);
        addItem("Add expense", ViewNames.EXPENSE);
        addItem("Notifications", ViewNames.NOTIFICATIONS);
    }

    private void addItem(String text, String viewName) {
        JButton item = new JButton(text);
        item.setHorizontalAlignment(SwingConstants.LEFT);
        item.setFocusPainted(false);
        item.setOpaque(true);
        item.setBorder(BorderFactory.createEmptyBorder(
                Theme.GAP, Theme.GAP, Theme.GAP, Theme.GAP));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (viewName.equals(activeView)) {
            item.setFont(Theme.BODY.deriveFont(Font.BOLD));
            item.setBackground(Theme.BLUE_LIGHT);
            item.setForeground(Theme.BLUE);
        } else {
            item.setFont(Theme.BODY);
            item.setBackground(Theme.WHITE);
            item.setForeground(Theme.TEXT);
        }
        item.setAlignmentX(LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, item.getPreferredSize().height));
        item.addActionListener(e ->
                viewManagerModel.setActiveView(viewName));
        add(item);
    }
}
