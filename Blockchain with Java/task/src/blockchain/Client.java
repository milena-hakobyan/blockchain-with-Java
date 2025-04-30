package blockchain;

import java.security.*;


/**
 *  Each Client generates a key pair (private and public).
 *  The private key is used to sign messages, and the public key is attached to the message for verification.
 *  This prevents unauthorized users from sending messages on behalf of others and ensures that messages are indeed from the sender.
 */
public class Client {
    private final String name;
    private final KeyPair keyPair;  // The RSA key pair for the client (private and public keys)

    public Client(String name) {
        this.name = name;
        this.keyPair = generateKeyPair();  // Generate a new RSA key pair for the client
    }

    // Getter method to retrieve the client's name
    public String getName() {
        return name;
    }

    // Getter method to retrieve the client's public key (to be used for verifying signatures)
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    /**
     * Signs a message with the client's private key.
     * This is used for message authentication and verification by others (miners or blockchain).
     *
     * @param id The ID of the message (usually unique)
     * @param message The actual content of the message
     * @return The generated signature (digital signature)
     * @throws Exception if there is any error in the signing process
     */
    public byte[] sign(long id, String message) throws Exception {
        // Initialize a signature object using the SHA256withRSA algorithm
        Signature signature = Signature.getInstance("SHA256withRSA");

        // Initialize the signature with the client's private key for signing
        signature.initSign(keyPair.getPrivate());

        // Update the signature with the message content (the message ID and message text)
        signature.update((id + message).getBytes());  // The input string is the combination of the message ID and the message text

        // Generate and return the digital signature
        return signature.sign();
    }

    /**
     * Generates a new RSA key pair (private and public keys) for the client.
     * The key pair is used for signing messages and verifying their authenticity.
     *
     * @return A new KeyPair containing the private and public keys
     */
    private KeyPair generateKeyPair() {
        try {
            // Create a KeyPairGenerator for RSA algorithm
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

            // Initialize the key pair generator with a key size of 2048 bits (strong security)
            generator.initialize(2048);

            // Generate and return the key pair (private and public keys)
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            // If the RSA algorithm is not available, throw a runtime exception
            throw new RuntimeException("Error generating RSA key pair", e);
        }
    }
}
