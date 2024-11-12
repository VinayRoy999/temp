package com.MovieSystemServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Signup {

    public boolean createAccount(Connection connection, Scanner scanner) {
        System.out.println("=== Signup ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter role (Admin/User): ");
        String role = scanner.nextLine();

        // SQL query to insert new user into Users table
        String userQuery = "INSERT INTO Users (Username, Email, Password, Role, IsLocked) VALUES (?, ?, ?, ?, ?)";
        String loginQuery = "INSERT INTO Login (UserID, Username, Password, Role) VALUES (?, ?, ?, ?)";

        try {
            // Insert into Users table
            try (PreparedStatement pstmt = connection.prepareStatement(userQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, username);
                pstmt.setString(2, email);
                pstmt.setString(3, password);  // In a real-world scenario, hash the password before saving
                pstmt.setString(4, role);
                pstmt.setInt(5, 0);  // Default to 0 (unlocked user)
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Get the generated UserID
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int userID = generatedKeys.getInt(1);

                            // Insert into Login table
                            try (PreparedStatement loginPstmt = connection.prepareStatement(loginQuery)) {
                                loginPstmt.setInt(1, userID);
                                loginPstmt.setString(2, username);
                                loginPstmt.setString(3, password);
                                loginPstmt.setString(4, role);
                                
                                int loginRowsAffected = loginPstmt.executeUpdate();
                                
                                if (loginRowsAffected > 0) {
                                    System.out.println("Account created successfully!");
                                    return true;  // Account creation successful
                                } else {
                                    System.out.println("Error while creating login credentials.");
                                    return false; // Login insert failed
                                }
                            }
                        } else {
                            System.out.println("Error generating UserID.");
                            return false; // Could not get UserID
                        }
                    }
                } else {
                    System.out.println("Account creation failed.");
                    return false; // User insert failed
                }
            }
        } catch (Exception e) {
            System.out.println("Error during account creation: " + e.getMessage());
            return false; // Error during account creation
        }
    }
}
