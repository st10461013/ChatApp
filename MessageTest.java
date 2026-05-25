/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Student
 */
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageTest {
     // --- MESSAGE LENGTH TESTS ---

    @Test
    public void testMessageLengthSuccess() {
        Message msg = new Message("+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 1);
        assertEquals("Message ready to send.", msg.checkMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        // Build a string longer than 250 characters
        String longMsg = "A".repeat(260);
        Message msg = new Message("+27718693002", longMsg, 1);
        assertTrue(msg.checkMessageLength().contains("exceeds 250 characters"));
    }

    // --- RECIPIENT NUMBER TESTS ---

    @Test
    public void testRecipientSuccess() {
        Message msg = new Message("+277186930", "Hello", 1);
        assertEquals("Cell phone number successfully captured.",
                msg.checkRecipientCell());
    }

    @Test
    public void testRecipientFailure() {
        // Number too long and no international code
        Message msg = new Message("08575975889", "Hello", 1);
        assertEquals("Cell phone number is incorrectly formatted or does not contain " +
                "an international code. Please correct the number and try again.",
                msg.checkRecipientCell());
    }

    // --- MESSAGE HASH TESTS ---

    @Test
    public void testMessageHashFormat() {
        Message msg = new Message("+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 1);
        String hash = msg.getMessageHash();
        // Hash must follow pattern: XX:N:WORDWORD (all caps)
        assertTrue(hash.matches("[0-9]{2}:[0-9]+:[A-Z]+"));
    }

    @Test
    public void testMessageHashLastWord() {
        Message msg = new Message("+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 1);
        // Last word should be TONIGHT
        assertTrue(msg.getMessageHash().endsWith("TONIGHT"));
    }

    // --- MESSAGE ID TESTS ---

    @Test
    public void testMessageIDLength() {
        Message msg = new Message("+27718693002", "Hello world", 1);
        assertTrue(msg.checkMessageID());
        System.out.println("Message ID generated: " + msg.getMessageID());
    }

    // --- SENT MESSAGE TESTS ---

    @Test
    public void testSendMessage() {
        Message msg = new Message("+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 1);
        assertEquals("Message successfully sent.", msg.sentMessage(1));
    }

    @Test
    public void testDisregardMessage() {
        Message msg = new Message("+27718693002",
                "Hi Keegan, did you receive the payment?", 2);
        assertEquals("Press 0 to delete the message.", msg.sentMessage(2));
    }

    @Test
    public void testStoreMessage() {
        Message msg = new Message("+27718693002", "Test message stored", 3);
        assertEquals("Message successfully stored.", msg.sentMessage(3));
    }
    
}
