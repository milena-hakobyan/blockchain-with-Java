package blockchain;


public class Block {
    private int id;
    private final int minerId;
    private String hash; //the hash of a block is a hash of all fields of a block
    private final String prevHash;
    private long timeStamp;//every block should contain a timestamp representing the time the block was created
    private int magic;
    private double generationTime;
    private final int numOfZeros;
    private String difficultyMessage="";

    public Block(String prevBlockHash, int numOfZeros, int minerId){
        this.numOfZeros = numOfZeros;
        this.prevHash = prevBlockHash;
        this.minerId = minerId;
    }

    public String getHash() { return hash;}

    public int getId() { return id;}

    public int getMinerId() { return minerId;}

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
            temp = StringUtil.applySha256(prevHash + this.id + this.timeStamp + magic +  minerId);
            magic++;
        } while (!temp.startsWith(target));

        long endTime = System.currentTimeMillis();
        this.hash = temp;
        this.generationTime = (endTime - startTime) / 1000.0;
    }


    //printing a block with the given format
    protected void printBlock() {
        System.out.println("Block:");
        System.out.println("Created by # " + minerId);
        System.out.println("Id: " + id);
        System.out.println("Timestamp: " + getTimeStamp());
        System.out.println("Magic number: " + magic);
        System.out.println("Hash of the previous block:\n" + getPrevHash());
        System.out.println("Hash of the block:\n" + getHash());
        System.out.println("Block was generating for " + generationTime + " seconds");
        System.out.println(difficultyMessage);
        System.out.println();
    }
}