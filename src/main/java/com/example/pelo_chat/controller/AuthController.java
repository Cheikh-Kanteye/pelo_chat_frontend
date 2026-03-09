package com.example.pelo_chat.controller;

import com.example.pelo_chat.service.SocketService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * PELO — Contrôleur de l'écran d'authentification (auth.fxml).
 * Gère le basculement entre Connexion et Inscription.
 */
public class AuthController {

    // ── Champs injectés par FXML ───────────────────────────
    @FXML
    private Label cardTitle;
    @FXML
    private Label cardSubtitle;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button submitButton;
    @FXML
    private Label switchPromptLabel;
    @FXML
    private Label switchLinkLabel;
    @FXML
    private CheckBox rememberMeCheck;
    @FXML
    private HBox rememberMeContainer;

    // Fichier texte (.properties) qui stocke les identifiants pour la connexion automatique.
    // Format : username=xxx \n password=xxx (en clair — usage local uniquement)
    private static final String SESSION_FILE = System.getProperty("user.home") + "/.pelo_session";

    private final SocketService socketService = new SocketService();
    private boolean isLoginMode = true;

    // ═══════════════════════════════════════════════════════
    // INITIALISATION
    // ═══════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        switchLinkLabel.setCursor(Cursor.HAND);
        showLoginMode();
        javafx.application.Platform.runLater(this::attemptAutoLogin);
    }

    // ═══════════════════════════════════════════════════════
    // HANDLERS FXML
    // ═══════════════════════════════════════════════════════

    @FXML
    private void onSubmit() {
        if (isLoginMode)
            handleLogin();
        else
            handleRegister();
    }

    @FXML
    private void onToggleMode() {
        if (isLoginMode)
            showRegisterMode();
        else
            showLoginMode();
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
        setVisible(confirmField, false);
        setVisible(rememberMeContainer, true);
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
        setVisible(confirmField, true);
        setVisible(rememberMeContainer, false);
        hideError();
    }

    // ═══════════════════════════════════════════════════════
    // LOGIQUE MÉTIER
    // ═══════════════════════════════════════════════════════

    /**
     * Lance la tentative de connexion :
     *  1. Ouvre le socket TCP vers le serveur
     *  2. Envoie le paquet LOGIN
     *  3. Attend la réponse (ACK = succès, autre = erreur avec message)
     *  4. Si "Se souvenir de moi" coché → sauvegarde les identifiants localement
     *  5. Navigue vers l'écran de chat
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        if (username.isEmpty() || passwordField.getText().isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        try {
            socketService.connect(packet -> {
                if ("ACK".equals(packet.getAction())) {
                    if (rememberMeCheck.isSelected()) {
                        saveSession(username, passwordField.getText());
                    }
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
            socketService.register(username, fullNameField.getText().trim(), passwordField.getText());
        } catch (Exception e) {
            showError("Impossible de joindre le serveur");
        }
    }

    /**
     * Charge chat.fxml, passe le socketService au ChatController, puis
     * remplace la scène d'auth par la scène de chat sur la même fenêtre.
     * Le socketService est réutilisé (connexion déjà ouverte, pas de reconnexion).
     */
    private void navigateToChat(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/pelo_chat/chat.fxml"));
            loader.setClassLoader(getClass().getClassLoader());
            Parent root = loader.load();

            ChatController ctrl = loader.getController();
            ctrl.init(username, socketService);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 960, 680));
            stage.setTitle("PELO — " + username);
        } catch (Exception e) {
            Throwable cause = (e.getCause() != null) ? e.getCause() : e;
            cause.printStackTrace();
            showError(cause.getClass().getSimpleName() + " : " + cause.getMessage());
        }
    }

    // ── Session Management ────────────────────────────────

    /**
     * Tente une connexion automatique si ~/.pelo_session existe.
     * Appelée via Platform.runLater() dans initialize() pour laisser
     * le temps au FXML de finir de se charger avant d'interagir avec les champs.
     */
    private void attemptAutoLogin() {
        Properties props = loadSession();
        if (props == null)
            return;

        String user = props.getProperty("username");
        String pass = props.getProperty("password");

        if (user != null && pass != null) {
            usernameField.setText(user);
            passwordField.setText(pass);
            rememberMeCheck.setSelected(true);
            handleLogin(); // Connexion transparente pour l'utilisateur
        }
    }

    private void saveSession(String username, String password) {
        try {
            Properties props = new Properties();
            props.setProperty("username", username);
            props.setProperty("password", password);
            try (OutputStream out = new FileOutputStream(SESSION_FILE)) {
                props.store(out, "PELO Chat Session");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Properties loadSession() {
        File file = new File(SESSION_FILE);
        if (!file.exists())
            return null;

        try (InputStream in = new FileInputStream(file)) {
            Properties props = new Properties();
            props.load(in);
            return props;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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

    /**
     * Affiche ou masque un nœud en gérant aussi managed pour ne pas réserver
     * d'espace.
     */
    private static void setVisible(javafx.scene.Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }
}
