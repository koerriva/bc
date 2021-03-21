package com.koerriva.project002.core.blockchain;

import com.koerriva.project002.utils.SHA256;
import com.koerriva.project002.utils.StringUtil;

import java.util.ArrayList;

public class Block {
    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    private final long timestamp;
    private int nonce;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();

        this.hash = hash();
    }

    public String hash() {
        return SHA256.hash(previousHash + timestamp + nonce + merkleRoot);
    }

    public void mine(int difficulty){
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0','0');
        while (!hash.substring(0,difficulty).equals(target)){
            nonce ++;
            hash = hash();
        }
        System.out.println("Block Mined!!"+hash);
    }

    //Add transactions to this block
    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if(transaction == null) return false;
        if(previousHash != "0") {
            if(transaction.processTransaction() != true) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }
}
