package com.licenta.yogi.services;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licenta.yogi.MainActivity;
import com.licenta.yogi.generatedcontracts.VolumeChain;
import com.licenta.yogi.generatedcontracts.VolumeCoin;
import com.licenta.yogi.models.Product;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.util.UUID;

public class TransferProductAsyncTask  extends AsyncTask<Void,Integer, Boolean> {

    private Product product;
    private Credentials credentials;
    private DatabaseReference mDatabase;
    private boolean isSuccess = true;
    private String addressToTransfer;
    private BigInteger amountToTransfer;

    public TransferProductAsyncTask(Product product,String addressToTransfer,BigInteger amountToTransfer) {
        this.product = product;
        this.addressToTransfer = addressToTransfer;
        this.amountToTransfer = amountToTransfer;
    }

    protected Boolean doInBackground(Void... params) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        try {
            Web3j web3j = Web3j.build(new HttpService(MainActivity.INFURA_URL));

            mDatabase.child("Users").child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.i("User", snapshot.child("walletPrivateKey").getValue(String.class));
                    credentials = Credentials.create(snapshot.child("walletPrivateKey").getValue(String.class));
                    ContractGasProvider gasProvider = new StaticGasProvider(BigInteger.valueOf(500000000000L), BigInteger.valueOf(3000000));
                    try {
                        VolumeCoin coinProductContract = VolumeCoin.load(product.getContractAddress(),web3j,credentials,gasProvider);

                        Log.i("Adresa contract",product.getContractAddress());
                        TransactionReceipt transferProductTransaction =
                                coinProductContract.transfer(
                                        addressToTransfer,
                                        amountToTransfer,
                                        BigInteger.valueOf(System.currentTimeMillis())).sendAsync().join();

                    } catch (Exception ex) {
                        isSuccess = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception ex) {
            isSuccess = false;
        }
        return isSuccess;
    }
}
