package com.example.pelo_chat;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * PELO — Design System Showcase
 * Démontre les composants du thème PELO en JavaFX.
 */
public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        AuthScreen auth = new AuthScreen(stage);

        stage.setTitle("PELO — Connexion");
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        stage.setScene(auth.build());
        stage.show();
    }

    public static VBox buildShowcase() {
        VBox root = new VBox(28);
        root.setPadding(new Insets(36));
        root.setStyle("-fx-background-color: " + PeloTheme.Colors.SURFACE_LIST + ";");

        // ── En-tête ──────────────────────────────────────────
        Label wordmark = new Label("P E L O");
        wordmark.setStyle(
            "-fx-font-family: 'Montserrat'; -fx-font-size: 28px; -fx-font-weight: 900;" +
            "-fx-text-fill: " + PeloTheme.Colors.NAVY + ";"
        );
        Label subtitle = PeloTheme.labelGold("Design System v1.0 · JavaFX");
        Label desc = PeloTheme.body("Xam Xam ak Teggin · Connect with Purpose");
        VBox header = new VBox(6, wordmark, subtitle, desc);

        // ── Boutons ──────────────────────────────────────────
        Label btnSection = PeloTheme.sectionLabel("Buttons");
        HBox buttons = new HBox(12,
            PeloTheme.primaryButton("Send Message"),
            PeloTheme.ghostButton("Add Contact"),
            PeloTheme.sendButton()
        );
        buttons.setAlignment(Pos.CENTER_LEFT);

        // ── Badges ───────────────────────────────────────────
        Label badgeSection = PeloTheme.sectionLabel("Badges");
        HBox badges = new HBox(10,
            PeloTheme.badge("4"),
            PeloTheme.badge("12"),
            PeloTheme.badgeGold("7"),
            PeloTheme.badgeDanger("!")
        );
        badges.setAlignment(Pos.CENTER_LEFT);

        // ── Status ───────────────────────────────────────────
        Label statusSection = PeloTheme.sectionLabel("Status Indicators");
        HBox statuses = new HBox(10,
            PeloTheme.statusOnline(),
            PeloTheme.statusBusy(),
            PeloTheme.statusAway()
        );
        statuses.setAlignment(Pos.CENTER_LEFT);

        // ── Avatars ──────────────────────────────────────────
        Label avatarSection = PeloTheme.sectionLabel("Avatars");
        HBox avatars = new HBox(10,
            PeloTheme.avatar("AB", PeloTheme.Styles.AVATAR_SM,  PeloTheme.Styles.AVATAR_NAVY),
            PeloTheme.avatar("NF", PeloTheme.Styles.AVATAR_MD,  PeloTheme.Styles.AVATAR_GREEN),
            PeloTheme.avatar("KS", PeloTheme.Styles.AVATAR_MD,  PeloTheme.Styles.AVATAR_PURPLE),
            PeloTheme.avatar("MM", PeloTheme.Styles.AVATAR_MD,  PeloTheme.Styles.AVATAR_GOLD),
            PeloTheme.avatar("OD", PeloTheme.Styles.AVATAR_MD,  PeloTheme.Styles.AVATAR_RED),
            PeloTheme.avatar("DK", PeloTheme.Styles.AVATAR_LG,  PeloTheme.Styles.AVATAR_GREEN),
            PeloTheme.avatarGroup("EQ", PeloTheme.Styles.AVATAR_TEAL)
        );
        avatars.setAlignment(Pos.CENTER_LEFT);

        // ── Message Bubbles ──────────────────────────────────
        Label bubbleSection = PeloTheme.sectionLabel("Message Bubbles");

        Label recv1 = PeloTheme.bubbleReceived("Bonjour ! Tu as revu le rapport trimestriel ?");
        recv1.setMaxWidth(420);
        HBox recvRow = new HBox(recv1);
        recvRow.setAlignment(Pos.CENTER_LEFT);

        Label sent1 = PeloTheme.bubbleSent("Oui, les chiffres du Q3 sont excellents !");
        sent1.setMaxWidth(420);
        HBox sentRow = new HBox(sent1);
        sentRow.setAlignment(Pos.CENTER_RIGHT);

        Label recv2 = PeloTheme.bubbleReceived("Parfait. Réunion à 14h — tu es dispo ?");
        recv2.setMaxWidth(420);
        HBox recvRow2 = new HBox(recv2);
        recvRow2.setAlignment(Pos.CENTER_LEFT);

        Label sent2 = PeloTheme.bubbleSent("Bien sûr, je prépare une synthèse de 5 minutes 👍");
        sent2.setMaxWidth(420);
        HBox sentRow2 = new HBox(sent2);
        sentRow2.setAlignment(Pos.CENTER_RIGHT);

        VBox bubbleBox = new VBox(8, recvRow, sentRow, recvRow2, sentRow2);

        // ── Inputs ───────────────────────────────────────────
        Label inputSection = PeloTheme.sectionLabel("Inputs");
        VBox inputs = new VBox(10,
            PeloTheme.searchBar("Rechercher des conversations…"),
            PeloTheme.messageInput("Écrire un message à Amadou Ba…")
        );

        // ── Filters ──────────────────────────────────────────
        Label filterSection = PeloTheme.sectionLabel("Filters / Tabs");
        HBox filters = new HBox(8,
            PeloTheme.filterButton("All", true),
            PeloTheme.filterButton("Direct", false),
            PeloTheme.filterButton("Groups", false)
        );
        filters.setAlignment(Pos.CENTER_LEFT);

        // ── Typography ───────────────────────────────────────
        Label typoSection = PeloTheme.sectionLabel("Typography");
        VBox typo = new VBox(6,
            PeloTheme.titleXl("Title XL — Montserrat 22px"),
            PeloTheme.titleLg("Title LG — Montserrat 16px Bold"),
            PeloTheme.titleMd("Title MD — Montserrat 14px Bold"),
            PeloTheme.titleSm("Title SM — Montserrat 12.5px Bold"),
            PeloTheme.body("Body — Nunito 12px · Pour les messages et descriptions"),
            PeloTheme.sectionLabel("Section label · caps 9px"),
            PeloTheme.labelGold("Label Gold · Montserrat 10px"),
            PeloTheme.meta("Meta · 9.5px · timestamps, statuts secondaires")
        );

        // ── File Tiles ───────────────────────────────────────
        Label fileSection = PeloTheme.sectionLabel("File Attachments");
        FlowPane files = new FlowPane(10, 10,
            PeloTheme.fileTile("Rapport_Q3_2025.pdf",     "2.4 MB · Today", PeloTheme.Styles.FILE_ICON_PDF),
            PeloTheme.fileTile("Budget_2026.xlsx",          "890 KB · Mon.",  PeloTheme.Styles.FILE_ICON_XLS),
            PeloTheme.fileTile("Stratégie_Expansion.docx", "1.1 MB · Fri.",  PeloTheme.Styles.FILE_ICON_DOC)
        );

        // ── Assemblage final ─────────────────────────────────
        root.getChildren().addAll(
            header,
            PeloTheme.divider(),
            btnSection,    buttons,
            PeloTheme.divider(),
            badgeSection,  badges,
            PeloTheme.divider(),
            statusSection, statuses,
            PeloTheme.divider(),
            avatarSection, avatars,
            PeloTheme.divider(),
            bubbleSection, bubbleBox,
            PeloTheme.divider(),
            inputSection,  inputs,
            PeloTheme.divider(),
            filterSection, filters,
            PeloTheme.divider(),
            typoSection,   typo,
            PeloTheme.divider(),
            fileSection,   files
        );

        return root;
    }
}
