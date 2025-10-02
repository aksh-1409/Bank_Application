import java.util.*;
import java.sql.*;

public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== BANK MENU ===");
            System.out.println("1: Create Account");
            System.out.println("2: View Account");
            System.out.println("3: Deposit");
            System.out.println("4: Withdraw");
            System.out.println("5: View Transactions");
            System.out.println("6: Delete Account");
            System.out.println("0: Exit");
            System.out.print("Enter choice: ");

            int choice = Integer.parseInt(sc.nextLine());

            try {
                switch (choice) {
                    case 1 : {
                        System.out.print("Name: ");
                        String name = sc.nextLine();
                        System.out.print("Initial Balance: ");
                        double bal = Double.parseDouble(sc.nextLine());
                        int newId = Account.createAccount(name, bal);
                        System.out.println("Account created with ID: " + newId);
                        break;
                    }
                    case 2 : {
                        System.out.print("Account ID: ");
                        int id = Integer.parseInt(sc.nextLine());
                        Account acc = Account.getAccount(id);
                        if (acc != null)
                            System.out.println("ID: " + acc.getId() + " | Name: " + acc.getName() + " | Balance: " + acc.getBalance());
                        else
                            System.out.println("Account not found!");
                        break;    
                    }
                    case 3 : {
                        System.out.print("Account ID: ");
                        int id = Integer.parseInt(sc.nextLine());
                        System.out.print("Amount: ");
                        double amt = Double.parseDouble(sc.nextLine());
                        Transaction.deposit(id, amt);
                    break;
                    }
                    case 4 : {
                        System.out.print("Account ID: ");
                        int id = Integer.parseInt(sc.nextLine());
                        System.out.print("Amount: ");
                        double amt = Double.parseDouble(sc.nextLine());
                        Transaction.withdraw(id, amt);
                        break;
                    }
                    case 5 : {
                        System.out.print("Account ID: ");
                        int id = Integer.parseInt(sc.nextLine());
                        Transaction.printAllTransactions(id);
                    break;
                    }
                    case 6 : {
                        System.out.print("Account ID: ");
                        int id = Integer.parseInt(sc.nextLine());
                        Account.deleteAccount(id);
                        System.out.println("Account deleted.");
                        break;
                    }
                    case 0 : {
                        System.out.println("Exit!");
                        return;
                    }
                    default : System.out.println("Invalid choice!");
                    break;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
