package com.example.pelo_chat.controller;

import com.example.pelo_chat.service.SocketService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * PELO — Contrôleur de l'écran d'authentification (auth.fxml).
 * Gère le basculement entre Connexion et Inscription.
 */
public class AuthController {

    // ── Champs injectés par FXML ───────────────────────────
    @FXML private Label         cardTitle;
    @FXML private Label         cardSubtitle;
    @FXML private TextField     fullNameField;
    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private Label         errorLabel;
    @FXML private Button        submitButton;
    @FXML private Label         switchPromptLabel;
    @FXML private Label         switchLinkLabel;

    private final SocketService socketService = new SocketService();
    private boolean isLoginMode = true;

    // ═══════════════════════════════════════════════════════
    // INITIALISATION
    // ═══════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        switchLinkLabel.setCursor(Cursor.HAND);
        showLoginMode();
    }

    // ═══════════════════════════════════════════════════════
    // HANDLERS FXML
    // ═══════════════════════════════════════════════════════

    @FXML
    private void onSubmit() {
        if (isLoginMode) handleLogin();
        else             handleRegister();
    }

    @FXML
    private void onToggleMode() {
        if (isLoginMode) showRegisterMode();
        else             showLoginMode();
    }

    // ═══════════════════════════════════════════════════════
    // RENDERERS
    // ═══════════════════════════════════════════════════════

    private void showLoginMode() {
        isLoginMode = true;
        clearFields();
        cardTitle.setText("Connexion");
        cardSubtitle.setText("Bienvenue ! Entrez vos identifiants.");
        switchPromptLabel.setText("Pas encore de compte ?");
        switchLinkLabel.setText("S'inscrire");
        submitButton.setText("Se connecter");
        setVisible(fullNameField, false);
        setVisible(confirmField,  false);
        hideError();
    }

    private void showRegisterMode() {
        isLoginMode = false;
        clearFields();
        cardTitle.setText("Créer un compte");
        cardSubtitle.setText("Rejoignez PELO et collaborez avec vos équipes.");
        switchPromptLabel.setText("Vous avez déjà un compte ?");
        switchLinkLabel.setText("Se connecter");
        submitButton.setText("Créer mon compte");
        setVisible(fullNameField, true);
        setVisible(confirmField,  true);
        hideError();
    }

    // ═══════════════════════════════════════════════════════
    // LOGIQUE MÉTIER
    // ═══════════════════════════════════════════════════════

    private void handleLogin() {
        String username = usernameField.getText().trim();
        if (username.isEmpty() || passwordField.getText().isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        try {
            socketService.connect(packet -> {
                if ("ACK".equals(packet.getAction())) {
                    navigateToChat(username);
                } else {
                    showError(packet.getContent());
                }
            });
            socketService.login(username, passwordField.getText());
        } catch (Exception e) {
            showError("Impossible de joindre le serveur");
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        if (fullNameField.getText().trim().isEmpty()
                || username.isEmpty()
                || passwordField.getText().isEmpty()
                || confirmField.getText().isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }
        if (!passwordField.getText().equals(confirmField.getText())) {
            showError("Les mots de passe ne correspondent pas.");
            return;
        }
        if (passwordField.getText().length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        try {
            socketService.connect(packet -> {
                if ("REGISTER_OK".equals(packet.getAction())) {
                    showError("Inscription réussie ! Connectez-vous.");
                    showLoginMode();
                } else {
                    showError(packet.getContent());
                }
            });
            socketService.register(username, passwordField.getText());
        } catch (Exception e) {
            showError("Impossible de joindre le serveur");
        }
    }

    private void navigateToChat(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/pelo_chat/chat.fxml"));
            Parent root = loader.load();

            ChatController ctrl = loader.getController();
            ctrl.init(username, socketService);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 960, 680));
            stage.setTitle("PELO — " + username);
        } catch (Exception e) {
            showError("Erreur lors du chargement de l'interface : " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════
    // UTILITAIRES UI
    // ═══════════════════════════════════════════════════════

    private void showError(String msg) {
        errorLabel.setText(msg);
        setVisible(errorLabel, true);
    }

    private void hideError() {
        setVisible(errorLabel, false);
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        fullNameField.clear();
        confirmField.clear();
    }

    /** Affiche ou masque un nœud en gérant aussi managed pour ne pas réserver d'espace. */
    private static void setVisible(javafx.scene.Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }
}
