package blockchain;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class MessageGenerator implements Runnable {
    private Blockchain blockchain = Blockchain.getInstance();  // Reference to the singleton Blockchain instance
    private static final AtomicLong MESSAGE_ID = new AtomicLong();  // Atomic counter to ensure unique message IDs

    // List of possible client names
    private final List<String> clientNames = List.of("Chill guy", "Regina Phalange", "Elen", "Milena", "Michael Scarn");

    // List of predefined chat messages
    private final List<String> messages = List.of(
            "Hello everyone!", "How's it going?", "What's up??", "Can anyone see this?", "I love this chat!",
            "What are you mining?", "The fastest miner wins:D", "Let's gooo!", "Can't believe it's still Tuesday:'(",
            "Anyone up for a chat;)", "I think my computer's overheating... 🔥", "Who's winning the race? 🏁",
            "Is this thing on? Testing 1, 2, 3...", "When does the next block get mined? ⏳", "Just waiting for the next block... 😴",
            "Who needs a snack break during mining? 🍕", "Mining's harder than I thought! 😅",
            "I swear I'm faster than my internet connection!", "Blockchain or bust! 🚀", "Wanna join my mining pool? 🤝",
            "I think I found the magic number! 💎", "Hashing away at it... ⏳", "What are we mining for, anyway? 🧐",
            "It's a beautiful day to mine some blocks! 🌞", "I'm not saying I'm the best miner, but... 😏"
    );

    @Override
    public void run() {
        Random random = new Random();  // Random object to select random clients and messages
        while (blockchain.isAcceptingMessages()) {  // Continue generating messages as long as the blockchain is accepting messages
            try {
                Thread.sleep(100);  // Wait for 100ms before generating a new message (simulates message arrival time)

                // Create a new client by randomly selecting from the client names list
                Client client = new Client(clientNames.get(random.nextInt(clientNames.size())));

                // Select a random message from the predefined list
                String msg = messages.get(random.nextInt(messages.size()));

                // Generate a unique message ID by incrementing the atomic counter
                long id = MESSAGE_ID.incrementAndGet();

                // Sign the message using the client's private key
                byte[] signature = client.sign(id, msg);

                // Create a new Message object with the message ID, client information, and the signed message
                Message message = new Message(id, client.getName(), msg, client.getPublicKey(), signature);

                // Add the new message to the pending messages list in the blockchain
                blockchain.addPendingMessage(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();  // Good practice to re-interrupt the thread
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
