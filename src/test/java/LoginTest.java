package com.chatapp;

/**
 * Unit tests for the Login class.
 *
 * @author Silindokuhle Gqukani (ST10461013)
 */
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginTest {

    private Login login;

    @BeforeEach
    public void setUp() {
        login = new Login();
    }

    @Test
    public void testCheckUserName_Valid() {
        assertTrue(login.checkUserName("user_"));
    }

    @Test
    public void testCheckUserName_Invalid() {
        assertFalse(login.checkUserName("user"));
        assertFalse(login.checkUserName("user123"));
        assertFalse(login.checkUserName("user_long"));
    }

    @Test
    public void testCheckPasswordComplexity_Valid() {
        assertTrue(login.checkPasswordComplexity("Passw0rd!"));
    }

    @Test
    public void testCheckPasswordComplexity_Invalid() {
        assertFalse(login.checkPasswordComplexity("password"));
        assertFalse(login.checkPasswordComplexity("Password"));
        assertFalse(login.checkPasswordComplexity("Pass1234"));
        assertFalse(login.checkPasswordComplexity("Pass!@#"));
    }

    @Test
    public void testCheckCellPhoneNumber_Valid() {
        assertTrue(login.checkCellPhoneNumber("+27123456789"));
    }

    @Test
    public void testCheckCellPhoneNumber_Invalid() {
        assertFalse(login.checkCellPhoneNumber("123456789"));
        assertFalse(login.checkCellPhoneNumber("+271234567890")); // 13 chars
        assertFalse(login.checkCellPhoneNumber("27123456789"));
    }

    @Test
    public void testRegisterUser_Success() {
        String result = login.registerUser("user_", "Passw0rd!", "+27123456789", "John", "Doe");
        assertEquals("User registered successfully", result);
    }

    @Test
    public void testRegisterUser_InvalidUsername() {
        String result = login.registerUser("user", "Passw0rd!", "+27123456789", "John", "Doe");
        assertEquals("Username is not correctly formatted: please ensure that your username "
                + "contains an underscore and is no more than five characters in length.", result);
    }

    @Test
    public void testRegisterUser_InvalidPassword() {
        String result = login.registerUser("user_", "password", "+27123456789", "John", "Doe");
        assertEquals("Password is not correctly formatted; please ensure that the password "
                + "contains at least eight characters, a capital letter, a number, and a special character.", result);
    }

    @Test
    public void testRegisterUser_InvalidPhoneNumber() {
        String result = login.registerUser("user_", "Passw0rd!", "123456789", "John", "Doe");
        assertEquals("Cell phone number incorrectly formatted or does not contain international code.", result);
    }

    @Test
    public void testLoginUser_Success() {
        login.registerUser("user_", "Passw0rd!", "+27123456789", "John", "Doe");
        assertTrue(login.loginUser("user_", "Passw0rd!"));
    }

    @Test
    public void testLoginUser_Failure() {
        login.registerUser("user_", "Passw0rd!", "+27123456789", "John", "Doe");
        assertFalse(login.loginUser("user_", "wrongPassword"));
    }

    @Test
    public void testReturnLoginStatus_SuccessWithName() {
        login.registerUser("user_", "Passw0rd!", "+27123456789", "John", "Doe");
        assertEquals("Welcome John Doe, it is great to see you again.",
                login.returnLoginStatus(true));
    }

    @Test
    public void testReturnLoginStatus_Failure() {
        assertEquals("Username or password incorrect, please try again.",
                login.returnLoginStatus(false));
    }
}
