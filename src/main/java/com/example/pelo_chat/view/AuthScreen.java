package com.example.pelo_chat.view;

import com.example.pelo_chat.HelloApplication;
import com.example.pelo_chat.PeloTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * PELO — Écran d'authentification
 * UI uniquement (sans logique backend).
 * Gère le basculement entre Connexion et Inscription.
 */
public class AuthScreen {

    private final Stage stage;
    private boolean isLoginMode = true;

    // Refs mutables (mises à jour lors du basculement de mode)
    private Label cardTitle;
    private Label cardSubtitle;
    private Label switchPromptLabel;
    private Label switchLinkLabel;
    private VBox  formContent;

    // Champs de formulaire (créés une fois, réutilisés)
    private final TextField     usernameField = makeField("Nom d'utilisateur");
    private final PasswordField passwordField = makePassField("Mot de passe");
    private final TextField     fullNameField = makeField("Nom complet");
    private final PasswordField confirmField  = makePassField("Confirmer le mot de passe");
    private final Label         errorLabel    = makeErrorLabel();

    public AuthScreen(Stage stage) {
        this.stage = stage;
    }

    // ═══════════════════════════════════════════════════════
    //  BUILD SCENE
    // ═══════════════════════════════════════════════════════

    public Scene build() {
        HBox root = new HBox(buildLeftPanel(), buildRightPanel());
        Scene scene = new Scene(root, 900, 580);
        PeloTheme.applyTo(scene);
        return scene;
    }

    // ═══════════════════════════════════════════════════════
    //  PANNEAU GAUCHE — Branding
    // ═══════════════════════════════════════════════════════

    private VBox buildLeftPanel() {
        // ── Wordmark ───────────────────────────────────────
        Label wordmark = new Label("P E L O");
        wordmark.setStyle(
            "-fx-font-family:'Montserrat';-fx-font-size:34px;-fx-font-weight:900;" +
            "-fx-text-fill:white;"
        );

        Label badge = new Label("Professional Messaging");
        badge.setStyle(
            "-fx-font-family:'Montserrat';-fx-font-size:10px;-fx-font-weight:bold;" +
            "-fx-text-fill:#1A1A00;-fx-background-color:#FDEF42;" +
            "-fx-background-radius:20px;-fx-padding:4px 12px;"
        );

        VBox topBrand = new VBox(10, wordmark, badge);

        // ── Tagline centrale ───────────────────────────────
        Label tagline = new Label("Xam Xam\nak Teggin");
        tagline.setStyle(
            "-fx-font-family:'Montserrat';-fx-font-size:26px;-fx-font-weight:bold;" +
            "-fx-text-fill:white;-fx-wrap-text:true;"
        );

        Label sub = new Label("Connect with Purpose");
        sub.setStyle(
            "-fx-font-family:'Nunito';-fx-font-size:13px;" +
            "-fx-text-fill:rgba(255,255,255,0.60);"
        );

        VBox taglineBox = new VBox(8, tagline, sub);

        // ── Décoration bas ─────────────────────────────────
        Region div = new Region();
        div.setStyle("-fx-background-color:rgba(255,255,255,0.15);");
        div.setPrefHeight(1);
        div.setMaxWidth(160);

        HBox avatarRow = new HBox(-8,
            PeloTheme.avatar("AB", PeloTheme.Styles.AVATAR_SM, PeloTheme.Styles.AVATAR_GREEN),
            PeloTheme.avatar("NF", PeloTheme.Styles.AVATAR_SM, PeloTheme.Styles.AVATAR_PURPLE),
            PeloTheme.avatar("KS", PeloTheme.Styles.AVATAR_SM, PeloTheme.Styles.AVATAR_RED),
            PeloTheme.avatar("DK", PeloTheme.Styles.AVATAR_SM, PeloTheme.Styles.AVATAR_GOLD),
            PeloTheme.avatar("MM", PeloTheme.Styles.AVATAR_SM, PeloTheme.Styles.AVATAR_TEAL)
        );
        avatarRow.setAlignment(Pos.CENTER_LEFT);

        Label members = new Label("+ 2 400 professionnels connectés");
        members.setStyle(
            "-fx-font-family:'Nunito';-fx-font-size:11px;" +
            "-fx-text-fill:rgba(255,255,255,0.50);"
        );

        VBox bottomDeco = new VBox(8, div, avatarRow, members);

        // ── Assemblage ─────────────────────────────────────
        VBox panel = new VBox(
            topBrand,
            PeloTheme.spacer(),
            taglineBox,
            PeloTheme.spacer(),
            bottomDeco
        );
        panel.setPadding(new Insets(48, 40, 40, 40));
        panel.setPrefWidth(380);
        panel.setMinWidth(380);
        panel.setMaxWidth(380);
        panel.setStyle(
            "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #1A2535, #006B32);"
        );
        return panel;
    }

    // ═══════════════════════════════════════════════════════
    //  PANNEAU DROIT — Formulaire
    // ═══════════════════════════════════════════════════════

