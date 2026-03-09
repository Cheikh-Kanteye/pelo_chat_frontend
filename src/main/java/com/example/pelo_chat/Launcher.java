package com.example.pelo_chat;

import javafx.application.Application;

/**
 * Point d'entrée de l'application PELO Chat.
 *
 * Cette classe existe uniquement pour séparer le main() de la classe Application.
 * Cela évite les erreurs de chargement de modules JavaFX sur certains JDK
 * (le JDK refuse de lancer directement une sous-classe d'Application depuis un fat-jar).
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(HelloApplication.class, args);
    }
}
