module com.example.pelo_chat {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.pelo_chat to javafx.fxml;
    exports com.example.pelo_chat;
    exports com.example.pelo_chat.view;
    opens com.example.pelo_chat.view to javafx.fxml;
}