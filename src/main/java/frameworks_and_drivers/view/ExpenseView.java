package frameworks_and_drivers.view;

import interface_adapters.view_manager.ViewManagerModel;
import interface_adapters.view_manager.ViewNames;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Screen for recording a shared expense. */
public class ExpenseView extends JPanel {

    /** Categories the user can pick from. */
    private static final String[] CATEGORIES = {
            "Food", "Transport", "Rent & bills", "Fun", "Other",
    };
    /** Used to switch screens. */
    private final ViewManagerModel viewManagerModel;
    /** What the expense was for. */
    private final JTextField descriptionField = Theme.textField();
    /** Total amount paid. */
    private final JTextField amountField = Theme.textField();
    /** Who paid. */
    private final JComboBox<String> paidByBox =
            styledBox(new String[] {"You"});
    /** The expense category. */
    private final JComboBox<String> categoryBox = styledBox(CATEGORIES);
    /** One checkbox per person sharing the expense. */
    private final JPanel splitPanel =
            new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.PAD, 0));
    /** Shows feedback after a button is clicked. */
    private final JLabel statusLabel =
            Theme.label(" ", Theme.SMALL, Theme.MUTED);

    /**
     * Builds the add expense screen.
     *
     * @param viewManagerModel the model used to switch screens
     */
    public ExpenseView(ViewManagerModel viewManagerModel) {
        this.viewManagerModel = viewManagerModel;
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        add(new NavigationBar(viewManagerModel, ViewNames.EXPENSE),
                BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout(0, Theme.PAD));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(
                Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD));
        content.add(Theme.label("Add an expense", Theme.TITLE, Theme.TEXT),
                BorderLayout.NORTH);
        content.add(buildForm(), BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
    }

    /**
     * Adds a person to the "Paid by" list and the "Split with" boxes.
     *
     * @param name the person's name
     */
    public void addPerson(String name) {
        paidByBox.addItem(name);
        splitPanel.add(splitOption(name, false));
        splitPanel.revalidate();
    }

    private JPanel buildForm() {
        JPanel fields = new JPanel(new GridLayout(2, 2, Theme.PAD, Theme.PAD));
        fields.setOpaque(false);
        fields.add(new LabelledField("What was it for?", descriptionField));
        fields.add(new LabelledField("Total amount ($)", amountField));
        fields.add(new LabelledField("Paid by", paidByBox));
        fields.add(new LabelledField("Category", categoryBox));

        splitPanel.setOpaque(false);
        splitPanel.add(splitOption("You", true));
        // TODO (add_expense use case): call addPerson(name)
        //  for each of the user's friends.

        JPanel split = new JPanel(new BorderLayout(0, Theme.GAP));
        split.setOpaque(false);
        split.add(Theme.label("Split equally with",
                Theme.SMALL, Theme.MUTED), BorderLayout.NORTH);
        split.add(splitPanel, BorderLayout.CENTER);

        JButton cancelButton = Theme.secondaryButton("Cancel");
        JButton addButton = Theme.primaryButton("Add expense");
        cancelButton.addActionListener(e ->
                viewManagerModel.setActiveView(ViewNames.DASHBOARD));
        addButton.addActionListener(e -> addExpense());

        JPanel buttons =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.GAP, 0));
        buttons.setOpaque(false);
        buttons.add(statusLabel);
        buttons.add(cancelButton);
        buttons.add(addButton);

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, Theme.PAD));
        card.add(fields, BorderLayout.NORTH);
        card.add(split, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);

        JPanel holder = new JPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.add(card, BorderLayout.NORTH);
        return holder;
    }

    private void addExpense() {
        // TODO (add_expense use case): call
        //  addExpenseController.execute(description, amount, payer,
        //  participants, category)
        statusLabel.setText("Adding expenses is coming soon.");
    }

    private static JCheckBox splitOption(String name, boolean selected) {
        JCheckBox box = new JCheckBox(name, selected);
        box.setFont(Theme.BODY);
        box.setOpaque(false);
        return box;
    }

    private static JComboBox<String> styledBox(String[] options) {
        JComboBox<String> box = new JComboBox<>(options);
        box.setFont(Theme.BODY);
        box.setBackground(Theme.WHITE);
        return box;
    }
}
