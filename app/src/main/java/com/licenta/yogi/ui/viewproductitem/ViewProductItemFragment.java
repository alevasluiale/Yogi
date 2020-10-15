package com.licenta.yogi.ui.viewproductitem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licenta.yogi.MainActivity;
import com.licenta.yogi.R;
import com.licenta.yogi.generatedcontracts.VolumeChain;
import com.licenta.yogi.generatedcontracts.VolumeCoin;
import com.licenta.yogi.models.Product;
import com.licenta.yogi.ui.transferproduct.TransferProductFragment;
import com.licenta.yogi.utils.ProductAdapter;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ViewProductItemFragment extends Fragment {

    private ViewProductItemViewModel productItemViewModel;
    private ProductAdapter productAdapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    private TextView productName;
    private TextView productSymbol;
    private TextView productTotal;
    private TextView productLeft;
    private TextView productSold;
    private TextView productHeight;
    private TextView productWitdh;
    private TextView productLength;
    private TextView productProductionDate;
    private TextView productExpirationDate;

    private Button displayInitialOwnerButton;
    private Button showListOfAllOwnersButton;
    private Button transferProductButton;
    private Button removeProductButton;

    private Credentials credentials;
    private View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        productItemViewModel =
                ViewModelProviders.of(this).get(ViewProductItemViewModel.class);
        root = inflater.inflate(R.layout.fragment_product_item_view, container, false);

        Bundle bundle = getArguments();
        Product product = bundle.getParcelable("product");
        productName = root.findViewById(R.id.id_view_product_name);
        productSymbol = root.findViewById(R.id.id_view_product_symbol);
        productTotal = root.findViewById(R.id.id_view_product_total);
        productLeft = root.findViewById(R.id.id_view_product_left);
        productSold = root.findViewById(R.id.id_view_product_sold);
        productProductionDate = root.findViewById(R.id.id_view_product_production_date);
        productExpirationDate = root.findViewById(R.id.id_view_product_expiration_date);

        displayInitialOwnerButton = root.findViewById(R.id.id_view_product_button_initial_owner);
        showListOfAllOwnersButton = root.findViewById(R.id.id_view_product_button_all_owners);
        transferProductButton = root.findViewById(R.id.id_view_product_button_transfer);
        removeProductButton = root.findViewById(R.id.id_view_product_button_remove);

        productName.setText(product.getName());
        productSymbol.setText(product.getSymbol());
        productTotal.setText(String.valueOf(product.getTotalSupply()));
        productLeft.setText(String.valueOf(product.getLeft()));
        productSold.setText(String.valueOf(product.getSold()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        productProductionDate.setText(sdf.format(new Date(product.getProductionDate())));
        productExpirationDate.setText(sdf.format(new Date(product.getExpirationDate())));

        displayInitialOwnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayInitialOwner(product,root.getContext(),v);
            }
        });

        transferProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("product",product);
                Navigation.findNavController(v).navigate(R.id.action_nav_view_product_item_to_nav_view_transfer_product,bundle);
            }
        });

        showListOfAllOwnersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("product",product);
                Navigation.findNavController(v).navigate(R.id.action_nav_view_product_item_to_nav_view_list_of_product_owners,bundle);
            }
        });

        return root;
    }
    private void displayInitialOwner(Product product, Context context,View view) {
        Toast.makeText(context,"Loading data from Blockchain in progress", Toast.LENGTH_SHORT).show();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        try {
            Web3j web3j = Web3j.build(new HttpService(MainActivity.INFURA_URL));

            mDatabase.child("Users").child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.i("User", snapshot.child("walletPrivateKey").getValue(String.class));
                    credentials = Credentials.create(snapshot.child("walletPrivateKey").getValue(String.class));

                    try {

                        org.web3j.abi.datatypes.Function functionGetProductData = new org.web3j.abi.datatypes.Function(
                                "getCreatedProductsSecondPart",
                                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(product.getContractAddress())),
                                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {},new TypeReference<Uint256>() {},
                                        new TypeReference<Uint256>() {},new TypeReference<org.web3j.abi.datatypes.Utf8String>() {}));
                        String encodedFunctionGetProductData = FunctionEncoder.encode(functionGetProductData);
                        Log.i("Adresa contract",product.getContractAddress());

                        EthCall ethCallProduct = web3j.ethCall(
                                Transaction.createEthCallTransaction(credentials.getAddress(),MainActivity.CONTRACT_CHAIN_ADDRESS,encodedFunctionGetProductData),
                                DefaultBlockParameterName.LATEST)
                                .sendAsync().join();


                        List<Type> listProduct = FunctionReturnDecoder.decode(ethCallProduct.getValue(),functionGetProductData.getOutputParameters());

                        org.web3j.abi.datatypes.Function functionGetUserData = new org.web3j.abi.datatypes.Function(
                                "getUser",
                                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(listProduct.get(0).getValue().toString())),
                                Arrays.<TypeReference<?>>asList(
                                        new TypeReference<Uint256>() {},
                                        new TypeReference<Utf8String>() {},
                                        new TypeReference<Utf8String>() {},
                                        new TypeReference<Utf8String>() {},
                                        new TypeReference<Utf8String>() {}));
                        String encodedFunctionGetUserData = FunctionEncoder.encode(functionGetUserData);

                        EthCall ethCallUser = web3j.ethCall(
                                Transaction.createEthCallTransaction(credentials.getAddress(),MainActivity.CONTRACT_CHAIN_ADDRESS,encodedFunctionGetUserData),
                                DefaultBlockParameterName.LATEST)
                                .sendAsync().join();


                        List<Type> listUser = FunctionReturnDecoder.decode(ethCallUser.getValue(),functionGetUserData.getOutputParameters());
                        Log.i("CallCompleted",listUser.get(1).getValue().toString());
                        Bundle bundle = new Bundle();
                        bundle.putString("userName",listUser.get(1).getValue().toString());
                        bundle.putString("userContractAddress",listProduct.get(0).getValue().toString());
                        bundle.putString("userRealAddress",listUser.get(2).getValue().toString());
                        bundle.putString("userCountry",listUser.get(3).getValue().toString());
                        bundle.putString("userCity",listUser.get(4).getValue().toString());
                        bundle.putString("productProductionDate", listProduct.get(1).getValue().toString());
                        bundle.putString("productExpirationDate", listProduct.get(2).getValue().toString());
                        bundle.putString("productRestrictedCountries",listProduct.get(3).getValue().toString());
                        bundle.putParcelable("product",product);

                        Navigation.findNavController(view).navigate(R.id.action_nav_view_product_item_to_nav_view_initial_product_owner,bundle);

                    }
                    catch (Exception ex) {
                        ex.printStackTrace();;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception ex) {
        }
    }

}