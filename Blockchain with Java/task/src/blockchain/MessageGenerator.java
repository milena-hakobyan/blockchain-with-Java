package blockchain;

import java.util.List;
import java.util.Random;

// Runnable task that generates random messages from clients and submits them to the blockchain
public class MessageGenerator implements Runnable {
    // Reference to the singleton Blockchain instance
    private Blockchain blockchain = Blockchain.getInstance();

    // Predefined list of clients (users) who send messages
    private final List<Client> clients = List.of(
            new Client("Chill guy"),
            new Client("Regina Phalange"),
            new Client("Spider-Man"),
            new Client("Milena"),
            new Client("Michael Scarn")
    );

    // Pool of possible message texts
    private final List<String> messages = List.of(
            "Hello everyone!",
            "How's it going?",
            "What's up??",
            "Can anyone see this?",
            "I love this chat!",
            "What are you mining?",
            "The fastest miner wins:D",
            "Let's gooo!",
            "Can't believe it's still Tuesday:'(",
            "Anyone up for a chat;)",
            "I think my computer's overheating... 🔥",
            "Who's winning the race? 🏁",
            "Is this thing on? Testing 1, 2, 3...",
            "When does the next block get mined? ⏳",
            "Just waiting for the next block... 😴",
            "Who needs a snack break during mining? 🍕",
            "Mining's harder than I thought! 😅",
            "Beep beep boop! 🤖",
            "I swear I'm faster than my internet connection!",
            "Is this the future of communication? 🕶️",
            "Blockchain or bust! 🚀",
            "Wanna join my mining pool? 🤝",
            "I think I found the magic number! 💎",
            "Hashing away at it... ⏳",
            "What are we mining for, anyway? 🧐",
            "It's a beautiful day to mine some blocks! 🌞",
            "I'm not saying I'm the best miner, but... 😏"
    );


    @Override
    public void run() {
        Random random = new Random();

        // Keep generating messages while blockchain is accepting them
        while (blockchain.isAcceptingMessages()) {
            try {
                // Wait 200ms between message generations to simulate real-time chat flow
                Thread.sleep(200);

                // Pick a random client and a random message
                Client client = clients.get(random.nextInt(clients.size()));
                String msg = messages.get(random.nextInt(messages.size()));

                // Create and add a new message to the blockchain's pending message list
                Message message = new Message(client.getName(), msg);
                blockchain.addPendingMessage(message);
            } catch (InterruptedException e) {
                // If interrupted, stop the thread gracefully
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                // Re-throw any unexpected exception as runtime
                throw new RuntimeException(e);
            }
        }
    }
}
