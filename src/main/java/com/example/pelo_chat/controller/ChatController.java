package com.example.pelo_chat.controller;

import com.example.pelo_chat.model.User;
import com.example.pelo_chat.service.SocketService;
import com.example.pelo_chat.utils.Packet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Popup;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatController {

    // ── FXML injections ───────────────────────────────────
    @FXML
    private VBox chatListContainer;
    @FXML
    private VBox messageContainer;
    @FXML
    private TextField messageField;
    @FXML
    private TextField searchField;
    @FXML
    private Button emojiButton;

    @FXML
    private Label headerName;
    @FXML
    private Label headerStatus;
    @FXML
    private Label headerRole;
    @FXML
    private Circle headerStatusDot;
    @FXML
    private Circle headerStatusInfoDot;

    @FXML
    private Text rpInitials;
    @FXML
    private Label rpName;
    @FXML
    private Label rpRole;
    @FXML
    private Circle rpStatusDot;
    @FXML
    private Label rpStatusLabel;

    @FXML
    private Circle userStatusDot;

    // ── État ─────────────────────────────────────────────
    private String currentUser;
    private SocketService socketService;
    private Popup emojiPickerPopup;
    private Path historyDir;

    private Contact currentContact;
    private HBox activeItem;

    private final List<Contact> contacts = new ArrayList<>();
    private final Map<String, List<ChatMessage>> history = new HashMap<>();
    private final Map<String, HBox> itemByPeer = new HashMap<>();
    private final Map<String, Circle> statusDotByPeer = new HashMap<>();

    // ── Emojis ───────────────────────────────────────────
    private static final String[] EMOJIS = {
            "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇",
            "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚",
            "😋", "😛", "😜", "🤪", "😝", "🤑", "🤗", "🤭", "🤔", "🤐",
            "😐", "😑", "😶", "😏", "😒", "🙄", "😬", "😔", "😪", "😴",
            "😷", "🤒", "🤕", "🤢", "🤮", "🤧", "🥵", "🥶", "😵", "🤯",
            "🥳", "😎", "🤓", "🧐", "😕", "😟", "🙁", "☹", "😮", "😯",
            "😲", "😳", "🥺", "😦", "😧", "😨", "😰", "😥", "😢", "😭",
            "😱", "😖", "😣", "😞", "😓", "😩", "😫", "🥱", "😤", "😡",
            "😠", "🤬", "😈", "👿", "💀", "💩", "🤡", "👻", "👽", "🤖",
            "👋", "🤚", "✋", "🖖", "👌", "✌", "🤞", "🤟", "🤘", "🤙",
            "👈", "👉", "👆", "👇", "☝", "👍", "👎", "✊", "👊", "👏",
            "🙌", "🤝", "🙏", "💪", "❤", "🧡", "💛", "💚", "💙", "💜",
            "🖤", "💔", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "✨",
            "🌟", "⭐", "🌈", "☀", "🌙", "⚡", "❄", "🔥", "💧", "🌊",
            "🎉", "🎊", "🎈", "🎁", "🏆", "🥇", "🎯", "🎮", "🎵", "🎶",
            "🍎", "🍊", "🍋", "🍇", "🍓", "🍕", "🍔", "🍟", "🍦", "☕",
            "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯",
            "🦁", "🐮", "🐷", "🐸", "🐵", "🙈", "🙉", "🙊", "🐔", "🐧"
    };

    // ── Constantes ───────────────────────────────────────
    private static final String[] AVATAR_STYLES = {
            "pelo-avatar-navy", "pelo-avatar-teal", "pelo-avatar-green",
            "pelo-avatar-purple", "pelo-avatar-red", "pelo-avatar-gold"
    };

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // ═══════════════════════════════════════════════════════
    // MODÈLES INTERNES
    // ═══════════════════════════════════════════════════════

    private record Contact(
            String username,
            String displayName,
            String initials,
            String avatarStyle,
            String role,
            boolean group,
            String status) {
        boolean isOnline() {
            return "ONLINE".equalsIgnoreCase(status);
        }
    }

    private record ChatMessage(
            String from,
            String content,
            boolean mine,
            String time) {
    }

    // ═══════════════════════════════════════════════════════
    // INITIALISATION
    // ═══════════════════════════════════════════════════════

    public void init(String username, SocketService service) {
        this.currentUser = username;
        this.socketService = service;

        // Préparer le dossier de persistance et charger l'historique local
        historyDir = Paths.get(System.getProperty("user.home"), ".pelo_chat", username, "messages");
        try {
            Files.createDirectories(historyDir);
        } catch (IOException ignored) {
        }
        loadLocalHistory();

        socketService.setOnPacketReceived(this::handlePacket);

        showLoadingState();
        socketService.requestUsers(currentUser);

        // Statut personnel : En ligne
        if (userStatusDot != null) {
            userStatusDot.setFill(Color.web("#2ecc71"));
        }
    }

    private void showLoadingState() {
        chatListContainer.getChildren().clear();
        Label loading = new Label("Chargement des contacts…");
        loading.getStyleClass().add("pelo-meta");
        loading.setStyle("-fx-padding: 16;");
        chatListContainer.getChildren().add(loading);
    }

    // ═══════════════════════════════════════════════════════
    // PERSISTANCE LOCALE DE L'HISTORIQUE
    // ═══════════════════════════════════════════════════════

    /**
     * Charge toutes les conversations sauvegardées depuis
     * ~/.pelo_chat/{user}/messages/.
     */
    private void loadLocalHistory() {
        if (historyDir == null || !Files.exists(historyDir))
            return;
        Type listType = new TypeToken<List<ChatMessage>>() {
        }.getType();
        try (var stream = Files.list(historyDir)) {
            stream.filter(p -> p.toString().endsWith(".json"))
                    .forEach(p -> {
                        String peer = p.getFileName().toString().replace(".json", "");
                        try {
                            String json = Files.readString(p);
                            List<ChatMessage> msgs = new Gson().fromJson(json, listType);
                            if (msgs != null && !msgs.isEmpty()) {
                                history.put(peer, new ArrayList<>(msgs));
                            }
                        } catch (IOException e) {
                            System.err.println("Lecture historique échouée pour " + peer + " : " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("Impossible de lister le dossier historique : " + e.getMessage());
        }
    }

    /** Sauvegarde la conversation avec un pair dans un fichier JSON. */
    private void saveConversation(String peer) {
        if (historyDir == null)
            return;
        List<ChatMessage> msgs = history.get(peer);
        if (msgs == null || msgs.isEmpty())
            return;
        Path file = historyDir.resolve(peer + ".json");
        try {
            Files.writeString(file, new Gson().toJson(msgs));
        } catch (IOException e) {
            System.err.println("Sauvegarde échouée pour " + peer + " : " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════
    // LISTE DES CONVERSATIONS
    // ═══════════════════════════════════════════════════════

    private void renderContactList() {
        chatListContainer.getChildren().clear();
        itemByPeer.clear();
        statusDotByPeer.clear();

        for (Contact c : contacts) {
            List<ChatMessage> msgs = history.getOrDefault(c.username(), List.of());
            String preview = msgs.isEmpty() ? "" : msgs.get(msgs.size() - 1).content();
            String time = msgs.isEmpty() ? "" : msgs.get(msgs.size() - 1).time();

            HBox item = buildChatItem(c, preview, time);
            itemByPeer.put(c.username(), item);
            chatListContainer.getChildren().add(item);
        }
    }

    private HBox buildChatItem(Contact c, String preview, String time) {
        StackPane avatar = makeAvatar(c.initials(), "pelo-avatar-md", c.avatarStyle(), false);

        if (!c.group()) {
            Circle dot = new Circle(5);
            dot.setFill(c.isOnline() ? Color.web("#2ecc71") : Color.web("#95a5a6"));
            dot.setStroke(Color.WHITE);
            dot.setStrokeWidth(1.5);
            StackPane.setAlignment(dot, Pos.BOTTOM_RIGHT);
            avatar.getChildren().add(dot);
            statusDotByPeer.put(c.username(), dot);
        }

        Label nameLabel = new Label(c.displayName());
        nameLabel.getStyleClass().add("pelo-title-sm");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("pelo-meta");

        HBox nameRow = new HBox(nameLabel, spacer, timeLabel);
        nameRow.setAlignment(Pos.CENTER_LEFT);

        Label previewLabel = new Label(preview);
        previewLabel.getStyleClass().add("pelo-body");
        previewLabel.setMaxWidth(180);

        VBox info = new VBox(2, nameRow, previewLabel);
        HBox.setHgrow(info, Priority.ALWAYS);

        HBox item = new HBox(12, avatar, info);
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("pelo-chat-item");
        item.setOnMouseClicked(e -> selectConversation(c, item));

        return item;
    }

    // ═══════════════════════════════════════════════════════
    // SÉLECTION D'UNE CONVERSATION
    // ═══════════════════════════════════════════════════════

    private void selectConversation(Contact contact, HBox item) {
        // Toujours récupérer l'objet Contact le plus récent du cache local
        // pour éviter d'utiliser une référence "stale" (ex: Capturée alors qu'il était
        // OFFLINE)
        final String searchName = contact.username();
        Contact freshContact = contacts.stream()
                .filter(c -> c.username().equalsIgnoreCase(searchName))
                .findFirst()
                .orElse(contact);

        if (activeItem != null)
            activeItem.getStyleClass().remove("active");

        item.getStyleClass().add("active");
        activeItem = item;
        currentContact = freshContact;

        // Header
        headerName.setText(freshContact.displayName());
        headerRole.setText(freshContact.role().isEmpty() ? "" : "• " + freshContact.role());
        updateHeaderStatus(freshContact);

        // Panneau de détails
        rpInitials.setText(freshContact.initials());
        rpName.setText(freshContact.displayName());
        rpRole.setText(freshContact.role());
        updateRightPanelStatus(freshContact);

        refreshMessages();
    }

    // ═══════════════════════════════════════════════════════
    // ZONE DE MESSAGES
    // ═══════════════════════════════════════════════════════

    private void refreshMessages() {
        messageContainer.getChildren().clear();

        List<ChatMessage> msgs = history.getOrDefault(
                currentContact.username(), List.of());

        if (msgs.isEmpty()) {
            Label empty = new Label("Commencez la conversation !");
            empty.getStyleClass().add("pelo-meta");
            HBox center = new HBox(empty);
            center.setAlignment(Pos.CENTER);
            center.setStyle("-fx-padding: 24;");
            messageContainer.getChildren().add(center);
            return;
        }

        for (ChatMessage msg : msgs) {
            messageContainer.getChildren().add(
                    msg.mine() ? buildSentBubble(msg) : buildReceivedBubble(msg));
        }
    }

    private Node buildSentBubble(ChatMessage msg) {
        Label text = new Label(msg.content());
        text.setWrapText(true);
        text.setStyle("-fx-text-fill: white;");
        text.getStyleClass().add("pelo-body");

        VBox bubble = new VBox(text);
        bubble.getStyleClass().addAll("pelo-bubble", "pelo-bubble-sent");

        Label timeLabel = new Label(msg.time());
        timeLabel.getStyleClass().add("pelo-meta");

        Text checks = new Text("✓✓");
        checks.setFill(Color.web("#2ecc71"));

        HBox timeRow = new HBox(5, timeLabel, checks);
        timeRow.setAlignment(Pos.CENTER_RIGHT);

        VBox content = new VBox(5, bubble, timeRow);
        content.setAlignment(Pos.TOP_RIGHT);

        HBox wrapper = new HBox(content);
        wrapper.setAlignment(Pos.TOP_RIGHT);
        return wrapper;
    }

    private Node buildReceivedBubble(ChatMessage msg) {
        StackPane avatar = makeAvatar(
                currentContact.initials(), "pelo-avatar-sm",
                currentContact.avatarStyle(), false);

        Label senderName = new Label(msg.from());
        senderName.getStyleClass().add("pelo-meta");

        Label text = new Label(msg.content());
        text.setWrapText(true);
        text.getStyleClass().add("pelo-body-navy");

        VBox bubble = new VBox(text);
        bubble.getStyleClass().addAll("pelo-bubble", "pelo-bubble-received");

        Label timeLabel = new Label(msg.time());
        timeLabel.getStyleClass().add("pelo-meta");

        VBox content = new VBox(5, senderName, bubble, timeLabel);

        HBox wrapper = new HBox(12, avatar, content);
        StackPane.setAlignment(avatar, Pos.TOP_CENTER);
        return wrapper;
    }

    // ═══════════════════════════════════════════════════════
    // ENVOI D'UN MESSAGE
    // ═══════════════════════════════════════════════════════

    @FXML
    private void onSend() {
        if (currentContact == null)
            return;

        String content = messageField.getText().trim();
        if (content.isEmpty())
            return;

        socketService.send(new Packet(
                "SEND_MESSAGE", currentUser, currentContact.username(), content));

        ChatMessage msg = new ChatMessage(currentUser, content, true, now());
        recordMessage(currentContact.username(), msg);
        messageContainer.getChildren().add(buildSentBubble(msg));

        messageField.clear();
    }

    // ═══════════════════════════════════════════════════════
    // RÉCEPTION D'UN MESSAGE
    // ═══════════════════════════════════════════════════════

    private void handlePacket(Packet packet) {
        if (packet == null || packet.getAction() == null)
            return;

        switch (packet.getAction()) {

            case "USERS_LIST" -> Platform.runLater(() -> {
                User[] users = new Gson().fromJson(packet.getContent(), User[].class);
                populateContacts(users);
            });

            case "MESSAGE_RECEIVED" -> Platform.runLater(() -> {
                String from = packet.getFrom();
                String content = packet.getContent();

                // Ajouter l'expéditeur si inconnu (forcément en ligne s'il envoie un msg)
                if (contacts.stream().noneMatch(c -> c.username().equalsIgnoreCase(from))) {
                    contacts.add(new Contact(
                            from, from, initials(from),
                            AVATAR_STYLES[contacts.size() % AVATAR_STYLES.length],
                            "", false, "ONLINE"));
                    renderContactList();
                    restoreActiveStyle();
                }

                ChatMessage msg = new ChatMessage(from, content, false, now());
                recordMessage(from, msg);

                if (currentContact != null && from.equalsIgnoreCase(currentContact.username())) {
                    messageContainer.getChildren().add(buildReceivedBubble(msg));
                }
            });

            case "STATUS_UPDATE" -> Platform.runLater(() -> {
                Type mapType = new TypeToken<Map<String, String>>() {
                }.getType();
                Map<String, String> data = new Gson().fromJson(packet.getContent(), mapType);
                if (data != null && data.containsKey("username") && data.containsKey("status")) {
                    updateContactStatus(data.get("username"), data.get("status"));
                }
            });

            case "ERROR" -> Platform.runLater(() -> System.err.println("Erreur serveur : " + packet.getContent()));
        }
    }

    /** Construit la liste de contacts à partir de la réponse serveur. */
    private void populateContacts(User[] users) {
        contacts.clear();
        for (int i = 0; i < users.length; i++) {
            User u = users[i];
            String displayName = (u.getFullName() != null && !u.getFullName().isBlank())
                    ? u.getFullName()
                    : u.getUsername();
            String status = u.getStatus() != null ? u.getStatus() : "OFFLINE";
            contacts.add(new Contact(
                    u.getUsername(),
                    displayName,
                    initials(displayName),
                    AVATAR_STYLES[i % AVATAR_STYLES.length],
                    "", // le serveur n'envoie pas de rôle pour l'instant
                    false,
                    status));
        }
        renderContactList();

        // Restaurer les previews des messages déjà reçus (ex. messages hors-ligne)
        for (Contact c : contacts) {
            List<ChatMessage> msgs = history.get(c.username());
            if (msgs != null && !msgs.isEmpty()) {
                ChatMessage last = msgs.get(msgs.size() - 1);
                HBox item = itemByPeer.get(c.username());
                if (item != null)
                    updatePreview(item, last.content(), last.time());
            }
        }

        // Sélectionner en priorité le contact qui a des messages en attente
        Contact firstWithMessages = contacts.stream()
                .filter(c -> history.containsKey(c.username()) && !history.get(c.username()).isEmpty())
                .findFirst()
                .orElse(contacts.isEmpty() ? null : contacts.get(0));

        if (firstWithMessages != null) {
            selectConversation(firstWithMessages, itemByPeer.get(firstWithMessages.username()));
        }
    }

    /**
     * Met à jour le statut d'un contact en temps réel sans reconstruire toute la
     * liste.
     * Appelée sur réception d'un paquet USER_STATUS_CHANGED.
     */
    private void updateContactStatus(String username, String newStatus) {
        contacts.replaceAll(c -> c.username().equalsIgnoreCase(username)
                ? new Contact(c.username(), c.displayName(), c.initials(),
                        c.avatarStyle(), c.role(), c.group(), newStatus)
                : c);

        // Mettre à jour le point coloré dans la liste
        Circle dot = statusDotByPeer.get(username);
        if (dot == null) {
            // Tentative de récupération insensible à la casse
            dot = statusDotByPeer.entrySet().stream()
                    .filter(e -> e.getKey().equalsIgnoreCase(username))
                    .map(Map.Entry::getValue)
                    .findFirst().orElse(null);
        }

        if (dot != null) {
            boolean online = "ONLINE".equalsIgnoreCase(newStatus);
            dot.setFill(online ? Color.web("#2ecc71") : Color.web("#95a5a6"));
        }

        // Mettre à jour le header si ce contact est sélectionné
        if (currentContact != null && currentContact.username().equalsIgnoreCase(username)) {
            // Recréer un objet Contact local pour forcer la mise à jour UI sans dépendre du
            // stream
            currentContact = new Contact(currentContact.username(), currentContact.displayName(),
                    currentContact.initials(), currentContact.avatarStyle(), currentContact.role(),
                    currentContact.group(), newStatus);

            updateHeaderStatus(currentContact);
            updateRightPanelStatus(currentContact);
        }
    }

    /** Affiche le bon libellé et la bonne couleur de statut dans le header. */
    private void updateHeaderStatus(Contact contact) {
        boolean online = contact.isOnline();
        if (online) {
            headerStatus.setText("En ligne");
            headerStatus.getStyleClass().removeAll("pelo-meta");
            if (!headerStatus.getStyleClass().contains("pelo-label-green"))
                headerStatus.getStyleClass().add("pelo-label-green");
            if (headerStatusDot != null)
                headerStatusDot.setFill(Color.web("#2ecc71"));
            if (headerStatusInfoDot != null)
                headerStatusInfoDot.setFill(Color.web("#2ecc71"));
        } else {
            headerStatus.setText("Hors ligne");
            headerStatus.getStyleClass().removeAll("pelo-label-green");
            if (!headerStatus.getStyleClass().contains("pelo-meta"))
                headerStatus.getStyleClass().add("pelo-meta");
            if (headerStatusDot != null)
                headerStatusDot.setFill(Color.web("#95a5a6"));
            if (headerStatusInfoDot != null)
                headerStatusInfoDot.setFill(Color.web("#95a5a6"));
        }
    }

    /** Met à jour le statut dans le panneau de droite. */
    private void updateRightPanelStatus(Contact contact) {
        if (rpStatusLabel == null || rpStatusDot == null)
            return;

        boolean online = contact.isOnline();
        if (online) {
            rpStatusLabel.setText("• En ligne");
            rpStatusLabel.getStyleClass().removeAll("pelo-meta");
            if (!rpStatusLabel.getStyleClass().contains("pelo-label-green"))
                rpStatusLabel.getStyleClass().add("pelo-label-green");
            rpStatusDot.setFill(Color.web("#2ecc71"));
        } else {
            rpStatusLabel.setText("• Hors ligne");
            rpStatusLabel.getStyleClass().removeAll("pelo-label-green");
            if (!rpStatusLabel.getStyleClass().contains("pelo-meta"))
                rpStatusLabel.getStyleClass().add("pelo-meta");
            rpStatusDot.setFill(Color.web("#95a5a6"));
        }
    }

    /**
     * Réapplique la classe "active" sur l'item du contact courant après un
     * rechargement.
     */
    private void restoreActiveStyle() {
        if (currentContact != null) {
            activeItem = itemByPeer.get(currentContact.username());
            if (activeItem != null)
                activeItem.getStyleClass().add("active");
        }
    }

    // ═══════════════════════════════════════════════════════
    // SÉLECTEUR D'EMOJIS
    // ═══════════════════════════════════════════════════════

    @FXML
    private void onEmojiPicker() {
        messageField.requestFocus();
        // Tente le picker natif de l'OS (Ctrl+. sur GNOME / KDE)
        // Si Robot échoue (headless, env. non supporté…) → picker custom
        Platform.runLater(() -> {
            if (!tryNativeEmojiPicker()) {
                showCustomEmojiPicker();
            }
        });
    }

    /** Simule Ctrl+. pour ouvrir le picker émoji natif du bureau. */
    private boolean tryNativeEmojiPicker() {
        try {
            java.awt.Robot robot = new java.awt.Robot();
            robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
            robot.keyPress(java.awt.event.KeyEvent.VK_PERIOD);
            robot.keyRelease(java.awt.event.KeyEvent.VK_PERIOD);
            robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Affiche / cache le picker émoji intégré à l'application. */
    private void showCustomEmojiPicker() {
        if (emojiPickerPopup == null) {
            emojiPickerPopup = buildEmojiPopup();
        }
        if (emojiPickerPopup.isShowing()) {
            emojiPickerPopup.hide();
        } else {
            Bounds bounds = emojiButton.localToScreen(emojiButton.getBoundsInLocal());
            emojiPickerPopup.show(emojiButton, bounds.getMinX(), bounds.getMinY() - 330);
        }
    }

    private Popup buildEmojiPopup() {
        Popup popup = new Popup();
        popup.setAutoHide(true);

        FlowPane grid = new FlowPane();
        grid.setPrefWrapLength(310);
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setStyle(
                "-fx-background-color: #1e2a3a;" +
                        "-fx-padding: 10;" +
                        "-fx-background-radius: 10;");

        for (String emoji : EMOJIS) {
            Button btn = new Button(emoji);
            btn.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-font-size: 18px;" +
                            "-fx-cursor: hand;" +
                            "-fx-padding: 4;");
            btn.setOnMouseEntered(e -> btn.setStyle(
                    "-fx-background-color: #2a3a4a;" +
                            "-fx-font-size: 18px;" +
                            "-fx-cursor: hand;" +
                            "-fx-padding: 4;" +
                            "-fx-background-radius: 6;"));
            btn.setOnMouseExited(e -> btn.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-font-size: 18px;" +
                            "-fx-cursor: hand;" +
                            "-fx-padding: 4;"));
            btn.setOnAction(e -> {
                int caret = messageField.getCaretPosition();
                messageField.insertText(caret, emoji);
                popup.hide();
                messageField.requestFocus();
            });
            grid.getChildren().add(btn);
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setPrefSize(330, 300);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background-color: #1e2a3a;" +
                        "-fx-background: #1e2a3a;" +
                        "-fx-border-color: #2a3a4a;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;");

        popup.getContent().add(scroll);
        return popup;
    }

    // ═══════════════════════════════════════════════════════
    // DÉCONNEXION
    // ═══════════════════════════════════════════════════════

    @FXML
    private void onDisconnect() {
        socketService.disconnect();
        Platform.exit();
    }

    // ═══════════════════════════════════════════════════════
    // UTILITAIRES
    // ═══════════════════════════════════════════════════════

    private void recordMessage(String peer, ChatMessage msg) {
        history.computeIfAbsent(peer, k -> new ArrayList<>()).add(msg);
        saveConversation(peer);

        HBox item = itemByPeer.get(peer);
        if (item != null)
            updatePreview(item, msg.content(), msg.time());
    }

    /** Met à jour l'aperçu (dernière ligne + heure) dans l'item de liste. */
    private static void updatePreview(HBox item, String preview, String time) {
        if (item.getChildren().size() < 2)
            return;
        if (!(item.getChildren().get(1) instanceof VBox info))
            return;
        if (info.getChildren().size() < 2)
            return;

        if (info.getChildren().get(0) instanceof HBox nameRow
                && !nameRow.getChildren().isEmpty()) {
            Node last = nameRow.getChildren().get(nameRow.getChildren().size() - 1);
            if (last instanceof Label timeLabel)
                timeLabel.setText(time);
        }
        if (info.getChildren().get(1) instanceof Label previewLabel) {
            previewLabel.setText(preview);
        }
    }

    private static StackPane makeAvatar(String initials, String sizeStyle,
            String colorStyle, boolean onlineDot) {
        StackPane sp = new StackPane();
        sp.getStyleClass().addAll("pelo-avatar", sizeStyle, colorStyle);

        Text t = new Text(initials);
        t.setFill(Color.WHITE);
        sp.getChildren().add(t);

        if (onlineDot) {
            Circle dot = new Circle(5);
            dot.setFill(Color.web("#2ecc71"));
            dot.setStroke(Color.WHITE);
            dot.setStrokeWidth(1.5);
            StackPane.setAlignment(dot, Pos.BOTTOM_RIGHT);
            sp.getChildren().add(dot);
        }
        return sp;
    }

    private static String initials(String name) {
        String[] parts = name.split("[\\s._-]+");
        if (parts.length >= 2)
            return (String.valueOf(parts[0].charAt(0)) + parts[1].charAt(0)).toUpperCase();
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    private static String now() {
        return LocalTime.now().format(TIME_FMT);
    }
}
