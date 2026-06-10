package com.chatapp;

/**
 * Login class handles user registration, login validation,
 * and login status messages for the QuickChat application.
 *
 * @author Silindokuhle Gqukani (ST10461013)
 */
public class Login {

    private String username;
    private String password;
    private String phoneNumber;
    private String firstName;
    private String lastName;

    /**
     * Parameterised constructor for Login.
     */
    public Login(String username, String password, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Default (no-arg) constructor.
     */
    public Login() {
        // Intentionally empty — fields set via registerUser()
    }

    /**
     * Checks that the username contains an underscore and is 5 chars or fewer.
     */
    public boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    /**
     * Checks that the password has at least 8 chars, one capital,
     * one digit, and one special character.
     */
    public boolean checkPasswordComplexity(String password) {
        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) {
                hasCapital = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        return password.length() >= 8 && hasCapital && hasNumber && hasSpecial;
    }

    /**
     * Validates that the phone number starts with +27 and is at most 12 chars.
     */
    public boolean checkCellPhoneNumber(String phone) {
        return phone.startsWith("+27") && phone.length() <= 12;
    }

    /**
     * Registers a new user after validating all three fields.
     * Stores first and last name extracted from the username for the welcome message.
     */
    public String registerUser(String username, String password, String phoneNumber,
                               String firstName, String lastName) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted: please ensure that your username "
                    + "contains an underscore and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password "
                    + "contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber(phoneNumber)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;

        return "User registered successfully";
    }

    /**
     * Overloaded registerUser without first/last name (backwards-compatible for tests).
     */
    public String registerUser(String username, String password, String phoneNumber) {
        return registerUser(username, password, phoneNumber, username, "");
    }

    /**
     * Validates login credentials against stored values.
     */
    public boolean loginUser(String username, String password) {
        return this.username != null
                && this.username.equals(username)
                && this.password.equals(password);
    }

    /**
     * Returns an appropriate welcome or failure message after a login attempt.
     */
    public String returnLoginStatus(boolean success) {
        if (success) {
            String name = (firstName != null && !firstName.isEmpty())
                    ? firstName + (lastName != null && !lastName.isEmpty() ? " " + lastName : "")
                    : username;
            return "Welcome " + name + ", it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}
