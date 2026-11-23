package frameworks_and_drivers.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/** Colours, fonts and styled components shared by every screen. */
final class Theme {

    /** Main brand colour (royal blue). */
    static final Color BLUE = new Color(0x1D4ED8);
    /** Light blue for the active menu item. */
    static final Color BLUE_LIGHT = new Color(0xE3EAFD);
    /** Page background. */
    static final Color BACKGROUND = new Color(0xF4F6FB);
    /** Card and input background. */
    static final Color WHITE = Color.WHITE;
    /** Border colour for cards and inputs. */
    static final Color BORDER = new Color(0xC9D0DE);
    /** Main text colour. */
    static final Color TEXT = new Color(0x1C1C1A);
    /** Secondary (grey) text colour. */
    static final Color MUTED = new Color(0x4B5563);
    /** Accepted / success colour. */
    static final Color GREEN = new Color(0x6AE864);
    /** Needs-your-action colour. */
    static final Color ORANGE = new Color(0xF29B1F);
    /** Pending colour. */
    static final Color YELLOW = new Color(0xFFF029);
    /** Warning / error colour. */
    static final Color RED = new Color(0xE62020);

    /** Font for page titles. */
    static final Font TITLE = new Font("SansSerif", Font.BOLD, 26);
    /** Font for card headings. */
    static final Font HEADING = new Font("SansSerif", Font.BOLD, 17);
    /** Font for normal text. */
    static final Font BODY = new Font("SansSerif", Font.PLAIN, 15);
    /** Font for labels and small text. */
    static final Font SMALL = new Font("SansSerif", Font.PLAIN, 13);
    /** Font for the big numbers on the dashboard. */
    static final Font STAT = new Font("SansSerif", Font.BOLD, 26);

    /** Small spacing in pixels. */
    static final int GAP = 8;
    /** Padding inside cards and buttons in pixels. */
    static final int PAD = 20;
    /** Width of text boxes, in characters. */
    static final int FIELD_COLUMNS = 20;

    private Theme() {
    }

    /**
     * Creates a filled blue button.
     *
     * @param text the button text
     * @return the styled button
     */
    static JButton primaryButton(String text) {
        JButton button = baseButton(text);
        button.setBackground(BLUE);
        button.setForeground(WHITE);
        return button;
    }

    /**
     * Creates a white button with a grey outline.
     *
     * @param text the button text
     * @return the styled button
     */
    static JButton secondaryButton(String text) {
        JButton button = baseButton(text);
        button.setBackground(WHITE);
        button.setForeground(TEXT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(GAP, PAD, GAP, PAD)));
        return button;
    }

    /**
     * Creates a styled one-line text box.
     *
     * @return the text field
     */
    static JTextField textField() {
        JTextField field = new JTextField(FIELD_COLUMNS);
        styleInput(field);
        return field;
    }

    /**
     * Creates a styled password box.
     *
     * @return the password field
     */
    static JPasswordField passwordField() {
        JPasswordField field = new JPasswordField(FIELD_COLUMNS);
        styleInput(field);
        return field;
    }

    /**
     * Creates a white card with a border and padding.
     *
     * @return the card panel
     */
    static JPanel card() {
        JPanel card = new JPanel();
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(PAD, PAD, PAD, PAD)));
        return card;
    }

    /**
     * Creates a label in the given font and colour.
     *
     * @param text the label text
     * @param font the font
     * @param color the text colour
     * @return the label
     */
    static JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    /**
     * Creates a small coloured status tag, like "Pending".
     *
     * @param text the tag text
     * @param background the tag colour
     * @param foreground the text colour
     * @return the tag label
     */
    static JLabel tag(String text, Color background, Color foreground) {
        JLabel tag = label(text, SMALL.deriveFont(Font.BOLD), foreground);
        tag.setOpaque(true);
        tag.setBackground(background);
        tag.setBorder(BorderFactory.createEmptyBorder(2, GAP, 2, GAP));
        return tag;
    }

    private static JButton baseButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BODY.deriveFont(Font.BOLD));
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(GAP, PAD, GAP, PAD));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static void styleInput(JTextField field) {
        field.setFont(BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP)));
    }
}
