package com.chatapp;

/**
 * Message class handles the creation, validation, sending,
 * and storage of chat messages in the QuickChat application.
 *
 * JSON file writing uses org.json library:
 * Crockford, D. (2002). JSON (JavaScript Object Notation). [online]
 * Available at: https://www.json.org
 * Maven dependency: org.json:json:20240303
 *
 * @author Silindokuhle Gqukani (ST10461013)
 */
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Message {

    private String messageID;
    private String recipient;
    private String message;
    private String messageHash;
    private int messageNumber;

    /** Stores all sent messages for the session. */
    private static ArrayList<String> sentMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;

    /**
     * Constructs a Message, auto-generating the ID and hash.
     *
     * @param recipient     the recipient's cell number
     * @param message       the message body
     * @param messageNumber the sequential message counter
     */
    public Message(String recipient, String message, int messageNumber) {
        this.recipient = recipient;
        this.message = message;
        this.messageNumber = messageNumber;
        this.messageID = generateMessageID();
        this.messageHash = createMessageHash();
    }

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
     * Validates the recipient cell number: must start with '+' and be 12 chars or fewer.
     * South African international format (+27XXXXXXXXX) is 12 characters.
     */
    public String checkRecipientCell() {
        if (recipient.length() <= 12 && recipient.startsWith("+")) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain "
                    + "an international code. Please correct the number and try again.";
        }
    }

    /**
     * Creates the message hash using:
     * first 2 chars of ID : messageNumber : firstWord + lastWord (all uppercase).
     */
    public String createMessageHash() {
        String[] words = message.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^a-zA-Z]", "");
        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z]", "");
        String prefix = messageID.substring(0, 2);
        return (prefix + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    /**
     * Validates that the message does not exceed 250 characters.
     */
    public String checkMessageLength() {
        if (message.length() <= 250) {
            return "Message ready to send.";
        } else {
            int over = message.length() - 250;
            return "Message exceeds 250 characters by " + over + "; please reduce the size.";
        }
    }

    /**
     * Handles the send/discard/store decision for a message.
     *
     * @param choice 1 = Send, 2 = Discard, 3 = Store
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                totalMessagesSent++;
                String record = "Message ID: " + messageID + "\n"
                        + "Message Hash: " + messageHash + "\n"
                        + "Recipient: " + recipient + "\n"
                        + "Message: " + message;
                sentMessages.add(record);
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option selected.";
        }
    }

    /**
     * Writes the message as a JSON object to messages.json in append mode.
     * Uses the org.json library (attribution above).
     */
    public void storeMessage() {
        // Build JSON object using org.json library
        JSONObject json = new JSONObject();
        json.put("messageID", messageID);
        json.put("messageHash", messageHash);
        json.put("recipient", recipient);
        json.put("message", message);

        // Append to messages.json
        try (FileWriter fw = new FileWriter("messages.json", true)) {
            fw.write(json.toString(2));
            fw.write(System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error writing to messages.json: " + e.getMessage());
        }
    }

    /**
     * Returns a formatted string of all sent messages.
     */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (String msg : sentMessages) {
            sb.append(msg).append("\n----------------------------\n");
        }
        return sb.toString();
    }

    /** Returns the total count of sent messages this session. */
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    /** Resets static state — used in tests to prevent accumulation across test runs. */
    public static void resetForTesting() {
        sentMessages.clear();
        totalMessagesSent = 0;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getMessageID()   { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient()   { return recipient; }
    public String getMessage()     { return message; }
}
