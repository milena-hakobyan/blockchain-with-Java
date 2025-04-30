package blockchain;

import java.security.PublicKey;
import java.security.Signature;

public class Message {
    private final String sender;
    private final String text;

    public Message(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }

}