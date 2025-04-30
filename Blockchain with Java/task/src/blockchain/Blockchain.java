package blockchain;

import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Blockchain {
    //implementing Singleton pattern
    private static Blockchain INSTANCE;
    protected static int id;
    private final ArrayList<Block> list;
    private List<Message> pendingMessages;
    private Map<String, Client> clients;
    private int N;
    private volatile boolean acceptingMessages;

    private Blockchain() {
        id = 0;
        N = 0;
        list = new ArrayList<>();
        pendingMessages = new ArrayList<>();
        clients = new HashMap<>();
        acceptingMessages = true;
    }

    // Singleton pattern: ensures only one instance of Blockchain
    public static Blockchain getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Blockchain();
        }
        return INSTANCE;
    }

    public int getN() {
        return this.N;
    }

    public static int getId() {
        return id;
    }

    public void incrementId() {
        id++;
    }

    // Synchronized method to safely retrieve the list of pending messages
    public synchronized List<Message> getPendingMessages() {
        return this.pendingMessages;
    }

    // Synchronized method to safely add a message to the pendingMessages list
    public synchronized void addPendingMessage(Message m) {
        // Get the current maximum message ID in the blockchain
        long maxId = list.isEmpty() ? 0 : getLastBlock().getMaxMessageId();

        // Only add the message if it's valid and has a greater ID than the last one
        if (verifyMessage(m) && m.getId() > maxId) {
            pendingMessages.add(m);
        } else {
            System.out.println("Rejected invalid message: " + m);
        }
    }

    public Map<String, Client> getClients() {
        return clients;
    }

    public void clearPendingMessages() {
        pendingMessages.clear();
    }

    // Utility methods to control whether the blockchain is accepting messages
    public void startAcceptingMessages() {
        acceptingMessages = true;
    }

    public void stopAcceptingMessages() {
        acceptingMessages = false;
    }

    public boolean isAcceptingMessages() {
        return acceptingMessages;
    }

    // Method to validate a block and add it to the blockchain
    public synchronized void addBlock(Block b, double creationTime) {
        if (validateBlock(b)) {
            list.add(b);

            // Adjust difficulty based on the block's creation time
            if (creationTime > 60_000 && N > 0) {
                N--; // Decrease difficulty
                b.setDifficultyMessage("N was decreased by 1");
            } else if (creationTime > 10_000) {
                b.setDifficultyMessage("N stays the same");
            } else {
                N++; // Increase difficulty
                b.setDifficultyMessage("N was increased to " + N);
            }

            incrementId();
        }
    }

    // Method to verify the validity of a message (check signature)
    public boolean verifyMessage(Message msg) {
        return msg.isValid();
    }


    public boolean validateBlock(Block b) {
        Block last = getLastBlock();

        if (last == null) {
            //must be the first block, prevHash should be "0", and difficulty N should be 0
            if (!b.getPrevHash().equals("0") || N != 0) return false;

            //also verify messages (ID > 0, signature valid)
            for (Message m : b.getMessages()) {
                if (!verifyMessage(m) || m.getId() <= 0) return false;
            }
            return b.getHash().startsWith("0".repeat(N));
        }

        //a block's prevhash should match the current last block's hash,
        // and the block should start with the specified num of zeros
        if (!b.getPrevHash().equals(last.getHash()) || !b.getHash().startsWith("0".repeat(N))) {
            return false;
        }

        long prevMaxId = last.getMaxMessageId();

        //check messages: valid signature, and ID strictly greater than previous max
        for (Message m : b.getMessages()) {
            if (!verifyMessage(m) || m.getId() <= prevMaxId) {
                return false;
            }
        }

        return true;
    }


    public Block getLastBlock() {
        if (list.isEmpty()) {
            return null;
        }
        return list.getLast();
    }


    public boolean validateBlockchain() {
        if (list.isEmpty()) return true;

        for (int i = 0; i < list.size(); i++) {
            Block block = list.get(i);

            if (i == 0) {
                // Genesis block: must have prevHash "0" and difficulty N = 0
                if (!block.getPrevHash().equals("0") || block.getId() != 1 || !block.getHash().startsWith("0".repeat(0))) {
                    return false;
                }
            } else {
                if (!validateBlock(block)) {
                    return false;
                }
            }
        }

        return true;
    }

    public void printBlockChain() {
        list.stream().forEach(Block::printBlock);
    }
}