package blockchain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class BlockchainSimulation {
    public void startMining() {
        //Executor for mining: 10 threads simulate 10 miners mining in parallel
        ExecutorService miningExecutor = Executors.newFixedThreadPool(10);

        //Executor for message generation: only one thread generates messages in parallel
        ExecutorService messageExecutor = Executors.newSingleThreadExecutor();

        Blockchain blockchain = Blockchain.getInstance();

        //holds the list of messages that were generated while the previous block was being mined
        //A block should contain messages that the blockchain received during the creation of the previous block
        List<Message> previousMessages = new ArrayList<>();

        try {
            for (int i = 0; i < 5; i++) { //simulate mining of 5 blocks
                // Allow messages to start being added
                blockchain.startAcceptingMessages();

                // Launching the message generator in parallel
                messageExecutor.submit(new MessageGenerator());

                // Giving the generator some time to accumulate messages before mining starts
                Thread.sleep(500);

                String prevHash = (i == 0) ? "0" : blockchain.getLastBlock().getHash();

                List<Callable<Block>> callables = new ArrayList<>();

                // miners get the messages collected during the previous round
                List<Message> messagesForBlock = new ArrayList<>(previousMessages);

                // Creating 10 miners, each mining a block
                for (int m = 0; m < 10; m++) {
                    int minerId = m + 1;
                    callables.add(() -> {
                        Block block = new Block(prevHash, blockchain.getN(), minerId, messagesForBlock);
                        block.mine(blockchain.getId()); // perform proof-of-work
                        return block;
                    });
                }

                // Start mining: the first block successfully mined is accepted
                Block block = miningExecutor.invokeAny(callables);

                // Stop accepting messages once a block is mined
                blockchain.stopAcceptingMessages();

                //capture all new messages generated while this block was being mined
                previousMessages = new ArrayList<>(blockchain.getPendingMessages());
                blockchain.clearPendingMessages();

                //validate the winning block and add it to the blockchain if it's valid
                if (blockchain.validateBlock(block)) {
                    blockchain.addBlock(block, block.getGenerationTime());
                }
            }

            //print all blocks in the blockchain after simulation is complete
            blockchain.printBlockChain();

            //stop the message generator thread and shutdown miner threads
            messageExecutor.shutdownNow();
            messageExecutor.awaitTermination(5, TimeUnit.SECONDS);
            miningExecutor.shutdown();
            miningExecutor.awaitTermination(5, TimeUnit.SECONDS);

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
