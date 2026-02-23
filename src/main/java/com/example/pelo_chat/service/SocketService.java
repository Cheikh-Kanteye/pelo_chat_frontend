package com.example.pelo_chat.service;

import com.example.pelo_chat.utils.Packet;
import com.google.gson.Gson;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

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

    public void connect(Consumer<Packet> callback) throws IOException {
        running = false; // Stop existing listener if any
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        this.onPacketReceived = callback;
        socket = new Socket(HOST, PORT);
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        running = true;
        Thread listener = new Thread(() -> {
            try {
                String line;
                while (running && (line = reader.readLine()) != null) {
                    Packet packet = gson.fromJson(line, Packet.class);
                    if (onPacketReceived != null) {
                        Platform.runLater(() -> onPacketReceived.accept(packet));
                    }
                }
            } catch (IOException e) {
                if (running && onPacketReceived != null) {
                    Platform.runLater(() -> onPacketReceived.accept(
                            new Packet("ERROR", "server", null, "Connexion perdue")));
                }
            }
        });
        listener.setDaemon(true);
        listener.start();
    }

    public void login(String username, String password) {
        send(new Packet("LOGIN", username, null, password));
    }

    public void register(String username, String password) {
        send(new Packet("REGISTER", username, null, password));
    }

    /** Demande au serveur la liste de tous les utilisateurs (hors soi-même). */
    public void requestUsers(String username) {
        send(new Packet("GET_USERS", username, null, null));
    }

    public void send(Packet packet) {
        if (writer != null) {
            writer.println(gson.toJson(packet));
        }
    }

    public void setOnPacketReceived(Consumer<Packet> callback) {
        this.onPacketReceived = callback;
    }

    public void disconnect() {
        send(new Packet("LOGOUT", null, null, null));
        try {
            if (socket != null)
                socket.close();
        } catch (IOException ignored) {
        }
    }
}
