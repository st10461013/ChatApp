package com.chatapp;

/**
 * Unit tests for the Message class.
 *
 * @author Silindokuhle Gqukani (ST10461013)
 */
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageTest {

    /** Reset static state before each test to prevent accumulation across runs. */
    @BeforeEach
    public void resetMessageState() {
        Message.resetForTesting();
    }

    // ── MESSAGE LENGTH ──────────────────────────────────────────────────────

    @Test
    public void testMessageLengthSuccess() {
        Message msg = new Message("+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 1);
        assertEquals("Message ready to send.", msg.checkMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        String longMsg = "A".repeat(260);
        Message msg = new Message("+27718693002", longMsg, 1);
        assertTrue(msg.checkMessageLength().contains("exceeds 250 characters"));
    }

    // ── RECIPIENT NUMBER ────────────────────────────────────────────────────

    @Test
    public void testRecipientSuccess() {
        // +27718693002 is a valid 12-character SA international number
        Message msg = new Message("+27718693002", "Hello", 1);
        assertEquals("Cell phone number successfully captured.",
                msg.checkRecipientCell());
    }

    @Test
    public void testRecipientFailure() {
        // No international code and too long
        Message msg = new Message("08575975889", "Hello", 1);
        assertEquals("Cell phone number is incorrectly formatted or does not contain "
                        + "an international code. Please correct the number and try again.",
                msg.checkRecipientCell());
    }

    // ── MESSAGE HASH ────────────────────────────────────────────────────────

    @Test
    public void testMessageHashFormat() {
        Message msg = new Message("+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 1);
        String hash = msg.getMessageHash();
        // Format: XX:N:WORDWORD (all caps)
        assertTrue(hash.matches("[0-9]{2}:[0-9]+:[A-Z]+"),
                "Hash did not match expected pattern: " + hash);
    }

    @Test
    public void testMessageHashFirstAndLastWord() {
        Message msg = new Message("+27718693002",
                "Hi Mike, can you join us for dinner tonight?", 1);
        String hash = msg.getMessageHash();
        // Full hash must contain HI and end with TONIGHT
        // Pattern: XX:1:HITONIGHT
        assertTrue(hash.contains(":1:"),
                "Hash missing message number segment: " + hash);
        assertTrue(hash.endsWith("TONIGHT"),
                "Hash last word should be TONIGHT: " + hash);
        // Extract the word part after the second colon
        String wordPart = hash.substring(hash.lastIndexOf(':') + 1);
        assertTrue(wordPart.startsWith("HI"),
                "Hash first word should be HI: " + hash);
        assertEquals("HITONIGHT", wordPart,
                "Combined word part should be HITONIGHT: " + hash);
    }

    // ── MESSAGE ID ──────────────────────────────────────────────────────────

    @Test
    public void testMessageIDLength() {
        Message msg = new Message("+27718693002", "Hello world", 1);
        assertTrue(msg.checkMessageID(),
                "Message ID should be <= 10 chars: " + msg.getMessageID());
        assertEquals(10, msg.getMessageID().length(),
                "Message ID should be exactly 10 digits");
    }

    // ── SENT MESSAGE ────────────────────────────────────────────────────────

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

    @Test
    public void testTotalMessagesSentAccumulates() {
        Message msg1 = new Message("+27718693002", "First message", 1);
        msg1.sentMessage(1);
        Message msg2 = new Message("+27718693002", "Second message", 2);
        msg2.sentMessage(1);
        assertEquals(2, Message.returnTotalMessages());
    }
}
s