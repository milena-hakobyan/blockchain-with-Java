package blockchain;

import java.util.Date;
import java.util.List;

public class Block {
    private int id;
    private final int minerId;
    private String hash; //the hash of a block is a hash of all fields of a block
    private final String prevHash;
    private long timeStamp;//every block should contain a timestamp representing the time the block was created
    private int magic;
    private double generationTime;
    private final int numOfZeros;
    private final List<Message> messages;
    private String difficultyMessage;

    public Block(String prevBlockHash, int numOfZeros, int minerId, List<Message> messages){
        this.numOfZeros = numOfZeros;
        this.prevHash = prevBlockHash;
        this.minerId = minerId;
        this.messages = messages;
    }

    public String getHash() {
        return hash;
    }

    public int getId() { return id;}

    public String getPrevHash() { return prevHash;}

    public long getTimeStamp() { return timeStamp;}

    public double getGenerationTime() { return generationTime;}

    public void setId(int id){  this.id = id;}

    public void setDifficultyMessage(String s){ difficultyMessage = s;}


    /**
     * Mines the block by finding a magic number such that the hash of the block
     * starts with the required number of leading zeros.
     * Sets the block's ID, timestamp, hash, magic number, and generation time.
     * If the difficulty (number of zeros) is zero, the first hash will be accepted.
     */
    public void mine(int blockID) {
        this.id = blockID;
        this.magic = 0;

        String target = "0".repeat(numOfZeros);
        String temp;

        long startTime = System.currentTimeMillis();
        this.timeStamp = startTime;

        do {
            temp = StringUtil.applySha256(this.prevHash + this.id + this.timeStamp +
                                            this.magic +  this.minerId + this.messages);
            magic++;
        } while (!temp.startsWith(target));

        long endTime = System.currentTimeMillis();
        this.hash = temp;
        this.generationTime = (endTime - startTime) / 1000.0;
    }


    
    //printing a block with the given format
    protected void printBlock() {
        System.out.println("Block:");
        System.out.println("Created by miner # " + minerId);
        System.out.println("Id: " + id);
        System.out.println("Timestamp: " + getTimeStamp());
        System.out.println("Magic number: " + magic);
        System.out.println("Hash of the previous block:\n" + getPrevHash());
        System.out.println("Hash of the block:\n" + getHash());
        System.out.println("Block data: ");
        messages.stream().forEach(message -> {
            System.out.println(message.getSender() +": " + message.getText());
        });
        System.out.println("Block was generating for " + generationTime + " seconds");
        System.out.println(difficultyMessage);
        System.out.println();
    }
}