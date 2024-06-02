package BankingManageementSystem;

/* importing required packages */
import java.sql.*;
import java.util.Scanner;

/* Class to handle account transactions */
public class AccountManager {
    private Connection connection;
    private Scanner scanner;

    /* parameterized constructor */
    AccountManager(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    /* function to deposit money */
    public void deposit_money(long account_number) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();

        try {
            connection.setAutoCommit(false);
            if (account_number != 0) {
                PreparedStatement preparedStatement = connection
                        .prepareStatement("SELECT * FROM accounts WHERE account_number = ? and security_pin = ? ");
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultset = preparedStatement.executeQuery();

                if (resultset.next()) {
                    String deposit_query = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(deposit_query);
                    preparedStatement1.setDouble(1, amount);
                    preparedStatement1.setLong(2, account_number);
                    int rowsAffected = preparedStatement1.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Rs." + amount + " deposited Successfully");
                        connection.commit();
                        connection.setAutoCommit(true);
                        return;
                    } else {
                        System.out.println("Transaction Failed!");
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                } else {
                    System.out.println("Invalid Security Pin!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    /* function to withdraw money */
    public void withdraw_money(long account_number) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();

        try {
            connection.setAutoCommit(false);
            if (account_number != 0) {
                PreparedStatement preparedStatement = connection
                        .prepareStatement("SELECT * FROM accounts WHERE account_number = ? and security_pin = ? ");
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    double current_balance = resultSet.getDouble("balance");
                    if (amount <= current_balance) {
                        String withdraw_query = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(withdraw_query);
                        preparedStatement1.setDouble(1, amount);
                        preparedStatement1.setLong(2, account_number);
                        int rowsAffected = preparedStatement1.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Rs." + amount + " debited Successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        } else {
                            System.out.println("Transaction Failed!");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    } else {
                        System.out.println("Insufficient Balance!");
                    }
                } else {
                    System.out.println("Invalid Pin!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    /* function to transfer money */
    public void transfer_money(long sender_account_number) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter Receiver Account Number: ");
        long receiver_account_number = scanner.nextLong();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            if (sender_account_number != 0 && receiver_account_number != 0) {
                // Checking for sender's account and security pin
                PreparedStatement preparedStatement1 = connection
                        .prepareStatement("SELECT * FROM accounts WHERE account_number = ? AND security_pin = ? ");
                preparedStatement1.setLong(1, sender_account_number);
                preparedStatement1.setString(2, security_pin);
                ResultSet resultSet1 = preparedStatement1.executeQuery();

                // Checking for receiver's account details
                PreparedStatement preparedStatement2 = connection
                        .prepareStatement("SELECT * FROM accounts WHERE account_number = ? AND security_pin = ? ");
                preparedStatement2.setLong(1, sender_account_number);
                preparedStatement2.setString(2, security_pin);
                ResultSet resultSet2 = preparedStatement1.executeQuery();

                if (resultSet1.next()) {
                    if (resultSet2.next()) {
                        double current_balance = resultSet1.getDouble("balance");
                        if (amount <= current_balance) {
                            // Debit and credit queries
                            String debit_query = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                            String credit_query = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

                            // Debit and Credit prepared Statements
                            PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);
                            PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);

                            // Set Values for debit and credit prepared statements
                            creditPreparedStatement.setDouble(1, amount);
                            creditPreparedStatement.setLong(2, receiver_account_number);
                            debitPreparedStatement.setDouble(1, amount);
                            debitPreparedStatement.setLong(2, sender_account_number);
                            int rowsAffected1 = debitPreparedStatement.executeUpdate();
                            int rowsAffected2 = creditPreparedStatement.executeUpdate();

                            if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                                System.out.println("Transaction Successful!");
                                System.out.println("Rs." + amount + " Transferred Successfully");
                                connection.commit();
                                connection.setAutoCommit(true);
                                return;
                            } else {
                                System.out.println("Transaction Failed");
                                connection.rollback();
                                connection.setAutoCommit(true);
                            }
                        } else {
                            System.out.println("Insufficient Balance!");
                        }
                    } else {
                        System.out.println("Receiver's Account does not Exist!");
                    }
                } else {
                    System.out.println("Invalid Security Pin!");
                }
            } else {
                System.out.println("Invalid account number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    /* function to get balance status of the account */
    public void getBalance(long account_number){
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT balance FROM Accounts WHERE account_number = ? AND security_pin = ?");
            preparedStatement.setLong(1, account_number);
            preparedStatement.setString(2, security_pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                double balance = resultSet.getDouble("balance");
                System.out.println("Balance: "+balance);
            }else{
                System.out.println("Invalid Pin!");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
