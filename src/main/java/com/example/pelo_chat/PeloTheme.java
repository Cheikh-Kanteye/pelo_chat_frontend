package com.example.pelo_chat;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Objects;

/**
 * PELO Design System — Utilitaire JavaFX
 * <p>
 * Usage minimal :
 * <pre>
 *   PeloTheme.applyTo(scene);
 *   Button btn = PeloTheme.primaryButton("Envoyer");
 * </pre>
 *
 * @version 1.0
 */
public final class PeloTheme {

    private PeloTheme() {}

    // ═══════════════════════════════════════════════════════
    //  STYLESHEET
    // ═══════════════════════════════════════════════════════

    /** URL externe du stylesheet à passer à Scene.getStylesheets() */
    public static final String STYLESHEET = Objects.requireNonNull(
            PeloTheme.class.getResource("pelo-theme.css"),
            "pelo-theme.css introuvable dans le classpath"
    ).toExternalForm();

    /** Applique le thème PELO à une scène JavaFX. */
    public static void applyTo(Scene scene) {
        if (!scene.getStylesheets().contains(STYLESHEET)) {
            scene.getStylesheets().add(STYLESHEET);
        }
    }

    /** Applique le thème PELO à un nœud racine (Parent). */
    public static void applyTo(Parent parent) {
        if (!parent.getStylesheets().contains(STYLESHEET)) {
            parent.getStylesheets().add(STYLESHEET);
        }
    }

    // ═══════════════════════════════════════════════════════
    //  COLORS — Palette complète PELO
    // ═══════════════════════════════════════════════════════

    /**
     * Constantes de couleurs (hex strings + objets Color JavaFX).
     * Utiliser les hex strings pour les inline styles,
     * les Color objects pour les opérations programmatiques.
     */
    public static final class Colors {

        // — Brand colors
        public static final String GREEN        = "#00853F";
        public static final String GREEN_LIGHT  = "#00A84F";
        public static final String GREEN_DARK   = "#006B32";
        public static final String GOLD         = "#FDEF42";

        // — Neutrals
        public static final String NAVY         = "#2C3E50";
        public static final String NAVY_DEEP    = "#1A2535";
        public static final String NAVY_MID     = "#3D5166";
        public static final String GRAY_LIGHT   = "#ECF0F1";
        public static final String GRAY_MID     = "#BDC3C7";
        public static final String GRAY_DARK    = "#7F8C8D";
        public static final String WHITE        = "#FFFFFF";
        public static final String OFF_WHITE    = "#F7F9FA";

        // — Status
        public static final String ONLINE       = "#2ECC71";
        public static final String BUSY         = "#E74C3C";
        public static final String AWAY         = "#F39C12";

        // — Surfaces
        public static final String SURFACE_LIST = "#F0F3F5";
        public static final String SURFACE_MSG  = "#FAFBFC";
        public static final String DARK_BG      = "#0D1520";

        // — Borders
        public static final String BORDER       = "#DDE3E8";
        public static final String BORDER_LIGHT = "#EEF1F4";

        // — Objets Color JavaFX (pour animations, bindings, etc.)
        public static final Color FX_GREEN        = Color.web(GREEN);
        public static final Color FX_GREEN_LIGHT  = Color.web(GREEN_LIGHT);
        public static final Color FX_GOLD         = Color.web(GOLD);
        public static final Color FX_NAVY         = Color.web(NAVY);
        public static final Color FX_ONLINE       = Color.web(ONLINE);
        public static final Color FX_BUSY         = Color.web(BUSY);
        public static final Color FX_AWAY         = Color.web(AWAY);
        public static final Color FX_GRAY_LIGHT   = Color.web(GRAY_LIGHT);
        public static final Color FX_GRAY_DARK    = Color.web(GRAY_DARK);

        /** Convertit une hex string en Color JavaFX. */
        public static Color of(String hex) {
            return Color.web(hex);
        }

        private Colors() {}
    }

    // ═══════════════════════════════════════════════════════
    //  SPACING — Valeurs d'espacement
    // ═══════════════════════════════════════════════════════

    public static final class Spacing {
        public static final double XS  = 4;
        public static final double SM  = 8;
        public static final double MD  = 12;
        public static final double LG  = 16;
        public static final double XL  = 24;
        public static final double XXL = 32;

        /** Retourne un objet Insets uniforme. */
        public static Insets all(double value)              { return new Insets(value); }
        public static Insets of(double v, double h)         { return new Insets(v, h, v, h); }
        public static Insets of(double t, double r, double b, double l) {
            return new Insets(t, r, b, l);
        }

