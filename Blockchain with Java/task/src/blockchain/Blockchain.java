package blockchain;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blockchain {
    private static Blockchain INSTANCE;
    private int id;
    private final ArrayList<Block> list;
    private final List<Transaction> pendingTransactions;
    private final HashMap<PublicKey, Client> registeredClients;
    private volatile Map<Integer, Miner> minerIdToMiner; // Added
    private int N;
    private volatile boolean acceptingTransactions;

    private Blockchain() {
        id = 1;
        N = 0;
        list = new ArrayList<>();
        pendingTransactions = new ArrayList<>();
        registeredClients = new HashMap<>();
        minerIdToMiner = new HashMap<>(); // Initialize
        acceptingTransactions = true;
    }

    public static Blockchain getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Blockchain();
        }
        return INSTANCE;
    }

    public int getN() {
        return this.N;
    }

    public int getId() {
        return id;
    }

    public void incrementId() {
        this.id++;
    }

    public synchronized List<Transaction> getPendingTransactions() {
        return this.pendingTransactions;
    }

    public synchronized void clearPendingTransactions() {
        pendingTransactions.clear();
    }

    public void startAcceptingTransactions() {
        acceptingTransactions = true;
    }

    public void stopAcceptingTransactions() {
        acceptingTransactions = false;
    }

    public boolean isAcceptingTransactions() {
        return acceptingTransactions;
    }

    public synchronized void registerClient(Client client) {
        PublicKey key = client.getPublicKey();
        if (!registeredClients.containsKey(key)) {
            registeredClients.put(key, client);
        }
    }

    // Register a miner and map its ID
    public synchronized void registerMiner(int id, Miner miner) {
        if (!minerIdToMiner.containsKey(id)) {
            minerIdToMiner.put(id, miner);
            registerClient(miner); // also as regular client
        }
    }

    public synchronized void addTransaction(Transaction tx) {
        if (!tx.isReward()) {
            if (!tx.isValid()) {
                System.out.println("Invalid signature — transaction rejected.");
                return;
            }

            long maxId = list.isEmpty() ? 0 : getLastBlock().getMaxTransactionId();
            if (tx.getId() <= maxId) {
                System.out.println("Transaction ID not strictly greater than last seen — transaction rejected.");
                return;
            }

            Client sender = registeredClients.get(tx.getSenderKey());
            Client receiver = registeredClients.get(tx.getReceiverKey());

            if (sender == null || receiver == null) {
                System.out.println("Unknown sender or receiver — transaction rejected.");
                return;
            }

            if (sender.getBalance() >= tx.getAmount()) {
                sender.subtractBalance(tx.getAmount());
                receiver.addBalance(tx.getAmount());
                pendingTransactions.add(tx);
            }
        } else {
            if (tx.getReceiverKey() == null) {
                System.out.println("Reward transaction missing receiver — rejected.");
                return;
            }

            Client receiver = registeredClients.get(tx.getReceiverKey());
            if (receiver == null) {
                System.out.println("Unknown reward recipient — transaction rejected.");
                return;
            }

            receiver.addBalance(tx.getAmount());
            pendingTransactions.add(tx);
        }
    }

    public synchronized void addBlock(Block b, double creationTime) {
        if (validateBlock(b)) {
            list.add(b);

            // Adjust the difficulty and assign message
            String difficultyMessage = adjustDifficulty(creationTime);
            b.setDifficultyMessage(difficultyMessage);

            // Reward the miner
            rewardMiner(b.getMinerId());

            // Update blockchain state
            incrementId();
        }
    }

    private synchronized void rewardMiner(int id) {
        Miner miner = minerIdToMiner.get(id);
        if (miner != null) {
            miner.reward();
        } else {
            System.out.println("Unknown miner ID — reward skipped.");
        }
    }

    private synchronized String adjustDifficulty(double creationTime) {
        if (creationTime > 2 && N > 0) {
            N--;
            return "N was decreased by 1";
        } else if (creationTime > 0.002) {
            return "N stays the same";
        } else {
            N++;
            return "N was increased to " + N;
        }
    }

    public boolean validateBlock(Block b) {
        Block last = getLastBlock();

        if (last == null) {
            if (!b.getPrevHash().equals("0") || N != 0) return false;

            for (Transaction tx : b.getTransactions()) {
                if (!tx.isValid() || tx.getId() <= 0) return false;
            }
            return b.getHash().startsWith("0".repeat(N));
        }

        if (!b.getPrevHash().equals(last.getHash()) || !b.getHash().startsWith("0".repeat(N))) {
            return false;
        }

        long prevMaxId = last.getMaxTransactionId();
        for (Transaction tx : b.getTransactions()) {
            if (!tx.isValid() || tx.getId() <= prevMaxId)
                return false;
        }

        return true;
    }

    public Block getLastBlock() {
        if (list.isEmpty()) {
            return null;
        }
        return list.getLast();
    }

    public void printBlockChain() {
        list.forEach(Block::printBlock);
    }
}
