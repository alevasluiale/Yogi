package com.licenta.yogi.services;

import android.os.AsyncTask;
import android.util.Log;

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
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.util.UUID;

public class AddProductAsyncTask extends AsyncTask<Void,Integer, Boolean> {

    private Product product;
    private Credentials credentials;
    private DatabaseReference mDatabase;

    public AddProductAsyncTask(Product product) {
        this.product = product;
    }
    protected Boolean doInBackground(Void... params) {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        product.setId(UUID.randomUUID().toString());
        mDatabase.child("Users").child(userUid).child("Products").child(product.getId()).setValue(product);

        try
        {
            Web3j web3j = Web3j.build(new HttpService(MainActivity.INFURA_URL));

            mDatabase.child("Users").child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    credentials = Credentials.create(snapshot.child("walletPrivateKey").getValue(String.class));
                    ContractGasProvider gasProvider = new StaticGasProvider(BigInteger.valueOf(500000000000L), BigInteger.valueOf(3000000));
                    try {
                        VolumeCoin coinProductContract = VolumeCoin.deploy(
                                web3j, credentials,
                                gasProvider,
                                BigInteger.valueOf(product.getTotalSupply()),
                                product.getName(),
                                product.getSymbol()
                        ).sendAsync().join();
                        mDatabase.child("Users").child(userUid)
                                .child("Products").child(product.getId())
                                .child("contractAddress").setValue(coinProductContract.getContractAddress());
                        Log.i("ContractCreated",coinProductContract.getContractAddress());
                        VolumeChain volumeChainContract = VolumeChain.load(MainActivity.CONTRACT_CHAIN_ADDRESS,web3j,credentials,gasProvider);
                            Log.i("VolumeChain","Contract loaded");


                            TransactionReceipt addProductTransaction = volumeChainContract.addProducts(coinProductContract.getContractAddress(),
                                    product.getName(),
                                    product.getId(),
                                    product.getSymbol(),
                                    BigInteger.valueOf(product.getTotalSupply()),
                                    BigInteger.valueOf(product.getLength()),
                                    BigInteger.valueOf(product.getWidth()),
                                    BigInteger.valueOf(product.getHeight()),
                                    credentials.getAddress(),
                                    BigInteger.valueOf(product.getProductionDate()),
                                    BigInteger.valueOf(product.getExpirationDate()),
                                    product.getRestrictedCountries()).sendAsync().join();
                            Log.i("TransactionAddProduct",addProductTransaction.getGasUsed() + addProductTransaction.getContractAddress());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch(Exception ex) {
            return false;
        }
        return true;
    }


    protected void onPostExecute(Long result) {
        Log.i("AsyncTask", "Task executed");
    }
}