        private Spacing() {}
    }

    // ═══════════════════════════════════════════════════════
    //  RADIUS — Rayons de bordure
    // ═══════════════════════════════════════════════════════

    public static final class Radius {
        public static final double SM     = 8;
        public static final double MD     = 10;
        public static final double LG     = 14;
        public static final double BUBBLE = 18;
        public static final double CARD   = 24;
        public static final double PILL   = 20;

        private Radius() {}
    }

    // ═══════════════════════════════════════════════════════
    //  FONT SIZES
    // ═══════════════════════════════════════════════════════

    public static final class FontSize {
        public static final double MICRO  = 9;
        public static final double XS     = 10;
        public static final double SM     = 11;
        public static final double MD     = 12;
        public static final double LG     = 13;
        public static final double XL     = 14;
        public static final double XXL    = 16;
        public static final double TITLE  = 22;

        private FontSize() {}
    }

    // ═══════════════════════════════════════════════════════
    //  CSS CLASS NAMES — Constantes pour éviter les typos
    // ═══════════════════════════════════════════════════════

    public static final class Styles {

        // Buttons
        public static final String BTN_PRIMARY  = "pelo-btn-primary";
        public static final String BTN_GHOST    = "pelo-btn-ghost";
        public static final String BTN_ICON     = "pelo-btn-icon";
        public static final String SEND_BTN     = "pelo-send-btn";
        public static final String TOOLBAR_BTN  = "pelo-toolbar-btn";

        // Badges
        public static final String BADGE         = "pelo-badge";
        public static final String BADGE_GOLD    = "pelo-badge-gold";
        public static final String BADGE_DANGER  = "pelo-badge-danger";
        public static final String NOTIF_BADGE   = "pelo-notif-badge";

        // Status
        public static final String STATUS_PILL   = "pelo-status-pill";
        public static final String STATUS_ONLINE = "pelo-status-online";
        public static final String STATUS_BUSY   = "pelo-status-busy";
        public static final String STATUS_AWAY   = "pelo-status-away";

        // Avatars
        public static final String AVATAR         = "pelo-avatar";
        public static final String AVATAR_SM      = "pelo-avatar-sm";
        public static final String AVATAR_MD      = "pelo-avatar-md";
        public static final String AVATAR_LG      = "pelo-avatar-lg";
        public static final String AVATAR_GREEN   = "pelo-avatar-green";
        public static final String AVATAR_NAVY    = "pelo-avatar-navy";
        public static final String AVATAR_PURPLE  = "pelo-avatar-purple";
        public static final String AVATAR_RED     = "pelo-avatar-red";
        public static final String AVATAR_BLUE    = "pelo-avatar-blue";
        public static final String AVATAR_TEAL    = "pelo-avatar-teal";
        public static final String AVATAR_GOLD    = "pelo-avatar-gold";
        public static final String AVATAR_GROUP   = "pelo-avatar-group";

        // Message bubbles
        public static final String BUBBLE           = "pelo-bubble";
        public static final String BUBBLE_SENT      = "pelo-bubble-sent";
        public static final String BUBBLE_RECEIVED  = "pelo-bubble-received";
        public static final String TYPING_BUBBLE    = "pelo-typing-bubble";
        public static final String DATE_DIVIDER     = "pelo-date-divider";

        // Inputs
        public static final String SEARCH_BAR  = "pelo-search-bar";
        public static final String TEXT_INPUT   = "pelo-text-input";

        // Layout / colonnes
        public static final String SIDEBAR          = "pelo-sidebar";
        public static final String SIDEBAR_ITEM     = "pelo-sidebar-item";
        public static final String CHAT_LIST        = "pelo-chat-list";
        public static final String CHAT_LIST_HEADER = "pelo-chat-list-header";
        public static final String CHAT_ITEM        = "pelo-chat-item";
        public static final String CHAT_HEADER      = "pelo-chat-header";
        public static final String CHAT_INPUT_AREA  = "pelo-chat-input-area";
        public static final String RIGHT_PANEL      = "pelo-right-panel";
        public static final String RP_HEADER        = "pelo-rp-header";
        public static final String DEPT_TAG         = "pelo-dept-tag";
        public static final String SECTION_LABEL    = "pelo-section-label";

