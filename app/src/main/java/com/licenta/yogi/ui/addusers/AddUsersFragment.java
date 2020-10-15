package com.licenta.yogi.ui.addusers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licenta.yogi.LoginActivity;
import com.licenta.yogi.MainActivity;
import com.licenta.yogi.NavigationActivity;
import com.licenta.yogi.R;
import com.licenta.yogi.generatedcontracts.VolumeChain;
import com.licenta.yogi.models.User;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.MalformedInputException;
import java.util.concurrent.ExecutionException;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.widget.Toast.LENGTH_SHORT;

public class AddUsersFragment extends Fragment {

    private AddUsersViewModel addUsersViewModel;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private View root;
    private EditText userFirstName;
    private EditText userLastName;
    private EditText userEmail;
    private EditText userPassword;
    private EditText userRetypedPassword;
    private EditText userCountry;
    private EditText userCity;
    private Spinner spinner;
    private Integer[] userType = new Integer[1];

    private String walletFileName;

    private void setWalletFileName(String fileName) {
        walletFileName = fileName;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addUsersViewModel = ViewModelProviders.of(this).get(AddUsersViewModel.class);
        root = inflater.inflate(R.layout.fragment_add_users, container, false);
        userFirstName = root.findViewById(R.id.id_add_users_user_first_name);
        userLastName = root.findViewById(R.id.id_add_users_user_last_name);
        userEmail = root.findViewById(R.id.id_add_users_user_email);
        userPassword = root.findViewById(R.id.id_add_users_user_password);
        userRetypedPassword = root.findViewById(R.id.id_add_users_user_retype_password);
        userCountry = root.findViewById(R.id.id_add_users_user_country);
        userCity = root.findViewById(R.id.id_add_users_user_city);
        spinner = root.findViewById(R.id.id_add_users_user_type);
        userType[0] = 0;
        addUsersViewModel.getUserEmail().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                userEmail.setText(s);
            }
        });
        addUsersViewModel.getUserFirstName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                userFirstName.setText(s);
            }
        });
        addUsersViewModel.getUserLastName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                userLastName.setText(s);
            }
        });
        addUsersViewModel.getUserPassword().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                userPassword.setText(s);
            }
        });
        addUsersViewModel.getUserRetypedPassword().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                userRetypedPassword.setText(s);
            }
        });
        addUsersViewModel.getUserCountry().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                userCountry.setText(s);
            }
        });
        addUsersViewModel.getUserCity().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                userCity.setText(s);
            }
        });
        root.findViewById(R.id.id_add_users_add_user_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateUserFields() == false) return;

                mAuth = FirebaseAuth.getInstance();

                mAuth.createUserWithEmailAndPassword(userEmail.getText().toString(),userPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(userEmail.getText().toString(),userFirstName.getText().toString(),userLastName.getText().toString(),
                                    userCountry.getText().toString(),userCity.getText().toString(),userType[0]);

                            FirebaseUser newUser = mAuth.getCurrentUser();

                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("Users").child(newUser.getUid()).setValue(user);

                            addUserToBlockchain(user,newUser.getUid());
                            Toast.makeText(root.getContext(),"User added with success",LENGTH_SHORT).show();
                            clearFields();
                            mAuth.signOut();
                        }
                        else {
                            Toast.makeText(root.getContext(),"Failed to add user " + task.getException().getMessage(),LENGTH_SHORT).show();
                            clearFields();
                        }
                    }
                });
                mAuth.signOut();
                Task<AuthResult> adminSignTask = mAuth.signInWithEmailAndPassword("alex@yahoo.com", "test123");
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userType[0] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return root;
    }


    private boolean validateUserFields() {
        String firstName = userFirstName.getText().toString();
        String lastName = userLastName.getText().toString();
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String retypedPassword = userRetypedPassword.getText().toString();
        String country = userCountry.getText().toString();
        String city = userCity.getText().toString();
        boolean validation = true;

        if(TextUtils.isEmpty(email)) {
            userEmail.setError("Please type email.");
            validation = false;
        }
        if(TextUtils.isEmpty(password)) {
            userPassword.setError("Please insert password.");
            validation = false;
        }
        if(password.length()<6) {
            userPassword.setError("Password should be at least 6 characters.");
            validation = false;
        }
        return validation;
    }

    private void clearFields() {
        userFirstName.getText().clear();
        userLastName.getText().clear();
        userEmail.getText().clear();
        userPassword.getText().clear();
        userRetypedPassword.getText().clear();
        userCountry.getText().clear();
        userCity.getText().clear();
        spinner.setSelection(0);
    }
    public void addUserToBlockchain(User user,String userUid) {
        try {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            BigInteger privateKeyInDec = ecKeyPair.getPrivateKey();

            String sPrivatekeyInHex = privateKeyInDec.toString(16);

            WalletFile aWallet = Wallet.createLight(userUid, ecKeyPair);
            String sAddress = aWallet.getAddress();

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Users").child(userUid).child("walletPrivateKey").setValue(sPrivatekeyInHex);
            mDatabase.child("Users").child(userUid).child("walletAddress").setValue(sAddress);

            user.setWalletAddress(sAddress);


            Credentials credentials = Credentials.create("f2215278f4feba41619b0541de151769fed1ad0ca8511f4acb9ade2faef7c9a3");


            Web3j web3j = Web3j.build(new HttpService(MainActivity.INFURA_URL));
            Web3ClientVersion clientVersion = web3j.web3ClientVersion().sendAsync().get();

            if(!clientVersion.hasError()){
                Log.i("","Connected to infura");
            }
            else {
                Log.i("WalletActivity",clientVersion.getError().getMessage());
            }

            VolumeChain volumeContract = VolumeChain.load(
                    MainActivity.CONTRACT_CHAIN_ADDRESS,
                    web3j,credentials,
                    BigInteger.valueOf(500000000000L),
                    BigInteger.valueOf(3000000));

            TransactionReceipt addUserToBlockchainTransaction = volumeContract.addUser(
                    user.getWalletAddress(),
                    BigInteger.valueOf(user.getUserType()),
                    user.getFirstName()+" "+user.getLastName(),
                    "adresa",
                    user.getCountry(),
                    user.getCity()
                    ).sendAsync().get();

            TransactionReceipt transactionReceiptRemoteCall;
            transactionReceiptRemoteCall = Transfer.sendFunds(web3j, credentials, user.getWalletAddress(), BigDecimal.valueOf(500), Convert.Unit.GWEI).sendAsync().join();
        }
        catch (Exception e) {
            Log.e("AddUser",e.getMessage());
        }


//        VolumeChain volumeChainContract = VolumeChain.load(MainActivity.CONTRACT_CHAIN_ADDRESS, web3j, <credentials>, GAS_PRICE, GAS_LIMIT);
    }
}