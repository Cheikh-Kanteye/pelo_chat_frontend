package com.example.pelo_chat.controller;

import com.example.pelo_chat.service.SocketService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private Label errorLabel;

    private final SocketService socketService = new SocketService();

    @FXML
    private void onLogin() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            errorLabel.setText("Username requis");
            return;
        }

        try {
            socketService.connect(packet -> {
                if ("ACK".equals(packet.getAction())) {
                    ouvrirChatWindow(username, socketService);
                } else {
                    errorLabel.setText(packet.getContent());
                }
            });
            socketService.login(username, ""); // LoginController only has username field in the current snippet
        } catch (IOException e) {
            errorLabel.setText("Impossible de joindre le serveur");
        }
    }

    private void ouvrirChatWindow(String username, SocketService service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chat.fxml"));
            Parent root = loader.load();

            ChatController ctrl = loader.getController();
            ctrl.init(username, service);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            errorLabel.setText("Erreur ouverture chat");
        }
    }
}
