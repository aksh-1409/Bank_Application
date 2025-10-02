import java.sql.*;

public class Account {
    private int id;
    private String name;
    private double balance;

    public Account(int id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public int getId() 
    { 
        return id; 
    }
    public String getName() 
    { 
        return name; 
    }
    public double getBalance() 
    {
         return balance; 
    }

   
    public static int createAccount(String name, double balance) throws SQLException {
        String sql = "insert into accounts (name, balance) values (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setDouble(2, balance);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

   
    public static Account getAccount(int id) throws SQLException {
        String sql = "select * from accounts where id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Account(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("balance")
                );
            }
        }
        return null;
    }

   
    public static void updateBalance(int id, double newBalance) throws SQLException {
        String sql = "update accounts set balance = ? where id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    
    public static void deleteAccount(int id) throws SQLException {
        String sql = "delete from accounts where id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
