package frameworks_and_drivers.view;

import java.awt.FlowLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** One form row: a label followed by an input field. */
class LabelledField extends JPanel {

    /**
     * Creates the row.
     *
     * @param label the label text
     * @param field the input component
     */
    LabelledField(String label, JComponent field) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new JLabel(label));
        add(field);
    }
}
