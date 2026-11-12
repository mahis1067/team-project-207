
package app_business_rules.signup;

/** Input data for the sign-up use case. */
public class SignUpInputData {

    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;

    /**
     * Creates the input data for signing up.
     *
     * @param username the requested username
     * @param password the plain-text password
     * @param firstName the user's first name
     * @param lastName the user's last name
     */
    public SignUpInputData(String username, String password,
                           String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
