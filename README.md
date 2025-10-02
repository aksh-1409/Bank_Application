# Bank Management System

A comprehensive bank management application with both CLI and Web interfaces, connected to MySQL database.

## Features

- **Account Management**: Create, view, and delete accounts
- **Transactions**: Deposit and withdraw money
- **Transaction History**: View complete transaction records
- **Dual Interface**: Both command-line and modern web interface
- **Real-time Updates**: Live balance updates and notifications

## Prerequisites

- Java 8 or higher
- MySQL Server
- MySQL JDBC Driver (mysql-connector-java)
- Internet connection (for downloading Gson library)

## Database Setup

1. Create a MySQL database named `bankdb`
2. Create the required tables:

```sql
CREATE DATABASE bankdb;
USE bankdb;

CREATE TABLE accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    balance DECIMAL(10,2) NOT NULL
);

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);
```

3. Update database credentials in `DatabaseManager.java` if needed

## Installation & Running

### Option 1: Using Batch Files (Windows)

1. **Compile the application:**
   ```bash
   compile.bat
   ```

2. **Run CLI version:**
   ```bash
   java App
   ```

3. **Run Web version:**
   ```bash
   run-web.bat
   ```

### Option 2: Manual Compilation

1. **Download Gson library:**
   ```bash
   curl -L -o gson-2.10.1.jar https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
   ```

2. **Compile:**
   ```bash
   javac -cp ".;gson-2.10.1.jar" *.java
   ```

3. **Run CLI:**
   ```bash
   java App
   ```

4. **Run Web Server:**
   ```bash
   java -cp ".;gson-2.10.1.jar" BankAPI
   ```

## Usage

### CLI Interface
The command-line interface provides a menu-driven system:
- Create Account
- View Account
- Deposit Money
- Withdraw Money
- View Transaction History
- Delete Account

### Web Interface
Access the modern web interface at `http://localhost:8080` with features:
- **Responsive Design**: Works on desktop, tablet, and mobile
- **Tabbed Navigation**: Easy switching between operations
- **Real-time Feedback**: Instant success/error messages
- **Modern UI**: Clean, professional interface

## API Endpoints

The web server exposes REST API endpoints:

- `POST /api/accounts` - Create new account
- `GET /api/accounts/{id}` - Get account details
- `POST /api/accounts/{id}/deposit` - Deposit money
- `POST /api/accounts/{id}/withdraw` - Withdraw money
- `GET /api/accounts/{id}/transactions` - Get transaction history
- `DELETE /api/accounts/{id}` - Delete account

## File Structure

```
bank_application/
├── Account.java              # Account management logic
├── Transaction.java          # Transaction handling
├── DatabaseManager.java      # Database connection
├── App.java                 # CLI interface
├── BankAPI.java             # Web API server
├── compile.bat              # Compilation script
├── run-web.bat              # Web server launcher
├── gson-2.10.1.jar          # JSON library
└── frontend/
    ├── index.html           # Web interface
    ├── style.css            # Styling
    └── script.js            # JavaScript functionality
```

## Security Notes

- Database credentials are currently hardcoded for development
- In production, use environment variables or config files
- Consider adding authentication for the web interface
- Implement HTTPS for production deployment

## Troubleshooting

1. **Compilation Errors**: Ensure Java is installed and JAVA_HOME is set
2. **Database Connection**: Verify MySQL is running and credentials are correct
3. **Web Server Issues**: Check if port 8080 is available
4. **Missing Dependencies**: Run `compile.bat` to auto-download required libraries

## Future Enhancements

- User authentication and authorization
- Account statements and reports
- Transfer between accounts
- Interest calculation
- Audit logging
- Mobile app integration
