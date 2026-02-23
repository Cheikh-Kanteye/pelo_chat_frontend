package com.example.pelo_chat.controller;

import com.example.pelo_chat.model.User;
import com.example.pelo_chat.service.SocketService;
import com.example.pelo_chat.utils.Packet;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatController {

    // ── FXML injections ───────────────────────────────────
    @FXML private VBox      chatListContainer;
    @FXML private VBox      messageContainer;
    @FXML private TextField messageField;
    @FXML private TextField searchField;

    @FXML private Label     headerName;
    @FXML private Label     headerStatus;
    @FXML private Label     headerRole;

    @FXML private Text      rpInitials;
    @FXML private Label     rpName;
    @FXML private Label     rpRole;

    // ── État ─────────────────────────────────────────────
    private String        currentUser;
    private SocketService socketService;

    private Contact       currentContact;
    private HBox          activeItem;

    private final List<Contact>                  contacts   = new ArrayList<>();
    private final Map<String, List<ChatMessage>> history    = new HashMap<>();
    private final Map<String, HBox>              itemByPeer = new HashMap<>();

    // ── Constantes ───────────────────────────────────────
    private static final String[] AVATAR_STYLES = {
            "pelo-avatar-navy", "pelo-avatar-teal", "pelo-avatar-green",
            "pelo-avatar-purple", "pelo-avatar-red", "pelo-avatar-gold"
    };

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm");

    // ═══════════════════════════════════════════════════════
    // MODÈLES INTERNES
    // ═══════════════════════════════════════════════════════

    private record Contact(
            String username,
            String displayName,
            String initials,
            String avatarStyle,
            String role,
            boolean group
    ) {}

    private record ChatMessage(
            String from,
            String content,
            boolean mine,
            String time
    ) {}

    // ═══════════════════════════════════════════════════════
    // INITIALISATION
    // ═══════════════════════════════════════════════════════

    public void init(String username, SocketService service) {
        this.currentUser   = username;
        this.socketService = service;

        socketService.setOnPacketReceived(this::handlePacket);

        // Afficher un état de chargement, puis demander la liste au serveur
        showLoadingState();
        socketService.requestUsers(currentUser);
    }

    private void showLoadingState() {
        chatListContainer.getChildren().clear();
        Label loading = new Label("Chargement des contacts…");
        loading.getStyleClass().add("pelo-meta");
        loading.setStyle("-fx-padding: 16;");
        chatListContainer.getChildren().add(loading);
    }

    // ═══════════════════════════════════════════════════════
    // LISTE DES CONVERSATIONS
    // ═══════════════════════════════════════════════════════

    private void renderContactList() {
        chatListContainer.getChildren().clear();
        itemByPeer.clear();

        for (Contact c : contacts) {
            List<ChatMessage> msgs    = history.getOrDefault(c.username(), List.of());
            String            preview = msgs.isEmpty() ? "" : msgs.get(msgs.size() - 1).content();
            String            time    = msgs.isEmpty() ? "" : msgs.get(msgs.size() - 1).time();

            HBox item = buildChatItem(c, preview, time);
            itemByPeer.put(c.username(), item);
            chatListContainer.getChildren().add(item);
        }
    }

    private HBox buildChatItem(Contact c, String preview, String time) {
        StackPane avatar = makeAvatar(c.initials(), "pelo-avatar-md", c.avatarStyle(), !c.group());

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
        if (activeItem != null) activeItem.getStyleClass().remove("active");

        item.getStyleClass().add("active");
        activeItem     = item;
        currentContact = contact;

        // Header
        headerName.setText(contact.displayName());
        headerRole.setText("• " + contact.role());
        headerStatus.setText("Online");

        // Panneau de détails
        rpInitials.setText(contact.initials());
        rpName.setText(contact.displayName());
        rpRole.setText(contact.role());

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
        if (currentContact == null) return;

        String content = messageField.getText().trim();
        if (content.isEmpty()) return;

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
        if (packet == null || packet.getAction() == null) return;

        switch (packet.getAction()) {

            case "USERS_LIST" -> Platform.runLater(() -> {
                User[] users = new Gson().fromJson(packet.getContent(), User[].class);
                populateContacts(users);
            });

            case "MESSAGE_RECEIVED" -> Platform.runLater(() -> {
                String from    = packet.getFrom();
                String content = packet.getContent();

                // Ajouter l'expéditeur si inconnu
                if (contacts.stream().noneMatch(c -> c.username().equals(from))) {
                    contacts.add(new Contact(
                            from, from, initials(from),
                            AVATAR_STYLES[contacts.size() % AVATAR_STYLES.length],
                            "", false));
                    renderContactList();
                    restoreActiveStyle();
                }

                ChatMessage msg = new ChatMessage(from, content, false, now());
                recordMessage(from, msg);

                if (currentContact != null && from.equals(currentContact.username())) {
                    messageContainer.getChildren().add(buildReceivedBubble(msg));
                }
            });

            case "ERROR" -> Platform.runLater(() ->
                    System.err.println("Erreur serveur : " + packet.getContent()));
        }
    }

    /** Construit la liste de contacts à partir de la réponse serveur. */
    private void populateContacts(User[] users) {
        contacts.clear();
        for (int i = 0; i < users.length; i++) {
            User u = users[i];
            contacts.add(new Contact(
                    u.getUsername(),
                    u.getUsername(),
                    initials(u.getUsername()),
                    AVATAR_STYLES[i % AVATAR_STYLES.length],
                    u.getStatus() != null ? u.getStatus() : "OFFLINE",
                    false));
        }
        renderContactList();
        if (!contacts.isEmpty()) {
            selectConversation(contacts.get(0), itemByPeer.get(contacts.get(0).username()));
        }
    }

    /** Réapplique la classe "active" sur l'item du contact courant après un rechargement. */
    private void restoreActiveStyle() {
        if (currentContact != null) {
            activeItem = itemByPeer.get(currentContact.username());
            if (activeItem != null) activeItem.getStyleClass().add("active");
        }
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

        HBox item = itemByPeer.get(peer);
        if (item != null) updatePreview(item, msg.content(), msg.time());
    }

    /** Met à jour l'aperçu (dernière ligne + heure) dans l'item de liste. */
    private static void updatePreview(HBox item, String preview, String time) {
        if (item.getChildren().size() < 2) return;
        if (!(item.getChildren().get(1) instanceof VBox info)) return;
        if (info.getChildren().size() < 2) return;

        if (info.getChildren().get(0) instanceof HBox nameRow
                && !nameRow.getChildren().isEmpty()) {
            Node last = nameRow.getChildren().get(nameRow.getChildren().size() - 1);
            if (last instanceof Label timeLabel) timeLabel.setText(time);
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
