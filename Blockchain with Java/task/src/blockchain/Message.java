package blockchain;

import java.security.PublicKey;
import java.security.Signature;

public class Message {
    // Unique identifier for the message
    private final long id;
    // Sender's name or identifier
    private final String sender;
    // The actual content of the message
    private final String text;
    // Public key of the sender used for signature verification
    private final PublicKey publicKey;
    // Digital signature of the message (signed using the sender's private key)
    private final byte[] signature;

    // Constructor to initialize the message with its id, sender, text, public key, and signature
    public Message(long id, String sender, String text, PublicKey publicKey, byte[] signature) {
        this.id = id;
        this.sender = sender;
        this.text = text;
        this.publicKey = publicKey;
        this.signature = signature;
    }

    // Method to validate the message's signature
    public boolean isValid() {
        try {
            // Create a Signature instance using the SHA256withRSA algorithm for signature verification
            Signature sig = Signature.getInstance("SHA256withRSA");

            // Initialize the signature object with the public key of the sender
            sig.initVerify(publicKey);

            // Update the signature object with the message content to be verified (id + text)
            sig.update((id + text).getBytes());

            return sig.verify(signature);
        } catch (Exception e) {
            // Return false if there is any exception during verification (invalid signature)
            return false;
        }
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getSender() {
        return sender;
    }

    public byte[] getSignature() {
        return signature;
    }
}
