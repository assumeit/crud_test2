package com.example.test2222222;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.*;

public class LoginController {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Label message;

    @FXML
    protected void handleLogin(ActionEvent event) {
        String user = username.getText();
        String pass = password.getText();

        String jdbcUrl = "jdbc:mysql://localhost:3306/crudddtest2";
        String dbUser = "root";
        String dbPassword = "";  // Ensure this matches your database's password

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
            String query = "SELECT * FROM user_login_info WHERE Username=? AND Password=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user);
            statement.setString(2, pass);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                message.setText("Login successful!");
                Stage stage = (Stage) username.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("crud-view.fxml"));
                stage.setScene(new Scene(loader.load()));
                stage.show();
            } else {
                message.setText("Invalid username or password!");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            message.setText("Error connecting to database!");
        }
    }
}
