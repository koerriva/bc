package com.koerriva.project002.core.blockchain;

import com.koerriva.project002.utils.SHA256;
import com.koerriva.project002.utils.StringUtil;

import java.security.PublicKey;

public class TransactionOutput {
    public String id;
    public PublicKey reciepient; //also known as the new owner of these coins.
    public float value; //the amount of coins they own
    public String parentTransactionId; //the id of the transaction this output was created in

    //Constructor
    public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = SHA256.hash(StringUtil.getStringFromKey(reciepient) + value + parentTransactionId);
    }

    //Check if coin belongs to you
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == reciepient);
    }
}
