import java.sql.*;

public class Transaction {

    public static void deposit(int accountId, double amount) throws SQLException {
        Account acc = Account.getAccount(accountId);
        if (acc != null) {
            double newBalance = acc.getBalance() + amount;
            Account.updateBalance(accountId, newBalance);
            recordTransaction(accountId, "DEPOSIT", amount);
            System.out.println("Deposit successful!");
        } else {
            System.out.println("Account not found!");
        }
    }

    public static void withdraw(int accountId, double amount) throws SQLException {
        Account acc = Account.getAccount(accountId);
        if (acc != null && acc.getBalance() >= amount) {
            double newBalance = acc.getBalance() - amount;
            Account.updateBalance(accountId, newBalance);
            recordTransaction(accountId, "WITHDRAW", amount);
            System.out.println("Withdrawal successful!");
        } else {
            System.out.println("Insufficient funds or account not found!");  
        }
    }

    private static void recordTransaction(int accountId, String type, double amount) throws SQLException {
        String sql = "insert into transactions (account_id, transaction_type, amount) values (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            stmt.setString(2, type);
            stmt.setDouble(3, amount);
            stmt.executeUpdate();
        }
    }

    public static void printAllTransactions(int accountId) throws SQLException {
        String sql = "select id, transaction_type, amount from transactions where account_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- Transaction History for Account ID: " + accountId + " ---");
            System.out.println("ID\tTYPE\t\tAMOUNT");
            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + "\t" +
                    rs.getString("transaction_type") + "\t\t" +
                    rs.getDouble("amount")
                );
            }
        }
    }
}
