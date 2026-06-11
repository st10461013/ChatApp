package com.chatapp;

/**
 * MainApp is the entry point for the QuickChat application.
 * Handles user registration, login, message sending, and the
 * Part 3 Stored Messages sub-menu.
 *
 * @author Silindokuhle Gqukani (ST10461013)
 */
import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Login login   = new Login();

        // ── REGISTRATION (with retry loop) ────────────────────────────────
        System.out.println("=== USER REGISTRATION ===");
        String regResult = "";

        while (!regResult.equals("User registered successfully")) {
            System.out.print("Enter your first name: ");
            String firstName = input.nextLine();

            System.out.print("Enter your last name: ");
            String lastName  = input.nextLine();

            System.out.print("Enter a username (max 5 chars, must include '_'): ");
            String username  = input.nextLine();

            System.out.print("Enter a password (min 8 chars, capital, number, special): ");
            String password  = input.nextLine();

            System.out.print("Enter your SA phone number (+27...): ");
            String phone     = input.nextLine();

            regResult = login.registerUser(username, password, phone, firstName, lastName);
            System.out.println(regResult);

            if (!regResult.equals("User registered successfully")) {
                System.out.println("Please try again.\n");
            }
        }

        // ── LOGIN (with retry loop) ────────────────────────────────────────
        System.out.println("\n=== USER LOGIN ===");
        boolean loggedIn = false;

        while (!loggedIn) {
            System.out.print("Enter your username: ");
            String loginUsername = input.nextLine();

            System.out.print("Enter your password: ");
            String loginPassword = input.nextLine();

            loggedIn = login.loginUser(loginUsername, loginPassword);
            System.out.println(login.returnLoginStatus(loggedIn));

            if (!loggedIn) {
                System.out.println("Please try again.\n");
            }
        }

        // ── HOW MANY MESSAGES ─────────────────────────────────────────────
        System.out.println("\nWelcome to QuickChat.");

        int numMessages = 0;
        while (numMessages <= 0) {
            System.out.print("How many messages do you want to send? ");
            try {
                numMessages = Integer.parseInt(input.nextLine().trim());
                if (numMessages <= 0) {
                    System.out.println("Please enter a positive number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input — please enter a number.");
            }
        }

        // ── MAIN MENU LOOP ────────────────────────────────────────────────
        boolean running = true;
        while (running) {
            System.out.println("\n========= MAIN MENU =========");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Stored Messages");
            System.out.println("4) Quit");
            System.out.print("Choose an option: ");

            int menuChoice;
            try {
                menuChoice = Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input — please enter 1–4.");
                continue;
            }

            switch (menuChoice) {

                // ── Option 1: Send Messages (for loop as required by POE) ──
                case 1:
                    for (int i = 0; i < numMessages; i++) {

                        System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");

                        // Collect & validate recipient and message (re-prompt on error)
                        Message msg = null;
                        while (msg == null) {
                            System.out.print("Enter recipient cell number (+27...): ");
                            String recipient    = input.nextLine();

                            System.out.print("Enter your message (max 250 chars): ");
                            String messageText  = input.nextLine();

                            msg = new Message(recipient, messageText, i + 1);

                            String recipientCheck = msg.checkRecipientCell();
                            String lengthCheck    = msg.checkMessageLength();
                            System.out.println(recipientCheck);
                            System.out.println(lengthCheck);

                            if (!recipientCheck.equals("Cell phone number successfully captured.")) {
                                System.out.println("Invalid recipient — please re-enter.\n");
                                msg = null;
                                continue;
                            }
                            if (!lengthCheck.equals("Message ready to send.")) {
                                System.out.println("Message too long — please re-enter.\n");
                                msg = null;
                            }
                        }

                        // Display ID and Hash BEFORE the send decision
                        System.out.println("\nMessage ID:   " + msg.getMessageID());
                        System.out.println("Message Hash: " + msg.getMessageHash());

                        System.out.println("\n1) Send Message");
                        System.out.println("2) Disregard Message");
                        System.out.println("3) Store Message");
                        System.out.print("Choose: ");

                        int sendChoice;
                        try {
                            sendChoice = Integer.parseInt(input.nextLine().trim());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input — message discarded.");
                            sendChoice = 2;
                        }

                        System.out.println(msg.sentMessage(sendChoice));

                        // Show full details after Send or Store
                        if (sendChoice == 1 || sendChoice == 3) {
                            String label = sendChoice == 1 ? "SENT" : "STORED";
                            System.out.println("\n--- " + label + " MESSAGE DETAILS ---");
                            System.out.println("Message ID:   " + msg.getMessageID());
                            System.out.println("Message Hash: " + msg.getMessageHash());
                            System.out.println("Recipient:    " + msg.getRecipient());
                            System.out.println("Message:      " + msg.getMessage());
                        }
                    }
                    break;

                // ── Option 2: Show recently sent messages ──────────────────
                case 2:
                    System.out.println("\n--- ALL SENT MESSAGES ---");
                    System.out.println(Message.printMessages());
                    System.out.println("Total messages sent: " + Message.returnTotalMessages());
                    break;

                // ── Option 3: Stored Messages sub-menu ────────────────────
                case 3:
                    // Load/refresh from JSON file
                    Message.loadStoredMessages();
                    storedMessagesMenu(input);
                    break;

                // ── Option 4: Quit ─────────────────────────────────────────
                case 4:
                    System.out.println("Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please choose 1–4.");
            }
        }

        input.close();
    }

    /**
     * Sub-menu for Stored Messages (Part 3 requirement 2).
     * Options: a–f as specified in the POE.
     */
    private static void storedMessagesMenu(Scanner input) {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== STORED MESSAGES MENU =====");
            System.out.println("a) Display sender & recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a message by ID");
            System.out.println("d) Search messages by recipient");
            System.out.println("e) Delete a message using its hash");
            System.out.println("f) Display full stored messages report");
            System.out.println("0) Back to main menu");
            System.out.print("Choose: ");

            String choice = input.nextLine().trim().toLowerCase();

            switch (choice) {
                case "a":
                    System.out.println(Message.displayStoredSenderRecipient());
                    break;

                case "b":
                    System.out.println(Message.longestStoredMessage());
                    break;

                case "c":
                    System.out.print("Enter Message ID to search: ");
                    String searchID = input.nextLine().trim();
                    System.out.println(Message.searchByMessageID(searchID));
                    break;

                case "d":
                    System.out.print("Enter recipient number to search: ");
                    String searchRecipient = input.nextLine().trim();
                    System.out.println(Message.searchByRecipient(searchRecipient));
                    break;

                case "e":
                    System.out.print("Enter Message Hash to delete: ");
                    String hash = input.nextLine().trim();
                    System.out.println(Message.deleteByHash(hash));
                    break;

                case "f":
                    System.out.println(Message.displayStoredReport());
                    break;

                case "0":
                    back = true;
                    break;

                default:
                    System.out.println("Invalid option. Please choose a–f or 0.");
            }
        }
    }
}
