package com.licenta.yogi.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.kenai.jffi.Main;
import com.licenta.yogi.MainActivity;
import com.licenta.yogi.generatedcontracts.VolumeCoin;
import com.licenta.yogi.models.TransferEvents;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.tx.Contract;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TransferEventsIntentService extends IntentService {
    public TransferEventsIntentService() {
        super(TransferEventsIntentService.class.getSimpleName());
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        PendingIntent reply = intent.getParcelableExtra("result") ;
        WebSocketService webSocketService = new WebSocketService(MainActivity.INFURA_WS_URL,true);
        try {
            webSocketService.connect();
        } catch (ConnectException e) {
            e.printStackTrace();
        }
        Web3j web3j = Web3j.build(webSocketService);
        Credentials credentials = Credentials.create(intent.getStringExtra("walletPrivateKey"));
        ContractGasProvider gasProvider = new StaticGasProvider(BigInteger.valueOf(500000000000L), BigInteger.valueOf(3000000));
        try {

            VolumeCoin coinProductContract = VolumeCoin.load(intent.getStringExtra("contractAddress"), web3j, credentials, gasProvider);
            Log.i("aici",intent.getStringExtra("contractAddress"));
            final EthFilter ethFilter = new EthFilter(DefaultBlockParameterName.EARLIEST,
                    DefaultBlockParameterName.LATEST,
                    coinProductContract.getContractAddress()
            );
            ethFilter.addSingleTopic(EventEncoder.encode(coinProductContract.TRANSFER_EVENT));

            Intent result = new Intent();

            ArrayList<TransferEvents> transferEventsArrayList = new ArrayList<TransferEvents>();
            web3j.ethLogFlowable(ethFilter).subscribe(event -> {
                
                Uint256 amount = (Uint256) FunctionReturnDecoder.decodeIndexedValue(event.getData(),new TypeReference<Uint256>(){});
                Address receiver = (Address) FunctionReturnDecoder.decodeIndexedValue(event.getTopics().get(2),new TypeReference<Address>(){});
                transferEventsArrayList.add(new TransferEvents(receiver.getValue(),amount.getValue().longValue()));

            });

            result.putParcelableArrayListExtra("transferEvents",transferEventsArrayList);
            Log.i(result.getIdentifier(),"result intent");
            reply.send(this,0,result);
        }
        catch(Exception ex) {
            Log.i("TransferEventes",ex.getMessage());
            ex.printStackTrace();
        }
    }
}
