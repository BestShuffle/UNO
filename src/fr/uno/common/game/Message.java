package fr.uno.common.game;

import java.io.Serializable;

/**
 * Classe d'objet Message
 */
public class Message implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1162448733713904568L;
    private MessageType messageType;
    private Object sender;
    private Object content;

    /**
     * Construit un objet Message
     *
     * @param messageType Type de message
     * @param sender      Nom de l'émetteur
     * @param content     Contenu
     */
    public Message(MessageType messageType, Object sender, Object content) {
        super();
        this.messageType = messageType;
        this.sender = sender;
        this.content = content;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Object getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return sender + " - " + content.toString();
    }
}
