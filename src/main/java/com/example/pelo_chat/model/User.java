package com.example.pelo_chat.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représentation côté client d'un utilisateur.
 * Correspond au DTO envoyé par le serveur via GET_USERS :
 * {"username":"alice","status":"ONLINE"}
 * Le mot de passe n'est jamais transmis au client.
 */
@Data
@NoArgsConstructor
public class User {
    private String username;
    private String status;   // "ONLINE" | "OFFLINE"
}
