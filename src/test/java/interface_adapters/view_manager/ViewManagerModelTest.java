package interface_adapters.view_manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ViewManagerModelTest {

    @Test
    void setActiveViewUpdatesStateAndNotifiesListeners() {
        ViewManagerModel model = new ViewManagerModel();
        List<Object> received = new ArrayList<>();
        model.addPropertyChangeListener(e -> received.add(e.getNewValue()));

        model.setActiveView("login");

        assertEquals("login", model.getActiveView());
        assertEquals(List.of("login"), received);
    }
}
