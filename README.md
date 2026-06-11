# 💬 ChatAppPart1 — PROG5121 Programming 1A POE

![Java](https://img.shields.io/badge/Language-Java-orange)
![Maven](https://img.shields.io/badge/Build-Maven-blue)
![JUnit](https://img.shields.io/badge/Tests-JUnit-green)
![GitHub](https://img.shields.io/badge/Version%20Control-GitHub-black)

---

## Project Overview

**ChatAppPart1** is a Java-based chat application developed as part of the 
PROG5121 Programming 1A Portfolio of Evidence (POE) 

The application is built in two parts:

| Part | Focus |
|------|-------|
| Part 1 | User Registration and Login with input validation |
| Part 2 | Messaging system with message tracking, hashing, and storage |
| Part 3 | Storing data and displaying task report |

---

##  Student Details

| Field | Info |
|-------|------|
| **Student Name** | [Gqukani Silindokuhle] |
| **Student Number** | [ST10461013] |
| **Module** | [PROG5121 — Programming 1A] |
| **Institution** | [Rosebank International] |
| **Lecturer** | [A.Phewa] |

---

##  How to Run This Project

### Requirements
- Java JDK 8 or higher
- NetBeans IDE
- Maven (built into NetBeans)

###  Steps to Run
1. Clone the repository:
```bash
   git clone https://github.com/YourUsername/ChatAppPart1.git
```
2. Open **NetBeans** → `File` → `Open Project` → select the cloned folder
3. Right-click the project → **Run**
4. Follow the on-screen prompts to register, log in, and send messages

---

##  Project Structure
ChatAppPart1/
└── src/
├── main/java/
│   ├── Login.java          # Part 1: Registration and login logic
│   ├── MainApp.java        # Part 1 & 2: User interaction and menu
│   └── Message.java        # Part 2: Messaging logic and methods
└── test/java/
├── LoginTest.java      # Part 1: JUnit tests for Login methods
└── MessageTest.java    # Part 2: JUnit tests for Message methods

---

##  Part 1 — Registration and Login

### Features
- User registration with full input validation
- Secure login using registered credentials
- Specific error messages for every invalid input

### Validation Rules

| Field | Rule |
|-------|------|
| Username | Must contain `_` and be 5 characters or fewer |
| Password | Min 8 characters, must include a capital letter, a number, and a special character |
| Phone Number | Must start with `+27` (SA international code) and be 12 characters or fewer |

### Part 1 Methods — `Login.java`

| Method | Returns | Description |
|--------|---------|-------------|
| `checkUserName(String)` | `boolean` | Validates username format |
| `checkPasswordComplexity(String)` | `boolean` | Validates password strength |
| `checkCellPhoneNumber(String)` | `boolean` | Validates SA phone number |
| `registerUser(String, String, String)` | `String` | Registers user if all inputs are valid |
| `loginUser(String, String)` | `boolean` | Checks credentials against stored data |
| `returnLoginStatus(boolean)` | `String` | Returns welcome or error message |

### Sample Registration Messages
✅ "User registered successfully."
❌ "Username is not correctly formatted; please ensure that your username
contains an underscore and is no more than five characters in length."
❌ "Password is not correctly formatted; please ensure that the password
contains at least eight characters, a capital letter, a number,
and a special character."
❌ "Cell phone number incorrectly formatted or does not contain
international code."

### Sample Login Messages
✅ "Welcome kyl_1 it is great to see you again."
❌ "Username or password incorrect, please try again."

---

##  Part 2 — QuickChat Messaging System

### Features
- Messaging is only available after a successful login
- User sets how many messages they want to send at the start
- Each message is tracked with a unique ID, hash, recipient, and content
- Messages can be sent, discarded, or stored
- Full message history is displayed on quit
- Total messages sent is accumulated and displayed

### QuickChat Menu
Welcome to QuickChat.
--- MENU ---

Send Messages
Show recently sent messages
Quit


> Option 2 is currently in development and displays: `"Coming Soon."`

### Message Structure

| Field | Description |
|-------|-------------|
| **Message ID** | Auto-generated random 10-digit number |
| **Message Hash** | Auto-generated: `XX:N:FIRSTWORDLASTWORD` (all caps) |
| **Recipient** | Cell number with international code, max 10 characters |
| **Message** | Text content, max 250 characters |
| **Num Sent** | Auto-incremented counter per session |

### Message Hash Example
Message ID  : 4782619053
Message No  : 1
Message     : "Hi Mike, can you join us for dinner tonight?"
Hash = 47:1:HITONIGHT

### Send Options

Send Message    → "Message successfully sent."
Disregard       → "Press 0 to delete the message."
Store Message   → "Message successfully stored."


### Part 2 Methods — `Message.java`

| Method | Returns | Description |
|--------|---------|-------------|
| `checkMessageID()` | `boolean` | Ensures message ID is not more than 10 characters |
| `checkRecipientCell()` | `String` | Validates recipient number format and length |
| `createMessageHash()` | `String` | Auto-generates the message hash |
| `checkMessageLength()` | `String` | Validates message does not exceed 250 characters |
| `sentMessage(int)` | `String` | Handles send, discard, or store choice |
| `printMessages()` | `String` | Returns all messages sent in the session |
| `returnTotalMessages()` | `int` | Returns total number of messages sent |
| `storeMessage()` | `void` | Stores message in JSON format |

### Message Length Validation Messages
✅ "Message ready to send."
❌ "Message exceeds 250 characters by X; please reduce the size."

### Recipient Validation Messages
✅ "Cell phone number successfully captured."
❌ "Cell phone number is incorrectly formatted or does not contain
an international code. Please correct the number and try again."

---

##  Unit Tests

### Part 1 Tests — `LoginTest.java`

| Test Method | What It Tests |
|-------------|---------------|
| `testValidUsername` | Valid username passes |
| `testInvalidUsernameNoUnderscore` | Username without `_` fails |
| `testInvalidUsernameTooLong` | Username over 5 characters fails |
| `testValidPassword` | Strong password passes |
| `testInvalidPasswordTooShort` | Weak password fails |
| `testValidPhoneNumber` | Correct +27 number passes |
| `testInvalidPhoneNumber` | Non-+27 number fails |
| `testRegisterUserSuccess` | Full registration returns success message |
| `testLoginSuccess` | Correct credentials return welcome message |
| `testLoginFailure` | Wrong credentials return error message |

### Part 2 Tests — `MessageTest.java`

| Test Method | What It Tests |
|-------------|---------------|
| `testMessageLengthSuccess` | Short message returns ready to send |
| `testMessageLengthFailure` | 260-char message returns exceeds error |
| `testRecipientSuccess` | Valid +27 number passes |
| `testRecipientFailure` | Invalid number without code fails |
| `testMessageHashFormat` | Hash matches expected pattern |
| `testMessageHashLastWord` | Hash ends with correct last word |
| `testMessageIDLength` | Message ID is 10 digits |
| `testSendMessage` | Returns "Message successfully sent." |
| `testDisregardMessage` | Returns "Press 0 to delete the message." |
| `testStoreMessage` | Returns "Message successfully stored." |

---

##  Commit History

This project was built incrementally — each commit represents a development milestone:

| Commit | Description |
|--------|-------------|
| Commit 1 | Project setup — Created Maven project with Login and MainApp |
| Commit 2 | Added username and password validation methods |
| Commit 3 | Added phone number validation and registerUser method |
| Commit 4 | Added loginUser and returnLoginStatus methods |
| Commit 5 | Completed MainApp with registration and login flow |
| Commit 6 | Added JUnit tests for all Login methods |
| Commit 7 | Added Message class with fields and constructor |
| Commit 8 | Added message ID and recipient validation methods |
| Commit 9 | Added message hash generation and length validation |
| Commit 10 | Added sentMessage, storeMessage, and printMessages methods |
| Commit 11 | Updated MainApp with QuickChat menu and messaging flow |
| Commit 12 | Added JUnit tests for all Message methods |
| Commit 13 | Updated README to cover Part 1 and Part 2 |

---

##  Repository Link

[https://github.com/YourUsername/ChatAppPart1](https://github.com/YourUsername/ChatAppPart1)

---

## 📝 License

This project is submitted for academic purposes at Rosebank International.  
© [Gqukani Silindokuhle] — [2026]


add README.md
git commit -m "Commit 13: Updated README to cover Part 1 and Part 2"
git push origin main