        // Typography
        public static final String TITLE_XL    = "pelo-title-xl";
        public static final String TITLE_LG    = "pelo-title-lg";
        public static final String TITLE_MD    = "pelo-title-md";
        public static final String TITLE_SM    = "pelo-title-sm";
        public static final String BODY        = "pelo-body";
        public static final String BODY_NAVY   = "pelo-body-navy";
        public static final String LABEL_CAPS  = "pelo-label-caps";
        public static final String LABEL_GOLD  = "pelo-label-gold";
        public static final String LABEL_GREEN = "pelo-label-green";
        public static final String META        = "pelo-meta";

        // Cards
        public static final String CARD        = "pelo-card";
        public static final String CARD_DARK   = "pelo-card-dark";

        // Misc
        public static final String DIVIDER       = "pelo-divider";
        public static final String DIVIDER_DARK  = "pelo-divider-dark";
        public static final String FILTER        = "pelo-filter";
        public static final String FILE_TILE     = "pelo-file-tile";
        public static final String FILE_ICON_PDF = "pelo-file-icon-pdf";
        public static final String FILE_ICON_DOC = "pelo-file-icon-doc";
        public static final String FILE_ICON_XLS = "pelo-file-icon-xls";

        // Pseudo-class utilitaire
        public static final String ACTIVE = "active";

        private Styles() {}
    }

    // ═══════════════════════════════════════════════════════
    //  COMPONENT FACTORIES
    // ═══════════════════════════════════════════════════════

    // ── Boutons ─────────────────────────────────────────────

    /** Bouton principal vert avec shadow. */
    public static Button primaryButton(String text) {
        Button b = new Button(text);
        b.getStyleClass().add(Styles.BTN_PRIMARY);
        return b;
    }

    /** Bouton contour vert (ghost). */
    public static Button ghostButton(String text) {
        Button b = new Button(text);
        b.getStyleClass().add(Styles.BTN_GHOST);
        return b;
    }

    /** Bouton icône carré arrondi transparent. */
    public static Button iconButton() {
        Button b = new Button();
        b.getStyleClass().add(Styles.BTN_ICON);
        return b;
    }

    /** Bouton d'envoi de message (40×40, vert). */
    public static Button sendButton() {
        Button b = new Button();
        b.getStyleClass().add(Styles.SEND_BTN);
        return b;
    }

    /** Bouton de barre d'outils de formatage. */
    public static Button toolbarButton() {
        Button b = new Button();
        b.getStyleClass().add(Styles.TOOLBAR_BTN);
        return b;
    }

    // ── Badges ──────────────────────────────────────────────

    /** Badge vert (non-lus). */
    public static Label badge(String count) {
        Label l = new Label(count);
        l.getStyleClass().add(Styles.BADGE);
        return l;
    }

    /** Badge or (notification sidebar). */
    public static Label badgeGold(String text) {
        Label l = new Label(text);
        l.getStyleClass().addAll(Styles.NOTIF_BADGE, Styles.BADGE_GOLD);
        return l;
    }

    /** Badge rouge (alerte). */
    public static Label badgeDanger(String text) {
        Label l = new Label(text);
        l.getStyleClass().addAll(Styles.BADGE, Styles.BADGE_DANGER);
        return l;
    }

    // ── Status ──────────────────────────────────────────────

    /** Pill "● Online" vert. */
    public static Label statusOnline() {
        Label l = new Label("● Online");
        l.getStyleClass().addAll(Styles.STATUS_PILL, Styles.STATUS_ONLINE);
        return l;
    }

    /** Pill "● Busy" rouge. */
    public static Label statusBusy() {
        Label l = new Label("● Busy");
        l.getStyleClass().addAll(Styles.STATUS_PILL, Styles.STATUS_BUSY);
        return l;
    }

    /** Pill "● Away" orange. */
    public static Label statusAway() {
        Label l = new Label("● Away");
        l.getStyleClass().addAll(Styles.STATUS_PILL, Styles.STATUS_AWAY);
        return l;
    }

    // ── Avatars ─────────────────────────────────────────────

    /**
     * Avatar circulaire avec initiales.
     *
     * @param initials  ex. "AB", "NF"
     * @param sizeStyle {@link Styles#AVATAR_SM}, {@link Styles#AVATAR_MD}, {@link Styles#AVATAR_LG}
     * @param colorStyle {@link Styles#AVATAR_GREEN}, {@link Styles#AVATAR_NAVY}, etc.
     */
    public static Label avatar(String initials, String sizeStyle, String colorStyle) {
        Label l = new Label(initials);
        l.getStyleClass().addAll(Styles.AVATAR, sizeStyle, colorStyle);
        return l;
    }

