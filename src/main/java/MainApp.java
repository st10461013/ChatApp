package com.chatapp;

/**
 * MainApp is the entry point for the QuickChat application.
 * Handles user registration, login, and the message-sending loop.
 *
 * @author Silindokuhle Gqukani (ST10461013)
 */
import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Login login = new Login();

        // ── REGISTRATION (with retry loop) ────────────────────────────────
        System.out.println("=== USER REGISTRATION ===");
        String regResult = "";

        while (!regResult.equals("User registered successfully")) {
            System.out.print("Enter your first name: ");
            String firstName = input.nextLine();

            System.out.print("Enter your last name: ");
            String lastName = input.nextLine();

            System.out.print("Enter a username (max 5 chars, must include '_'): ");
            String username = input.nextLine();

            System.out.print("Enter a password (min 8 chars, capital, number, special): ");
            String password = input.nextLine();

            System.out.print("Enter your SA phone number (+27...): ");
            String phone = input.nextLine();

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

        // ── MESSAGING ─────────────────────────────────────────────────────
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
            System.out.println("\n--- MENU ---");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Choose an option: ");

            int menuChoice;
            try {
                menuChoice = Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input — please enter 1, 2, or 3.");
                continue;
            }

            switch (menuChoice) {

                case 1: // Send Messages — driven by a for loop
                    // Use a for loop as required by the POE specification
                    for (int i = 0; i < numMessages; i++) {

                        System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");

                        // ── Recipient (re-prompt on invalid) ──────────────
                        Message msg = null;
                        while (msg == null) {
                            System.out.print("Enter recipient cell number (+27...): ");
                            String recipient = input.nextLine();

                            System.out.print("Enter your message (max 250 chars): ");
                            String messageText = input.nextLine();

                            msg = new Message(recipient, messageText, i + 1);

                            String recipientCheck = msg.checkRecipientCell();
                            System.out.println(recipientCheck);

                            String lengthCheck = msg.checkMessageLength();
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

                        // Display ID and Hash BEFORE the send decision (POE requirement)
                        System.out.println("\nMessage ID:   " + msg.getMessageID());
                        System.out.println("Message Hash: " + msg.getMessageHash());

                        // ── Send / Discard / Store ─────────────────────────
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

                        String sendResult = msg.sentMessage(sendChoice);
                        System.out.println(sendResult);

                        // If sent, display full details immediately (POE requirement)
                        if (sendChoice == 1) {
                            System.out.println("\n--- MESSAGE DETAILS ---");
                            System.out.println("Message ID:   " + msg.getMessageID());
                            System.out.println("Message Hash: " + msg.getMessageHash());
                            System.out.println("Recipient:    " + msg.getRecipient());
                            System.out.println("Message:      " + msg.getMessage());
                        }

                        // If stored, also display full details
                        if (sendChoice == 3) {
                            System.out.println("\n--- STORED MESSAGE DETAILS ---");
                            System.out.println("Message ID:   " + msg.getMessageID());
                            System.out.println("Message Hash: " + msg.getMessageHash());
                            System.out.println("Recipient:    " + msg.getRecipient());
                            System.out.println("Message:      " + msg.getMessage());
                        }
                    }
                    break;

                case 2: // Coming Soon
                    System.out.println("Coming Soon.");
                    break;

                case 3: // Quit
                    System.out.println("\n--- ALL SENT MESSAGES ---");
                    System.out.println(Message.printMessages());
                    System.out.println("Total messages sent: " + Message.returnTotalMessages());
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please choose 1, 2, or 3.");
            }
        }

        input.close();
    }
}
