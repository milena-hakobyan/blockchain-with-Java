package blockchain;

import java.security.Signature;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Blockchain {
    //implementing Singleton pattern
    private static Blockchain INSTANCE;
    protected static int id;
    private final ArrayList<Block> list;
    private final List<Message> pendingMessages;
    private int N;
    private volatile boolean acceptingMessages;

    // Private constructor for Singleton pattern
    private Blockchain() {
        id = 1;
        N = 0;
        list = new ArrayList<>();
        pendingMessages = new ArrayList<>();
        acceptingMessages = true;
    }

    // Returns the singleton instance
    public static Blockchain getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Blockchain();
        }
        return INSTANCE;
    }

    // Getter for mining difficulty
    public int getN() {
        return this.N;
    }

    // Getter for block ID
    public static int getId() {
        return id;
    }

    // Returns the list of pending messages (synchronized for thread safety)
    public synchronized List<Message> getPendingMessages() {
        return this.pendingMessages;
    }

    // Adds a message to the pending list (thread-safe)
    public synchronized void addPendingMessage(Message m) {
        pendingMessages.add(m);
    }

    // Increments the global block ID
    public void incrementId() {
        id++;
    }

    // Clears the list of pending messages
    public void clearPendingMessages() {
        pendingMessages.clear();
    }

    // Enables message intake during a mining round
    public void startAcceptingMessages() {
        acceptingMessages = true;
    }

    // Disables message intake when mining ends
    public void stopAcceptingMessages() {
        acceptingMessages = false;
    }

    // Returns whether message intake is allowed
    public boolean isAcceptingMessages() {
        return acceptingMessages;
    }

    // Gets the last block in the blockchain
    public Block getLastBlock() {
        if (list.isEmpty()) {
            return null;
        }
        return list.getLast();
    }

    // Adds a block to the chain if it passes validation and adjusts difficulty based on mining time
    public synchronized void addBlock(Block b, double creationTime) {
        if (validateBlock(b)) {
            list.add(b);

            // Adjust difficulty based on block creation time
            if (creationTime > 60_000 && N > 0) {
                N--;
                b.setDifficultyMessage("N was decreased by 1");
            } else if (creationTime > 10_000) {
                b.setDifficultyMessage("N stays the same");
            } else {
                N++;
                b.setDifficultyMessage("N was increased to " + N);
            }

            incrementId();
        }
    }

    // Validates a block based on its previous hash and difficulty
    public boolean validateBlock(Block b) {
        if (list.isEmpty() && b.getPrevHash().equals("0") && N == 0) {
            return true; // Genesis block
        } else if (b.getPrevHash().equals(list.getLast().getHash()) &&
                b.getHash().startsWith("0".repeat(N))) {
            return true;
        }
        return false;
    }

    // Validates the entire blockchain's integrity
    public boolean validateBlockchain() {
        for (int i = 1; i < list.size(); i++) {
            if (!list.get(i).getPrevHash().equals(list.get(i - 1).getHash()))
                return false;
        }
        return true;
    }

    // Prints the entire blockchain
    public void printBlockChain() {
        list.stream().forEach(Block::printBlock);
    }
}