    private VBox buildRightPanel() {
        // ── En-tête de la carte ────────────────────────────
        Label logoIcon = PeloTheme.avatar("P", PeloTheme.Styles.AVATAR_SM, PeloTheme.Styles.AVATAR_GREEN);

        cardTitle    = PeloTheme.titleXl("Connexion");
        cardSubtitle = PeloTheme.body("Bienvenue ! Entrez vos identifiants.");
        cardSubtitle.setWrapText(true);
        cardSubtitle.setMaxWidth(280);

        VBox titleBlock = new VBox(6, logoIcon, cardTitle, cardSubtitle);

        // ── Zone de formulaire (contenu dynamique) ─────────
        formContent = new VBox(10);

        // ── Lien de basculement ────────────────────────────
        switchPromptLabel = PeloTheme.body("Pas encore de compte ?");
        switchLinkLabel   = new Label("S'inscrire");
        switchLinkLabel.getStyleClass().add("pelo-auth-link");
        switchLinkLabel.setCursor(Cursor.HAND);
        switchLinkLabel.setOnMouseClicked(e -> toggleMode());

        HBox switchRow = new HBox(5, switchPromptLabel, switchLinkLabel);
        switchRow.setAlignment(Pos.CENTER);

        // ── Carte blanche ──────────────────────────────────
        VBox card = new VBox(20, titleBlock, formContent, switchRow);
        card.setPrefWidth(320);
        card.setMaxWidth(320);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 20px;" +
            "-fx-border-radius: 20px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 24, 0.0, 0, 6);" +
            "-fx-padding: 32px;"
        );

        // Remplir avec le formulaire de connexion initial
        renderLoginForm();

        // ── Panneau droit global ───────────────────────────
        VBox right = new VBox(card);
        right.setAlignment(Pos.CENTER);
        right.setStyle("-fx-background-color:" + PeloTheme.Colors.SURFACE_LIST + ";");
        right.setPadding(new Insets(32));
        HBox.setHgrow(right, Priority.ALWAYS);
        return right;
    }

    // ═══════════════════════════════════════════════════════
    //  RENDERERS DE FORMULAIRE
    // ═══════════════════════════════════════════════════════

    private void renderLoginForm() {
        isLoginMode = true;
        clearFields();
        cardTitle.setText("Connexion");
        cardSubtitle.setText("Bienvenue ! Entrez vos identifiants.");
        switchPromptLabel.setText("Pas encore de compte ?");
        switchLinkLabel.setText("S'inscrire");
        hideError();

        Button btn = authButton("Se connecter");
        btn.setOnAction(e -> handleLogin());

        formContent.getChildren().setAll(
            usernameField,
            passwordField,
            errorLabel,
            btn
        );
    }

    private void renderRegisterForm() {
        isLoginMode = false;
        clearFields();
        cardTitle.setText("Créer un compte");
        cardSubtitle.setText("Rejoignez PELO et collaborez avec vos équipes.");
        switchPromptLabel.setText("Vous avez déjà un compte ?");
        switchLinkLabel.setText("Se connecter");
        hideError();

        Button btn = authButton("Créer mon compte");
        btn.setOnAction(e -> handleRegister());

        formContent.getChildren().setAll(
            fullNameField,
            usernameField,
            passwordField,
            confirmField,
            errorLabel,
            btn
        );
    }

    private void toggleMode() {
        if (isLoginMode) renderRegisterForm();
        else             renderLoginForm();
    }

    // ═══════════════════════════════════════════════════════
    //  HANDLERS (UI only — pas de backend pour le moment)
    // ═══════════════════════════════════════════════════════

    private void handleLogin() {
        if (usernameField.getText().trim().isEmpty() || passwordField.getText().isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }
        navigateToMain(usernameField.getText().trim());
    }

    private void handleRegister() {
        if (fullNameField.getText().trim().isEmpty()
                || usernameField.getText().trim().isEmpty()
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
        navigateToMain(usernameField.getText().trim());
    }

    /** Navigation vers l'interface principale (design system showcase en attendant la vraie UI). */
    private void navigateToMain(String username) {
        ScrollPane showcase = new ScrollPane(HelloApplication.buildShowcase());
        showcase.setFitToWidth(true);
        showcase.setStyle("-fx-background-color:" + PeloTheme.Colors.SURFACE_LIST + ";");
        stage.getScene().setRoot(showcase);
        stage.setTitle("PELO — " + username);
        stage.setWidth(960);
        stage.setHeight(680);
    }

    // ═══════════════════════════════════════════════════════
    //  GESTION DES ERREURS
    // ═══════════════════════════════════════════════════════

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        fullNameField.clear();
        confirmField.clear();
    }

    // ═══════════════════════════════════════════════════════
    //  FABRIQUES DE COMPOSANTS
    // ═══════════════════════════════════════════════════════

    private static Button authButton(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("pelo-auth-btn");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    private static TextField makeField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("pelo-auth-input");
        return tf;
    }

    private static PasswordField makePassField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.getStyleClass().add("pelo-auth-input");
        return pf;
    }

    private static Label makeErrorLabel() {
        Label l = new Label();
        l.getStyleClass().add("pelo-auth-error");
        l.setWrapText(true);
        l.setVisible(false);
        l.setManaged(false);
        return l;
    }
}
