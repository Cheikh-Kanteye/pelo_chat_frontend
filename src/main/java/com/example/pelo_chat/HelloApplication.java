package com.example.pelo_chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/pelo_chat/auth.fxml"));
        Parent root = loader.load();

        stage.setTitle("PELO — Connexion");
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