    /** Avatar groupe (carré arrondi 12px). */
    public static Label avatarGroup(String initials, String colorStyle) {
        Label l = new Label(initials);
        l.getStyleClass().addAll(Styles.AVATAR, Styles.AVATAR_MD, colorStyle, Styles.AVATAR_GROUP);
        return l;
    }

    // ── Message Bubbles ─────────────────────────────────────

    /** Bulle de message envoyé (vert). */
    public static Label bubbleSent(String text) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.getStyleClass().addAll(Styles.BUBBLE, Styles.BUBBLE_SENT);
        return l;
    }

    /** Bulle de message reçu (gris). */
    public static Label bubbleReceived(String text) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.getStyleClass().addAll(Styles.BUBBLE, Styles.BUBBLE_RECEIVED);
        return l;
    }

    /** Séparateur de date dans le chat (ex. "Today, February 17"). */
    public static Label dateDivider(String dateText) {
        Label l = new Label(dateText);
        l.getStyleClass().add(Styles.DATE_DIVIDER);
        return l;
    }

    // ── Inputs ──────────────────────────────────────────────

    /** Barre de recherche. */
    public static TextField searchBar(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add(Styles.SEARCH_BAR);
        return tf;
    }

    /** Zone de saisie de message. */
    public static TextArea messageInput(String prompt) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setPrefRowCount(1);
        ta.setWrapText(true);
        ta.getStyleClass().add(Styles.TEXT_INPUT);
        return ta;
    }

    // ── Typography ──────────────────────────────────────────

    public static Label titleXl(String text) {
        Label l = new Label(text);
        l.getStyleClass().add(Styles.TITLE_XL);
        return l;
    }

    public static Label titleLg(String text) {
        Label l = new Label(text);
        l.getStyleClass().add(Styles.TITLE_LG);
        return l;
    }

    public static Label titleMd(String text) {
        Label l = new Label(text);
        l.getStyleClass().add(Styles.TITLE_MD);
        return l;
    }

    public static Label titleSm(String text) {
        Label l = new Label(text);
        l.getStyleClass().add(Styles.TITLE_SM);
        return l;
    }

    public static Label body(String text) {
        Label l = new Label(text);
        l.getStyleClass().add(Styles.BODY);
        return l;
    }

    public static Label sectionLabel(String text) {
        Label l = new Label(text.toUpperCase());
        l.getStyleClass().add(Styles.LABEL_CAPS);
        return l;
    }

    public static Label labelGold(String text) {
        Label l = new Label(text.toUpperCase());
        l.getStyleClass().add(Styles.LABEL_GOLD);
        return l;
    }

    public static Label meta(String text) {
        Label l = new Label(text);
        l.getStyleClass().add(Styles.META);
        return l;
    }

    // ── Layout Helpers ──────────────────────────────────────

    /** Ligne de séparation horizontale (1px). */
    public static Region divider() {
        Region r = new Region();
        r.getStyleClass().add(Styles.DIVIDER);
        r.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    /** Ligne de séparation foncée (pour dark backgrounds). */
    public static Region dividerDark() {
        Region r = new Region();
        r.getStyleClass().add(Styles.DIVIDER_DARK);
        r.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    /** Spacer flexible (s'étend pour remplir l'espace disponible). */
    public static Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        VBox.setVgrow(r, Priority.ALWAYS);
        return r;
    }

    // ── Filters ─────────────────────────────────────────────

    /** Bouton filtre/onglet. */
    public static Button filterButton(String text, boolean active) {
        Button b = new Button(text);
        b.getStyleClass().add(Styles.FILTER);
        if (active) b.getStyleClass().add(Styles.ACTIVE);
        return b;
    }

    // ── File Tiles ──────────────────────────────────────────

    /** Tuile de fichier PDF attaché. */
    public static HBox fileTile(String name, String size, String iconStyle) {
        Label icon = new Label("📄");
        icon.getStyleClass().add(iconStyle);

        Label fileName = new Label(name);
        fileName.getStyleClass().add(Styles.TITLE_SM);
        Label fileMeta = meta(size);

        VBox info = new VBox(2, fileName, fileMeta);

        HBox tile = new HBox(10, icon, info);
        tile.getStyleClass().add(Styles.FILE_TILE);
        tile.setPadding(Spacing.all(8));
        return tile;
    }
}
