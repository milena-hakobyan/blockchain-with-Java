package blockchain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class BlockchainSimulator {

    public void startMining(){
        ExecutorService executor = Executors.newFixedThreadPool(10);

        Blockchain blockchain = Blockchain.getInstance();
        try {
            for (int i = 0; i < 5; i++) {
                List<Callable<Block>> callables = new ArrayList<>();
                String prevHash = i==0?"0":blockchain.getLastBlock().getHash();
                for(int m = 0; m < 10; m++){
                    int minderId = m + 1; //miner-IDs start from 1
                    callables.add(()->{
                        Block block =  new Block(prevHash, blockchain.getN(), minderId);
                        block.mine(blockchain.getId());
                        return block;
                    });
                }
                Block block = executor.invokeAny(callables);
                if(blockchain.validateBlock(block)) {
                    blockchain.addBlock(block, block.getGenerationTime());
                }
            }
            blockchain.printBlockChain();
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
