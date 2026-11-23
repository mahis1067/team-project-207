package frameworks_and_drivers.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JPanel;

/** One form row: a small grey label above an input field. */
class LabelledField extends JPanel {

    /**
     * Creates the row.
     *
     * @param label the label text
     * @param field the input component
     */
    LabelledField(String label, JComponent field) {
        setLayout(new BorderLayout(0, Theme.GAP / 2));
        setOpaque(false);
        setAlignmentX(LEFT_ALIGNMENT);
        add(Theme.label(label, Theme.SMALL, Theme.MUTED),
                BorderLayout.NORTH);
        add(field, BorderLayout.CENTER);
        setMaximumSize(new Dimension(
                Integer.MAX_VALUE, getPreferredSize().height));
    }
}
