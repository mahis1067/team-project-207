package frameworks_and_drivers.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.AbstractBorder;

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
    /** Border colour for inputs and outlined buttons. */
    static final Color BORDER = new Color(0xC9D0DE);
    /** Softer border colour for cards. */
    static final Color CARD_BORDER = new Color(0xE2E6EF);
    /** Main text colour. */
    static final Color TEXT = new Color(0x1C1C1A);
    /** Secondary (grey) text colour. */
    static final Color MUTED = new Color(0x4B5563);
    /** Accepted / success colour. */
    static final Color GREEN = new Color(0x00C54A);
    /** Needs-your-action colour. */
    static final Color ORANGE = new Color(0xF29B1F);
    /** Pending colour. */
    static final Color YELLOW = new Color(0xFFF029);
    /** Warning / error colour. */
    static final Color RED = new Color(0xE62020);

    /** Closest installed font to the design's Inter. */
    private static final String FAMILY = pickFamily(
            "Inter", "Helvetica Neue", "Segoe UI", "SansSerif");

    /** Font for page titles. */
    static final Font TITLE = new Font(FAMILY, Font.BOLD, 24);
    /** Font for card headings. */
    static final Font HEADING = new Font(FAMILY, Font.BOLD, 16);
    /** Font for normal text. */
    static final Font BODY = new Font(FAMILY, Font.PLAIN, 14);
    /** Font for labels and small text. */
    static final Font SMALL = new Font(FAMILY, Font.PLAIN, 12);
    /** Font for the big numbers on the dashboard. */
    static final Font STAT = new Font(FAMILY, Font.BOLD, 24);

    /** Small spacing in pixels. */
    static final int GAP = 8;
    /** Padding inside cards and buttons in pixels. */
    static final int PAD = 20;
    /** Width of text boxes, in characters. */
    static final int FIELD_COLUMNS = 20;
    /** Corner roundness of cards in pixels. */
    static final int CARD_RADIUS = 16;
    /** Corner roundness of buttons and inputs in pixels. */
    static final int RADIUS = 10;

    private Theme() {
    }

    /**
     * Creates a filled blue button with rounded corners.
     *
     * @param text the button text
     * @return the styled button
     */
    static JButton primaryButton(String text) {
        JButton button = styleButton(new RoundedButton(text, null));
        button.setBackground(BLUE);
        button.setForeground(WHITE);
        return button;
    }

    /**
     * Creates a white button with a grey rounded outline.
     *
     * @param text the button text
     * @return the styled button
     */
    static JButton secondaryButton(String text) {
        JButton button = styleButton(new RoundedButton(text, BORDER));
        button.setBackground(WHITE);
        button.setForeground(TEXT);
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
     * Creates a white card with rounded corners and padding.
     *
     * @return the card panel
     */
    static JPanel card() {
        JPanel card = new RoundedPanel();
        card.setOpaque(false);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(PAD, PAD, PAD, PAD));
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
     * Makes a small rounded tag, like "Expense" or "Confirmed".
     *
     * @param text the tag text
     * @param background the fill colour
     * @param foreground the text colour
     * @return the tag
     */
    static JLabel tag(String text, Color background, Color foreground) {
        JLabel tag = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                        getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tag.setOpaque(false);
        tag.setBackground(background);
        tag.setForeground(foreground);
        tag.setFont(SMALL.deriveFont(Font.BOLD));
        tag.setBorder(BorderFactory.createEmptyBorder(
                GAP / 2, GAP + GAP / 2, GAP / 2, GAP + GAP / 2));
        return tag;
    }

    private static JButton styleButton(JButton button) {
        button.setFont(BODY.deriveFont(Font.BOLD));
        button.setBorder(BorderFactory.createEmptyBorder(GAP, PAD, GAP, PAD));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static void styleInput(JTextField field) {
        field.setFont(BODY);
        field.setBorder(new RoundedBorder());
    }

    private static Graphics2D smooth(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return g2;
    }

    private static String pickFamily(String... preferred) {
        List<String> installed = Arrays.asList(GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames());
        for (String family : preferred) {
            if (installed.contains(family)) {
                return family;
            }
        }
        return Font.SANS_SERIF;
    }

    /** A panel drawn with rounded corners and a soft outline. */
    private static final class RoundedPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                    CARD_RADIUS, CARD_RADIUS);
            g2.setColor(CARD_BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                    CARD_RADIUS, CARD_RADIUS);
            g2.dispose();
        }
    }

    /** A button drawn with rounded corners and an optional outline. */
    private static final class RoundedButton extends JButton {

        /** Outline colour, or null for no outline. */
        private final Color outline;

        RoundedButton(String text, Color outline) {
            super(text);
            this.outline = outline;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                    RADIUS, RADIUS);
            if (outline != null) {
                g2.setColor(outline);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                        RADIUS, RADIUS);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** A thin grey border with rounded corners, used on text boxes. */
    private static final class RoundedBorder extends AbstractBorder {

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y,
                                int width, int height) {
            Graphics2D g2 = smooth(g);
            g2.setColor(BORDER);
            g2.drawRoundRect(x, y, width - 1, height - 1, RADIUS, RADIUS);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(GAP, GAP, GAP, GAP);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(GAP, GAP, GAP, GAP);
            return insets;
        }
    }
}
