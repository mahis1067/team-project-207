package app;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/** Entry point of the program. */
public final class Main {

    private Main() {
    }

    /**
     * Starts the application.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame app = new AppBuilder().addLoginView().addSignupView().build();
            app.setVisible(true);
        });
    }
}
