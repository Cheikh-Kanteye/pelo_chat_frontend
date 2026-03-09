package com.example.pelo_chat.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Unité d'échange entre le client et le serveur via TCP.
 * Sérialisée en JSON (GSON) sur une ligne, une ligne = un paquet.
 *
 * Actions principales :
 *   LOGIN          → from=username, content=password
 *   REGISTER       → from=username, to=fullName, content=password
 *   ACK            → réponse succès du serveur (LOGIN ou autre)
 *   REGISTER_OK    → inscription acceptée
 *   GET_USERS      → from=username (demande la liste des autres users)
 *   USERS_LIST     → content=JSON tableau User[]
 *   SEND_MESSAGE   → from=moi, to=destinataire, content=texte
 *   MESSAGE_RECEIVED → from=expéditeur, content=texte
 *   STATUS_UPDATE  → content={"username":"x","status":"ONLINE|OFFLINE"}
 *   LOGOUT         → déconnexion propre
 *   ERROR          → content=message d'erreur lisible
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Packet {

    /** Type de l'opération (voir liste ci-dessus). */
    private String action;

    /** Expéditeur du paquet (username du client). */
    private String from;

    /** Destinataire : username pour SEND_MESSAGE, fullName pour REGISTER. */
    private String to;

    /** Charge utile : mot de passe, texte du message, JSON, ou message d'erreur. */
    private String content;


}
