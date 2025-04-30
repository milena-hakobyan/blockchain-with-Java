package blockchain;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Blockchain{
    //implementing Singleton pattern
    private static Blockchain INSTANCE;
    protected static int id;
    private ArrayList<Block> list;
    private int N; //number of zeros, denoted by N

    public Blockchain (){
        id=1;
        N=0;
        list = new ArrayList<>();
    }

    public static Blockchain getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new Blockchain();
        }
        return INSTANCE;
    }

    public int getId(){return this.id;}

    public int getN(){return this.N;}

    public Block getLastBlock() {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1);
    }


    //after the winner miner submits a block, blockchain should validate it
    // (prevHash is the hash of the current last and starts with N zeros)
    public void addBlock(Block b, double creationTime){
        if(validateBlock(b)) {
            list.add(b);
            if(creationTime > 60_000 && N > 0){
                N--;
                b.setDifficultyMessage("N was decreased by 1");
            }
            else if(creationTime > 10_000){
                b.setDifficultyMessage("N stays the same");
            }
            else {
                N++;
                b.setDifficultyMessage("N was increased to " + N);
            }
            id++;
        }
    }

    public boolean validateBlock(Block b){
        if(list.isEmpty() && b.getPrevHash().equals("0") && N==0){
            return true;
        }
        else if(b.getPrevHash().equals(list.getLast().getHash()) &&
                b.getHash().startsWith("0".repeat(N))) {
            return true;
        }
        return false;
    }
    

    // returns true if the blockchain is valid.
    public boolean validateBlockchain(){
        for(int i = 1; i < list.size();i++){
            if(!list.get(i).getPrevHash().equals(list.getLast().getHash()))
                return false;
        }
        return true;
    }


    public void printBlockChain(){
        for(Block block: list)
            block.printBlock();
    }
}
