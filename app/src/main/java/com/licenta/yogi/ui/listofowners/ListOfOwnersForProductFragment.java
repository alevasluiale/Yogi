package com.licenta.yogi.ui.listofowners;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licenta.yogi.MainActivity;
import com.licenta.yogi.R;
import com.licenta.yogi.generatedcontracts.VolumeCoin;
import com.licenta.yogi.models.Product;
import com.licenta.yogi.models.TransferEvents;
import com.licenta.yogi.models.User;
import com.licenta.yogi.services.TransferEventsIntentService;
import com.licenta.yogi.utils.ProductAdapter;
import com.licenta.yogi.utils.UserAdapter;
import com.licenta.yogi.utils.UserForView;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListOfOwnersForProductFragment extends Fragment {

    private Credentials credentials;
    private FirebaseAuth mAuth;
    private UserAdapter userAdapter;
    private ListView usersListView;
    private DatabaseReference mDatabase;
    private View root;

    private BroadcastReceiver mUpdateUIReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            final ListView userListView = (ListView) root.findViewById(R.id.list_of_owners_for_product_records_view);
            ArrayList<UserForView> usersView = new ArrayList<>();

            WebSocketService webSocketService = new WebSocketService(MainActivity.INFURA_WS_URL,true);

            try {
                webSocketService.connect();
            } catch (ConnectException e) {
                e.printStackTrace();
            }
            Web3j web3j = Web3j.build(webSocketService);

            ArrayList<TransferEvents> transferEventsArrayList = intent.getParcelableArrayListExtra("transferEvents");
            Log.i("fragment",transferEventsArrayList.get(0).getAddress());

            for(TransferEvents event: transferEventsArrayList) {
                org.web3j.abi.datatypes.Function functionGetUserData = new org.web3j.abi.datatypes.Function(
                        "getUser",
                        Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(event.getAddress())),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<Uint256>() {
                                },
                                new TypeReference<Utf8String>() {
                                },
                                new TypeReference<Utf8String>() {
                                },
                                new TypeReference<Utf8String>() {
                                },
                                new TypeReference<Utf8String>() {
                                }));
                String encodedFunctionGetUserData = FunctionEncoder.encode(functionGetUserData);

                EthCall ethCallUser = web3j.ethCall(
                        Transaction.createEthCallTransaction(credentials.getAddress(), MainActivity.CONTRACT_CHAIN_ADDRESS, encodedFunctionGetUserData),
                        DefaultBlockParameterName.LATEST)
                        .sendAsync().join();


                List<Type> listUser = FunctionReturnDecoder.decode(ethCallUser.getValue(), functionGetUserData.getOutputParameters());
                UserForView user = new UserForView(listUser.get(1).getValue().toString()
                        ,listUser.get(3).getValue().toString(),listUser.get(4).getValue().toString(),listUser.get(2).getValue().toString(),
                        event.getAmount());

                usersView.add(user);
            }

            userAdapter = new UserAdapter(root.getContext(),usersView);
            userListView.setAdapter(userAdapter);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mUpdateUIReceiver,
                new IntentFilter("result"));

        root = inflater.inflate(R.layout.fragment_product_list_of_owners, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle bundle = getArguments();
        Product product = bundle.getParcelable("product");

        mDatabase.child("Users").child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("User", snapshot.child("walletPrivateKey").getValue(String.class));
                credentials = Credentials.create(snapshot.child("walletPrivateKey").getValue(String.class));

                PendingIntent result = getActivity().createPendingResult(0,new Intent(),0);
                Intent intent = new Intent(getContext(), TransferEventsIntentService.class);
                intent.putExtra("walletPrivateKey",snapshot.child("walletPrivateKey").getValue(String.class));
                intent.putExtra("contractAddress",product.getContractAddress());
                intent.putExtra("result", result);
                getActivity().startService(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return root;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        ArrayList<TransferEvents> transferEventsArrayList = data.getParcelableArrayListExtra("transferEvents");
        Log.i("fragment",transferEventsArrayList.get(0).getAddress());
        super.onActivityResult(requestCode, resultCode, data);
    }
}
