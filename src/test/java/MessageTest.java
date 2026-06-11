package com.chatapp;

/**
 * Unit tests for the Message class — covers Part 2 and Part 3 requirements.
 *
 *
 * @author Silindokuhle Gqukani (ST10461013)
 */
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MessageTest {

    // ── POE test data constants ───────────────────────────────────────────────
    private static final String R1 = "+27834557896";
    private static final String M1 = "Did you get the cake?";

    private static final String R2 = "+27838884567";
    private static final String M2 = "Where are you? You are late! I have asked you to be on time.";

    private static final String R3 = "+27834484567";
    private static final String M3 = "Yohoooo, I am at your gate.";

    private static final String R4 = "0838884567";       // no international code — invalid
    private static final String M4 = "It is dinner time!";

    private static final String R5 = "+27838884567";
    private static final String M5 = "Ok, I am leaving without you.";

    /**
     * Reset all static state before each test to prevent cross-test contamination.
     */
    @BeforeEach
    public void resetState() {
        Message.resetForTesting();
    }

    // ── Part 2: Recipient validation ──────────────────────────────────────────

    @Test
    public void testRecipientSuccess() {
        Message msg = new Message(R1, M1, 1);
        assertEquals("Cell phone number successfully captured.", msg.checkRecipientCell());
    }

    @Test
    public void testRecipientFailure() {
        // R4 has no '+' international code — should fail
        Message msg = new Message(R4, M4, 4);
        assertEquals("Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.",
                msg.checkRecipientCell());
    }

    // ── Part 2: Message length ────────────────────────────────────────────────

    @Test
    public void testMessageLengthSuccess() {
        Message msg = new Message(R1, M1, 1);
        assertEquals("Message ready to send.", msg.checkMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        String longMsg = "A".repeat(260);
        Message msg = new Message(R1, longMsg, 1);
        assertTrue(msg.checkMessageLength().contains("exceeds 250 characters by 10"));
    }

    // ── Part 2: Message ID ────────────────────────────────────────────────────

    @Test
    public void testMessageIDLength() {
        Message msg = new Message(R1, M1, 1);
        assertTrue(msg.checkMessageID(), "ID should be <= 10 chars");
        assertEquals(10, msg.getMessageID().length(), "ID should be exactly 10 digits");
    }

    // ── Part 2: Message hash format ───────────────────────────────────────────

    @Test
    public void testMessageHashFormat() {
        Message msg = new Message(R1, M1, 1);
        String hash = msg.getMessageHash();
        assertTrue(hash.matches("[0-9]{2}:[0-9]+:[A-Z]+"),
                "Hash did not match pattern: " + hash);
    }

    @Test
    public void testMessageHashFirstAndLastWord() {
        // M1 = "Did you get the cake?" → first=DID, last=CAKE
        Message msg = new Message(R1, M1, 1);
        String hash = msg.getMessageHash();
        String wordPart = hash.substring(hash.lastIndexOf(':') + 1);
        assertTrue(wordPart.startsWith("DID"),
                "First word should be DID: " + hash);
        assertTrue(wordPart.endsWith("CAKE"),
                "Last word should be CAKE: " + hash);
        assertEquals("DIDCAKE", wordPart,
                "Word part should be DIDCAKE: " + hash);
    }

    // ── Part 2: sentMessage options ───────────────────────────────────────────

    @Test
    public void testSendMessage() {
        Message msg = new Message(R1, M1, 1);
        assertEquals("Message successfully sent.", msg.sentMessage(1));
    }

    @Test
    public void testDisregardMessage() {
        Message msg = new Message(R3, M3, 3);
        assertEquals("Press 0 to delete the message.", msg.sentMessage(2));
    }

    @Test
    public void testStoreMessage() {
        Message msg = new Message(R2, M2, 2);
        assertEquals("Message successfully stored.", msg.sentMessage(3));
        // Clean up test file
        new File("messages.json").delete();
    }

    // ── Part 3 Test 1: Sent messages array correctly populated ────────────────

    /**
     * POE requirement: The Messages array contains the expected test data.
     * Test data messages 1 and 4 are flagged "Sent".
     * System returns: "Did you get the cake?", "It is dinner time!"
     */
    @Test
    public void testSentMessagesArrayPopulated() {
        // Msg 1 — valid recipient, sent
        Message msg1 = new Message(R1, M1, 1);
        msg1.sentMessage(1);

        // Msg 2 — stored (not sent)
        Message msg2 = new Message(R2, M2, 2);
        msg2.sentMessage(3);

        // Msg 3 — disregarded (not sent)
        Message msg3 = new Message(R3, M3, 3);
        msg3.sentMessage(2);

        // Msg 4 — developer entry (invalid recipient, but still add to sent for test purposes)
        Message msg4 = new Message(R4, M4, 4);
        msg4.sentMessage(1);

        // Sent array should contain msg1 and msg4
        assertEquals(2, Message.getSentMessagesArray().size());
        assertEquals(M1, Message.getSentMessagesArray().get(0).getMessage());
        assertEquals(M4, Message.getSentMessagesArray().get(1).getMessage());
    }

    // ── Part 3 Test 2: Display longest message ────────────────────────────────

    /**
     * POE requirement: Display the longest message from messages 1–4.
     * System returns: "Where are you? You are late! I have asked you to be on time."
     * (Message 2 — stored — is the longest.)
     */
    @Test
    public void testDisplayLongestMessage() {
        Message msg1 = new Message(R1, M1, 1);
        msg1.sentMessage(1);

        Message msg2 = new Message(R2, M2, 2);
        msg2.sentMessage(3);

        Message msg3 = new Message(R3, M3, 3);
        msg3.sentMessage(2);

        Message msg4 = new Message(R4, M4, 4);
        msg4.sentMessage(1);

        // msg2 is stored — load it into storedMessagesArray
        // (in real use loadStoredMessages() reads the file; here we use the
        //  in-memory store since sentMessage(3) adds to storedMessagesArray)
        String result = Message.longestStoredMessage();
        assertTrue(result.contains(M2),
                "Longest message should be M2 but got: " + result);

        // Clean up
        new File("messages.json").delete();
    }

    // ── Part 3 Test 3: Search by message ID ──────────────────────────────────

    /**
     * POE requirement: Search for messageID "0838884567" — returns "It is dinner time!"
     * Note: the POE search ID is the developer number used as messageID in test context.
     * We seed a known ID by using the reconstruction constructor.
     */
    @Test
    public void testSearchByMessageID() {
        // Seed a stored message with a known ID using the reconstruction constructor
        Message seeded = new Message("0838884567", "XX:4:ITTIME",
                R5, M4, 4);
        Message.getStoredMessagesArray().add(seeded);

        String result = Message.searchByMessageID("0838884567");
        assertTrue(result.contains(M4),
                "Search by ID should return '" + M4 + "' but got: " + result);
    }

    // ── Part 3 Test 4: Search by recipient ───────────────────────────────────

    /**
     * POE requirement: Search sent/stored messages for +27838884567.
     * System returns messages 2 and 5 (both have that recipient).
     */
    @Test
    public void testSearchByRecipient() {
        // Msg 2 — stored
        Message msg2 = new Message(R2, M2, 2);
        msg2.sentMessage(3);

        // Msg 5 — also stored, same recipient
        Message msg5 = new Message(R5, M5, 5);
        msg5.sentMessage(3);

        // Msg 1 — sent, different recipient
        Message msg1 = new Message(R1, M1, 1);
        msg1.sentMessage(1);

        String result = Message.searchByRecipient("+27838884567");
        assertTrue(result.contains(M2),
                "Should contain M2 for that recipient: " + result);
        assertTrue(result.contains(M5),
                "Should contain M5 for that recipient: " + result);
        assertFalse(result.contains(M1),
                "Should NOT contain M1 (different recipient): " + result);

        // Clean up
        new File("messages.json").delete();
    }

    // ── Part 3 Test 5: Delete by message hash ────────────────────────────────

    /**
     * POE requirement: Delete Test Message 2 using its hash.
     * System returns: "Where are you? You are late! ..." successfully deleted.
     */
    @Test
    public void testDeleteByHash() {
        Message msg2 = new Message(R2, M2, 2);
        msg2.sentMessage(3); // adds to storedMessagesArray
        String hash = msg2.getMessageHash();

        String result = Message.deleteByHash(hash);
        assertTrue(result.contains("successfully deleted"),
                "Delete result should confirm deletion: " + result);
        assertTrue(result.contains(M2),
                "Delete result should name the deleted message: " + result);

        // Array should now be empty
        assertEquals(0, Message.getStoredMessagesArray().size());

        // Clean up
        new File("messages.json").delete();
    }

    // ── Part 3 Test 6: Total messages sent counter ───────────────────────────

    @Test
    public void testTotalMessagesSentAccumulates() {
        new Message(R1, M1, 1).sentMessage(1);
        new Message(R4, M4, 4).sentMessage(1);
        assertEquals(2, Message.returnTotalMessages());
    }

    // ── Part 3 Test 7: Disregarded messages array ─────────────────────────────

    @Test
    public void testDisregardedMessagesArrayPopulated() {
        Message msg3 = new Message(R3, M3, 3);
        msg3.sentMessage(2);
        assertEquals(1, Message.getDisregardedMessages().size());
        assertEquals(M3, Message.getDisregardedMessages().get(0).getMessage());
    }

    // ── Part 3 Test 8: Message hash and ID arrays ────────────────────────────

    @Test
    public void testHashAndIDArraysPopulated() {
        Message msg1 = new Message(R1, M1, 1);
        Message msg2 = new Message(R2, M2, 2);

        assertEquals(2, Message.getMessageIDArray().size());
        assertEquals(2, Message.getMessageHashArray().size());
        assertTrue(Message.getMessageIDArray().contains(msg1.getMessageID()));
        assertTrue(Message.getMessageHashArray().contains(msg1.getMessageHash()));
        assertTrue(Message.getMessageIDArray().contains(msg2.getMessageID()));
    }
}
