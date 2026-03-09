package com.example.pelo_chat.service;

import com.example.pelo_chat.utils.Packet;
import com.google.gson.Gson;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Couche réseau de PELO Chat.
 *
 * Gère la connexion TCP au serveur Java (localhost:8080).
 * Le protocole est simple : chaque paquet = une ligne JSON (terminée par \n).
 * Un thread d'écoute tourne en arrière-plan (daemon) et appelle le callback
 * à chaque paquet reçu. Le callback est toujours exécuté sur le thread JavaFX
 * (Platform.runLater) pour pouvoir modifier l'UI directement.
 */
public class SocketService {

    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private final Gson gson = new Gson();

    // Callback appelé à chaque paquet reçu (mis à jour par le Controller)
    private Consumer<Packet> onPacketReceived;
    private boolean running = false;

    /**
     * Ouvre la connexion TCP et démarre le thread d'écoute.
     * Si une connexion existante est ouverte, elle est d'abord fermée.
     *
     * @param callback fonction appelée à chaque paquet reçu (sur le thread JavaFX)
     */
    public void connect(Consumer<Packet> callback) throws IOException {
        running = false; // Stoppe l'éventuel listener existant
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        this.onPacketReceived = callback;
        socket = new Socket(HOST, PORT);
        // true = auto-flush : chaque println() envoie immédiatement sur le réseau
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        running = true;
        Thread listener = new Thread(() -> {
            try {
                String line;
                // Boucle bloquante : readLine() attend la prochaine ligne JSON du serveur
                while (running && (line = reader.readLine()) != null) {
                    Packet packet = gson.fromJson(line, Packet.class);
                    if (onPacketReceived != null) {
                        // Toujours repasser sur le thread JavaFX avant de toucher l'UI
                        Platform.runLater(() -> onPacketReceived.accept(packet));
                    }
                }
            } catch (IOException e) {
                // Connexion perdue (serveur arrêté, réseau coupé, etc.)
                if (running && onPacketReceived != null) {
                    Platform.runLater(() -> onPacketReceived.accept(
                            new Packet("ERROR", "server", null, "Connexion perdue")));
                }
            }
        });
        // Daemon : ce thread ne bloque pas l'arrêt de la JVM quand l'appli se ferme
        listener.setDaemon(true);
        listener.start();
    }

    /** Envoie un paquet LOGIN au serveur. */
    public void login(String username, String password) {
        send(new Packet("LOGIN", username, null, password));
    }

    /** Envoie un paquet REGISTER au serveur. */
    public void register(String username, String fullName, String password) {
        send(new Packet("REGISTER", username, fullName, password));
    }

    /** Demande au serveur la liste de tous les utilisateurs (hors soi-même). */
    public void requestUsers(String username) {
        send(new Packet("GET_USERS", username, null, null));
    }

    /** Sérialise le paquet en JSON et l'envoie au serveur sur une ligne. */
    public void send(Packet packet) {
        if (writer != null) {
            writer.println(gson.toJson(packet));
        }
    }

    /**
     * Remplace le callback d'écoute (utilisé par ChatController après la navigation
     * depuis AuthController, pour récupérer les paquets sur la nouvelle scène).
     */
    public void setOnPacketReceived(Consumer<Packet> callback) {
        this.onPacketReceived = callback;
    }

    /** Prévient le serveur de la déconnexion, puis ferme le socket. */
    public void disconnect() {
        send(new Packet("LOGOUT", null, null, null));
        try {
            if (socket != null)
                socket.close();
        } catch (IOException ignored) {
        }
    }
}
