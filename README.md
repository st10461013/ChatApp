# ChatAppPart1 — PROG5121 Programming 1A POE

##  Project Overview
ChatAppPart1 is Part 1 of a Java-based chat application developed for the 
PROG5121 Programming 1A module. It implements user registration and login 
functionality with full input validation.

---

##  Student Details
- **Name:** Gqukani Silindokuhle
- **Student Number:** ST10461013 
- **Module:** PROG5121 — Programming 1A
- **Institution:** IIE Rosebank College NMB

---

##  How to Run

### Requirements
- Java JDK 8 or higher
- NetBeans IDE
- Maven (built into NetBeans)

### Steps
1. Clone the repo:

2. 2. Open NetBeans → File → Open Project → select the folder
3. Right-click project → Run

---

##  Features

### User Registration
- Username must contain `_` and be 5 characters or fewer
- Password must be 8+ characters with a capital letter, number, and special character
- Phone number must start with `+27` and be 12 characters or fewer
- Returns specific error messages for each invalid field
- Stores details on successful registration

### User Login
- Compares entered credentials to stored registration details
- Returns personalised welcome message on success
- Returns error message on failure

---

##  Project Structur
ChatAppPart1/
└── src/
├── main/java/
│   ├── Login.java        # All validation and logic methods
│   └── MainApp.java      # User interaction via Scanner
└── test/java/
└── LoginTest.java    # JUnit unit tests

---

## 🧪 JUnit Tests

| Test Method | What It Tests |
|---|---|
| `testValidUsername` | Valid username passes |
| `testInvalidUsernameNoUnderscore` | Username without `_` fails |
| `testInvalidUsernameTooLong` | Username over 5 chars fails |
| `testValidPassword` | Strong password passes |
| `testInvalidPasswordTooShort` | Password under 8 chars fails |
| `testValidPhoneNumber` | +27 number passes |
| `testInvalidPhoneNumber` | Non-+27 number fails |
| `testRegisterUserSuccess` | Full registration returns success message |
| `testLoginSuccess` | Correct credentials return welcome message |
| `testLoginFailure` | Wrong credentials return error message |

---

## 📋 Method Summary

| Method | Returns | Purpose |
|---|---|---|
| `checkUserName(String)` | `boolean` | Validates username format |
| `checkPasswordComplexity(String)` | `boolean` | Validates password strength |
| `checkCellPhoneNumber(String)` | `boolean` | Validates SA phone number |
| `registerUser(String, String, String)` | `String` | Registers user if all valid |
| `loginUser(String, String)` | `boolean` | Checks login credentials |
| `returnLoginStatus(boolean)` | `String` | Returns login feedback message |

---

## 📸 Commit History
This project was built incrementally with 6+ commits tracking development progress:
1. Project setup
2. Username and password validation
3. Phone validation and registration
4. Login methods
5. MainApp completed
6. JUnit tests added

---

##  Repository
