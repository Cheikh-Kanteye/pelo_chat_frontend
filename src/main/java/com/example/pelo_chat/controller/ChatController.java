package com.example.pelo_chat.controller;

import com.example.pelo_chat.service.SocketService;
import com.example.pelo_chat.utils.Packet;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ChatController {

    @FXML private ListView<String> messageList;
    @FXML private TextField messageField;
    @FXML private TextField        recipientField;
    @FXML private Label statusLabel;

    private String        currentUser;
    private SocketService socketService;

    public void init(String username, SocketService service) {
        this.currentUser   = username;
        this.socketService = service;

        // Écoute les paquets entrants
        socketService.setOnPacketReceived(this::handlePacket);

        statusLabel.setText("Connecté en tant que : " + username);
    }

    private void handlePacket(Packet packet) {
        switch (packet.getAction()) {
            case "MESSAGE_RECEIVED" ->
                    messageList.getItems().add(
                            "[" + packet.getFrom() + "] " + packet.getContent()
                    );
            case "ACK" ->
                    messageList.getItems().add("✓ " + packet.getContent());
            case "ERROR" ->
                    statusLabel.setText("Erreur : " + packet.getContent());
        }
    }

    @FXML
    private void onSend() {
        String to      = recipientField.getText().trim();
        String content = messageField.getText().trim();

        if (to.isEmpty() || content.isEmpty()) return;

        socketService.send(new Packet("SEND_MESSAGE", currentUser, to, content));
        messageList.getItems().add("[moi → " + to + "] " + content);
        messageField.clear();
    }

    @FXML
    private void onDisconnect() {
        socketService.disconnect();
        Platform.exit();
    }
}
