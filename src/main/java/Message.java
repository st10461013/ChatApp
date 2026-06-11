package com.chatapp;

/**
 * Message class handles the creation, validation, sending,
 * storing, and retrieval of chat messages in QuickChat.
 *
 *
 * @author Silindokuhle Gqukani (ST10461013)
 */
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class Message {

    // ── Instance fields ───────────────────────────────────────────────────────
    private String messageID;
    private String recipient;
    private String message;
    private String messageHash;
    private int messageNumber;

    // ── Part 3: Five required arrays (static, session-scoped) ─────────────────
    /** Array 1: All messages that were sent. */
    private static ArrayList<Message> sentMessagesArray      = new ArrayList<>();

    /** Array 2: All messages that were disregarded. */
    private static ArrayList<Message> disregardedMessages    = new ArrayList<>();

    /** Array 3: All messages loaded from messages.json (stored messages). */
    private static ArrayList<Message> storedMessagesArray    = new ArrayList<>();

    /** Array 4: All message hashes generated this session. */
    private static ArrayList<String>  messageHashArray       = new ArrayList<>();

    /** Array 5: All message IDs generated this session. */
    private static ArrayList<String>  messageIDArray         = new ArrayList<>();

    // ── Legacy counters kept for Part 2 compatibility ─────────────────────────
    private static int totalMessagesSent = 0;

    // ── JSON storage file ──────────────────────────────────────────────────────
    private static final String JSON_FILE = "messages.json";

    // ── Constructors ──────────────────────────────────────────────────────────

    /**
     * Full constructor used at runtime — auto-generates ID and hash.
     */
    public Message(String recipient, String message, int messageNumber) {
        this.recipient     = recipient;
        this.message       = message;
        this.messageNumber = messageNumber;
        this.messageID     = generateMessageID();
        this.messageHash   = createMessageHash();

        // Register ID and hash in their arrays immediately on construction
        messageIDArray.add(this.messageID);
        messageHashArray.add(this.messageHash);
    }

    /**
     * Reconstruction constructor — used when loading messages back from JSON.
     * Does NOT push to ID/hash arrays (already registered when originally created).
     */
    public Message(String messageID, String messageHash,
                   String recipient, String message, int messageNumber) {
        this.messageID     = messageID;
        this.messageHash   = messageHash;
        this.recipient     = recipient;
        this.message       = message;
        this.messageNumber = messageNumber;
    }

    // ── ID & Hash generation ──────────────────────────────────────────────────

    /**
     * Generates a random 10-digit numeric message ID.
     */
    private String generateMessageID() {
        Random rand = new Random();
        long id = (long) (rand.nextDouble() * 9_000_000_000L) + 1_000_000_000L;
        return String.valueOf(id);
    }

    /**
     * Validates that the message ID is exactly 10 characters.
     */
    public boolean checkMessageID() {
        return messageID.length() <= 10;
    }

    /**
     * Creates the message hash: XX:N:FIRSTWORDLASTWORD (all uppercase).
     */
    public String createMessageHash() {
        String[] words    = message.trim().split("\\s+");
        String firstWord  = words[0].replaceAll("[^a-zA-Z]", "");
        String lastWord   = words[words.length - 1].replaceAll("[^a-zA-Z]", "");
        String prefix     = messageID.substring(0, 2);
        return (prefix + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    // ── Validation ────────────────────────────────────────────────────────────

    /**
     * Validates the recipient: must start with '+' and be 12 chars or fewer.
     * SA international format (+27XXXXXXXXX) is 12 characters.
     */
    public String checkRecipientCell() {
        if (recipient.startsWith("+") && recipient.length() <= 12) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.";
    }

    /**
     * Validates that the message body does not exceed 250 characters.
     */
    public String checkMessageLength() {
        if (message.length() <= 250) {
            return "Message ready to send.";
        }
        int over = message.length() - 250;
        return "Message exceeds 250 characters by " + over + "; please reduce the size.";
    }

    // ── Send / Discard / Store ────────────────────────────────────────────────

    /**
     * Handles the user's send/discard/store decision.
     *
     * @param choice 1=Send, 2=Disregard, 3=Store
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                totalMessagesSent++;
                sentMessagesArray.add(this);
                return "Message successfully sent.";

            case 2:
                disregardedMessages.add(this);
                return "Press 0 to delete the message.";

            case 3:
                storeMessage();
                storedMessagesArray.add(this);
                return "Message successfully stored.";

            default:
                return "Invalid option selected.";
        }
    }

    /**
     * Writes this message as a JSON object to messages.json (append mode).
     * Uses the org.json library (see attribution at top of file).
     */
    public void storeMessage() {
        JSONObject json = new JSONObject();
        json.put("messageID",   messageID);
        json.put("messageHash", messageHash);
        json.put("recipient",   recipient);
        json.put("message",     message);
        json.put("number",      messageNumber);

        try (FileWriter fw = new FileWriter(JSON_FILE, true)) {
            fw.write(json.toString(2));
            fw.write(System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error writing to " + JSON_FILE + ": " + e.getMessage());
        }
    }

    // ── Part 3: Stored Messages menu operations ───────────────────────────────

    /**
     * Loads all messages from messages.json into the storedMessagesArray.
     * Clears the array first to avoid duplicates on repeated calls.
     * Uses the org.json library (attribution above).
     */
    public static void loadStoredMessages() {
        storedMessagesArray.clear();
        File file = new File(JSON_FILE);
        if (!file.exists()) {
            return;
        }

        try {
            String raw = new String(Files.readAllBytes(Paths.get(JSON_FILE)));
            // The file may contain multiple JSON objects concatenated — split on '}{'
            // We wrap in array brackets for safe parsing
            String[] parts = raw.trim().split("(?<=\\})\\s*(?=\\{)");
            for (String part : parts) {
                part = part.trim();
                if (part.isEmpty()) continue;
                try {
                    JSONObject obj = new JSONObject(part);
                    Message m = new Message(
                            obj.optString("messageID",   "0000000000"),
                            obj.optString("messageHash", ""),
                            obj.optString("recipient",   ""),
                            obj.optString("message",     ""),
                            obj.optInt("number", 0)
                    );
                    storedMessagesArray.add(m);
                } catch (Exception ignored) { /* skip malformed entries */ }
            }
        } catch (IOException e) {
            System.err.println("Error reading " + JSON_FILE + ": " + e.getMessage());
        }
    }

    /**
     * (a) Returns a formatted list of sender (always "You") and recipient
     *     for all stored messages.
     */
    public static String displayStoredSenderRecipient() {
        if (storedMessagesArray.isEmpty()) {
            return "No stored messages found.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== Stored Messages — Sender & Recipient ===\n");
        for (int i = 0; i < storedMessagesArray.size(); i++) {
            Message m = storedMessagesArray.get(i);
            sb.append(i + 1).append(") Sender: You | Recipient: ").append(m.recipient).append("\n");
        }
        return sb.toString();
    }

    /**
     * (b) Returns the longest stored message body.
     */
    public static String longestStoredMessage() {
        if (storedMessagesArray.isEmpty()) {
            return "No stored messages found.";
        }
        Message longest = storedMessagesArray.get(0);
        for (Message m : storedMessagesArray) {
            if (m.message.length() > longest.message.length()) {
                longest = m;
            }
        }
        return "Longest stored message:\n  Recipient: " + longest.recipient
                + "\n  Message:   " + longest.message;
    }

    /**
     * (c) Searches stored messages (and sent messages) by message ID.
     * Returns the recipient and message text if found.
     */
    public static String searchByMessageID(String searchID) {
        // Search sent messages
        for (Message m : sentMessagesArray) {
            if (m.messageID.equals(searchID)) {
                return "Recipient: " + m.recipient + "\nMessage:   " + m.message;
            }
        }
        // Search stored messages
        for (Message m : storedMessagesArray) {
            if (m.messageID.equals(searchID)) {
                return "Recipient: " + m.recipient + "\nMessage:   " + m.message;
            }
        }
        return "No message found with ID: " + searchID;
    }

    /**
     * (d) Searches all sent and stored messages for a particular recipient.
     * Returns all matching message bodies.
     */
    public static String searchByRecipient(String searchRecipient) {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessagesArray) {
            if (m.recipient.equals(searchRecipient)) {
                sb.append(m.message).append("\n");
            }
        }
        for (Message m : storedMessagesArray) {
            if (m.recipient.equals(searchRecipient)) {
                sb.append(m.message).append("\n");
            }
        }
        if (sb.length() == 0) {
            return "No messages found for recipient: " + searchRecipient;
        }
        return sb.toString().trim();
    }

    /**
     * (e) Deletes a message from storedMessagesArray by its hash.
     * Also rewrites messages.json without the deleted entry.
     * Returns a confirmation or error message.
     */
    public static String deleteByHash(String hash) {
        Message toDelete = null;
        for (Message m : storedMessagesArray) {
            if (m.messageHash.equalsIgnoreCase(hash)) {
                toDelete = m;
                break;
            }
        }
        if (toDelete == null) {
            return "No stored message found with hash: " + hash;
        }
        storedMessagesArray.remove(toDelete);

        // Rewrite messages.json without the deleted entry
        try (FileWriter fw = new FileWriter(JSON_FILE, false)) {
            for (Message m : storedMessagesArray) {
                JSONObject json = new JSONObject();
                json.put("messageID",   m.messageID);
                json.put("messageHash", m.messageHash);
                json.put("recipient",   m.recipient);
                json.put("message",     m.message);
                json.put("number",      m.messageNumber);
                fw.write(json.toString(2));
                fw.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Error rewriting " + JSON_FILE + ": " + e.getMessage());
        }

        return "Message: \"" + toDelete.message + "\" successfully deleted.";
    }

    /**
     * (f) Returns a full report of all stored messages including
     *     Message Hash, Recipient, and Message — as required by the POE.
     */
    public static String displayStoredReport() {
        if (storedMessagesArray.isEmpty()) {
            return "No stored messages to report.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("==============================\n");
        sb.append("   STORED MESSAGES REPORT\n");
        sb.append("==============================\n");
        for (int i = 0; i < storedMessagesArray.size(); i++) {
            Message m = storedMessagesArray.get(i);
            sb.append("Message ").append(i + 1).append(":\n");
            sb.append("  Message Hash : ").append(m.messageHash).append("\n");
            sb.append("  Recipient    : ").append(m.recipient).append("\n");
            sb.append("  Message      : ").append(m.message).append("\n");
            sb.append("------------------------------\n");
        }
        return sb.toString();
    }

    // ── Legacy helpers (Part 2 compatibility) ─────────────────────────────────

    /**
     * Returns a formatted string of all sent messages (legacy Part 2 display).
     */
    public static String printMessages() {
        if (sentMessagesArray.isEmpty()) {
            return "No messages sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessagesArray) {
            sb.append("Message ID:   ").append(m.messageID).append("\n");
            sb.append("Message Hash: ").append(m.messageHash).append("\n");
            sb.append("Recipient:    ").append(m.recipient).append("\n");
            sb.append("Message:      ").append(m.message).append("\n");
            sb.append("----------------------------\n");
        }
        return sb.toString();
    }

    /** Returns the total count of sent messages this session. */
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    /**
     * Resets all static state — used in unit tests to prevent accumulation.
     */
    public static void resetForTesting() {
        sentMessagesArray.clear();
        disregardedMessages.clear();
        storedMessagesArray.clear();
        messageHashArray.clear();
        messageIDArray.clear();
        totalMessagesSent = 0;
    }

    // ── Array accessors (for unit tests) ──────────────────────────────────────

    public static ArrayList<Message> getSentMessagesArray()     { return sentMessagesArray; }
    public static ArrayList<Message> getDisregardedMessages()   { return disregardedMessages; }
    public static ArrayList<Message> getStoredMessagesArray()   { return storedMessagesArray; }
    public static ArrayList<String>  getMessageHashArray()      { return messageHashArray; }
    public static ArrayList<String>  getMessageIDArray()        { return messageIDArray; }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String getMessageID()     { return messageID; }
    public String getMessageHash()   { return messageHash; }
    public String getRecipient()     { return recipient; }
    public String getMessage()       { return message; }
    public int    getMessageNumber() { return messageNumber; }
}
