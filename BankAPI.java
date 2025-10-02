import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import java.sql.SQLException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class BankAPI {
    private static final Gson gson = new Gson();
    
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // API endpoints
        server.createContext("/api/accounts", new AccountHandler());
        server.createContext("/api/accounts/", new AccountOperationHandler());
        
        // Serve static files
        server.createContext("/", new StaticFileHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("Bank API Server started on http://localhost:8080");
        System.out.println("Web Interface: http://localhost:8080");
        System.out.println("Press Ctrl+C to stop the server");
    }
    
    static class AccountHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            try {
                if ("POST".equals(exchange.getRequestMethod())) {
                    // Create account
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    JsonObject json = gson.fromJson(requestBody, JsonObject.class);
                    
                    String name = json.get("name").getAsString();
                    double balance = json.get("balance").getAsDouble();
                    
                    int accountId = Account.createAccount(name, balance);
                    
                    JsonObject response = new JsonObject();
                    response.addProperty("success", true);
                    response.addProperty("accountId", accountId);
                    response.addProperty("message", "Account created successfully");
                    
                    sendJsonResponse(exchange, 200, response);
                } else {
                    sendErrorResponse(exchange, 405, "Method not allowed");
                }
            } catch (Exception e) {
                sendErrorResponse(exchange, 500, "Error: " + e.getMessage());
            }
        }
    }
    
    static class AccountOperationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            try {
                String path = exchange.getRequestURI().getPath();
                String[] pathParts = path.split("/");
                
                if (pathParts.length >= 4) {
                    int accountId = Integer.parseInt(pathParts[3]);
                    
                    if ("GET".equals(exchange.getRequestMethod())) {
                        if (pathParts.length == 4) {
                            // Get account details
                            Account account = Account.getAccount(accountId);
                            if (account != null) {
                                JsonObject response = new JsonObject();
                                response.addProperty("success", true);
                                response.addProperty("id", account.getId());
                                response.addProperty("name", account.getName());
                                response.addProperty("balance", account.getBalance());
                                sendJsonResponse(exchange, 200, response);
                            } else {
                                sendErrorResponse(exchange, 404, "Account not found");
                            }
                        } else if (pathParts.length == 5 && "transactions".equals(pathParts[4])) {
                            // Get transactions
                            try {
                                List<Map<String, Object>> transactions = getTransactionHistory(accountId);
                                JsonObject response = new JsonObject();
                                response.addProperty("success", true);
                                response.add("transactions", gson.toJsonTree(transactions));
                                sendJsonResponse(exchange, 200, response);
                            } catch (Exception e) {
                                sendErrorResponse(exchange, 500, "Error retrieving transactions: " + e.getMessage());
                            }
                        }
                    } else if ("POST".equals(exchange.getRequestMethod())) {
                        String requestBody = new String(exchange.getRequestBody().readAllBytes());
                        JsonObject json = gson.fromJson(requestBody, JsonObject.class);
                        
                        if (pathParts.length == 5) {
                            String operation = pathParts[4];
                            double amount = json.get("amount").getAsDouble();
                            
                            if ("deposit".equals(operation)) {
                                boolean success = performDeposit(accountId, amount);
                                JsonObject response = new JsonObject();
                                if (success) {
                                    response.addProperty("success", true);
                                    response.addProperty("message", "Deposit successful");
                                    sendJsonResponse(exchange, 200, response);
                                } else {
                                    response.addProperty("success", false);
                                    response.addProperty("error", "Account not found");
                                    sendJsonResponse(exchange, 400, response);
                                }
                            } else if ("withdraw".equals(operation)) {
                                String result = performWithdraw(accountId, amount);
                                JsonObject response = new JsonObject();
                                if ("SUCCESS".equals(result)) {
                                    response.addProperty("success", true);
                                    response.addProperty("message", "Withdrawal successful");
                                    sendJsonResponse(exchange, 200, response);
                                } else if ("INSUFFICIENT_FUNDS".equals(result)) {
                                    response.addProperty("success", false);
                                    response.addProperty("error", "Insufficient funds. Transaction cannot be completed.");
                                    sendJsonResponse(exchange, 400, response);
                                } else {
                                    response.addProperty("success", false);
                                    response.addProperty("error", "Account not found");
                                    sendJsonResponse(exchange, 400, response);
                                }
                            }
                        }
                    } else if ("DELETE".equals(exchange.getRequestMethod())) {
                        // Delete account
                        Account.deleteAccount(accountId);
                        JsonObject response = new JsonObject();
                        response.addProperty("success", true);
                        response.addProperty("message", "Account deleted successfully");
                        sendJsonResponse(exchange, 200, response);
                    }
                }
            } catch (Exception e) {
                sendErrorResponse(exchange, 500, "Error: " + e.getMessage());
            }
        }
    }
    
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if ("/".equals(path)) {
                path = "/index.html";
            }
            
            String filePath = "frontend" + path;
            File file = new File(filePath);
            
            if (file.exists() && !file.isDirectory()) {
                String contentType = getContentType(filePath);
                exchange.getResponseHeaders().set("Content-Type", contentType);
                
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                exchange.sendResponseHeaders(200, fileBytes.length);
                exchange.getResponseBody().write(fileBytes);
            } else {
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.length());
                exchange.getResponseBody().write(response.getBytes());
            }
            exchange.getResponseBody().close();
        }
        
        private String getContentType(String filePath) {
            if (filePath.endsWith(".html")) return "text/html";
            if (filePath.endsWith(".css")) return "text/css";
            if (filePath.endsWith(".js")) return "application/javascript";
            if (filePath.endsWith(".json")) return "application/json";
            return "text/plain";
        }
    }
    
    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }
    
    private static void sendJsonResponse(HttpExchange exchange, int statusCode, JsonObject response) throws IOException {
        String jsonResponse = gson.toJson(response);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, jsonResponse.length());
        exchange.getResponseBody().write(jsonResponse.getBytes());
        exchange.getResponseBody().close();
    }
    
    private static void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        JsonObject error = new JsonObject();
        error.addProperty("success", false);
        error.addProperty("error", message);
        sendJsonResponse(exchange, statusCode, error);
    }
    
    // Helper method for deposit that returns success status
    private static boolean performDeposit(int accountId, double amount) throws SQLException {
        Account acc = Account.getAccount(accountId);
        if (acc != null) {
            double newBalance = acc.getBalance() + amount;
            Account.updateBalance(accountId, newBalance);
            recordTransaction(accountId, "DEPOSIT", amount);
            return true;
        }
        return false;
    }
    
    // Helper method for withdraw that returns detailed status
    private static String performWithdraw(int accountId, double amount) throws SQLException {
        Account acc = Account.getAccount(accountId);
        if (acc == null) {
            return "ACCOUNT_NOT_FOUND";
        }
        if (acc.getBalance() < amount) {
            return "INSUFFICIENT_FUNDS";
        }
        double newBalance = acc.getBalance() - amount;
        Account.updateBalance(accountId, newBalance);
        recordTransaction(accountId, "WITHDRAW", amount);
        return "SUCCESS";
    }
    
    // Helper method to record transactions (same logic as Transaction class)
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
    
    // Helper method to get transaction history
    private static List<Map<String, Object>> getTransactionHistory(int accountId) throws SQLException {
        List<Map<String, Object>> transactions = new ArrayList<>();
        String sql = "select id, transaction_type, amount from transactions where account_id = ? order by id desc";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("id", rs.getInt("id"));
                transaction.put("type", rs.getString("transaction_type"));
                transaction.put("amount", rs.getDouble("amount"));
                transaction.put("timestamp", "N/A"); // No timestamp column in your database
                transactions.add(transaction);
            }
        }
        
        return transactions;
    }
}
