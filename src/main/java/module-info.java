module com.example.pelo_chat {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires static lombok;
    requires com.google.gson;
    requires java.desktop;


    opens com.example.pelo_chat to javafx.fxml;
    opens com.example.pelo_chat.controller to javafx.fxml, com.google.gson;
    opens com.example.pelo_chat.utils to com.google.gson;
    opens com.example.pelo_chat.model to com.google.gson;
    opens com.example.pelo_chat.service to javafx.fxml;

    exports com.example.pelo_chat;
    exports com.example.pelo_chat.controller;
    exports com.example.pelo_chat.service;    // ← AJOUTER
    exports com.example.pelo_chat.utils;      // ← AJOUTER
    exports com.example.pelo_chat.model;      // ← AJOUTER
}