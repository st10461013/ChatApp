/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Student
 */
import java.util.ArrayList;
import java.util.Random;


public class Message {
    private String messageID;
    private String recipient;
    private String message;
    private String messageHash;
    private int messageNumber; // which number message this is (1st, 2nd, etc.)

    // Stores all sent messages for the session
    private static ArrayList<String> sentMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;

    // Constructor
    public Message(String recipient, String message, int messageNumber) {
        this.recipient = recipient;
        this.message = message;
        this.messageNumber = messageNumber;
        this.messageID = generateMessageID();
        this.messageHash = createMessageHash();
    }

    // Auto-generates a random 10-digit message ID
    private String generateMessageID() {
        Random rand = new Random();
        long id = (long)(rand.nextDouble() * 9_000_000_000L) + 1_000_000_000L;
        return String.valueOf(id);
    }

    // Checks messageID is not more than 10 characters
    public boolean checkMessageID() {
        return messageID.length() <= 10;
    }

    // Checks recipient cell number (max 10 chars, must start with + code)
    public String checkRecipientCell() {
        if (recipient.length() <= 10 && recipient.startsWith("+")) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain " +
                   "an international code. Please correct the number and try again.";
        }
    }

    // Creates the message hash: first 2 of ID : message number : firstWord + lastWord
    public String createMessageHash() {
        String[] words = message.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^a-zA-Z]", "");
        String lastWord  = words[words.length - 1].replaceAll("[^a-zA-Z]", "");
        String prefix    = messageID.substring(0, 2);
        return (prefix + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    // Lets the user choose to Send, Discard, or Store the message
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                totalMessagesSent++;
                String record = "Message ID: " + messageID + "\n" +
                                "Message Hash: " + messageHash + "\n" +
                                "Recipient: " + recipient + "\n" +
                                "Message: " + message;
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

    // Validates message length
    public String checkMessageLength() {
        if (message.length() <= 250) {
            return "Message ready to send.";
        } else {
            int over = message.length() - 250;
            return "Message exceeds 250 characters by " + over + "; please reduce the size.";
        }
    }

    // Prints all sent messages
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

    // Returns total messages sent
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // Stores message to JSON (research requirement)
    public void storeMessage() {
        // Basic JSON-style storage using a string
        String json = "{\n" +
                "  \"messageID\": \"" + messageID + "\",\n" +
                "  \"messageHash\": \"" + messageHash + "\",\n" +
                "  \"recipient\": \"" + recipient + "\",\n" +
                "  \"message\": \"" + message + "\"\n" +
                "}";
        System.out.println("Message stored:\n" + json);
    }

    // Getters (used in tests and MainApp)
    public String getMessageID()   { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient()   { return recipient; }
    public String getMessage()     { return message; }
}
