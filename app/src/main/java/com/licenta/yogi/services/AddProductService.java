package com.licenta.yogi.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import java8.util.concurrent.CompletableFuture;

public class AddProductService extends Service {

    private DatabaseReference mDatabase;
    private Credentials credentials;

    public AddProductService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.

        createProductAndDeployToBlockchain(intent.getParcelableExtra("Product"));
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    public void createProductAndDeployToBlockchain(Product product) {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        product.setId(UUID.randomUUID().toString());
        mDatabase.child("Users").child(userUid).child("Products").child(product.getId()).setValue(product);

        try
        {
            Web3j web3j = Web3j.build(new HttpService(MainActivity.INFURA_URL));
            Web3ClientVersion clientVersion = web3j.web3ClientVersion().sendAsync().get();

            mDatabase.child("Users").child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.i("User",snapshot.child("walletPrivateKey").getValue(String.class));
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

                        VolumeChain volumeChainContract = VolumeChain.load(MainActivity.CONTRACT_CHAIN_ADDRESS,web3j,credentials,gasProvider);
                        if(volumeChainContract.isValid()) {
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
                        }
                        else {
                            Log.e("VolumeChainLoading", "FAILED");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if(credentials != null) {
                Log.i("Credentials loaded", credentials.getAddress());


            }
            if(!clientVersion.hasError()){
                Log.i("","Connected to infura");
            }
            else {
                Log.i("WalletActivity",clientVersion.getError().getMessage());
            }
        }
        catch(Exception ex) {
            Log.i("AddProducts",ex.getMessage());
        }

    }
}
