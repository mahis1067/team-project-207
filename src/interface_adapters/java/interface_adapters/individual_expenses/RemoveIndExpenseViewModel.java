package interface_adapters.individual_expenses;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;

/** Stores the information the view displays after removing an expense. */
public class RemoveIndExpenseViewModel {

  private final PropertyChangeSupport support = new PropertyChangeSupport(this);
  private State state = new State(null, null);

  /**
   * Returns the latest result.
   *
   * @return the current result
   */
  public State getState() {
    return state;
  }

  /**
   * Registers a listener for view updates.
   *
   * @param listener the listener to notify when the result changes
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  void setState(State newState) {
    State oldState = state;
    state = newState;
    support.firePropertyChange("state", oldState, newState);
  }

  /**
   * The result shown to the user.
   *
   * @param expenseId the unique identifier of the expense
   * @param errorMessage the failure message, or null on success
   */
  public record State(UUID expenseId, String errorMessage) {
  }
}
