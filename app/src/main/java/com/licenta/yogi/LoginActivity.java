package com.licenta.yogi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.ExecutionException;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class LoginActivity extends Activity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailText);
        passwordEditText = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.LoginButton);
    }
    public void Login(View view)
    {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(TextUtils.isEmpty(email)) {
            emailEditText.setError("Please type email.");
            emailEditText.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            passwordEditText.setError("Please insert password.");
            passwordEditText.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Login", "signInWithEmail:success");
                            createWalletIfUserDoesNotHave(mAuth.getCurrentUser().getUid());
                            startActivity(new Intent(LoginActivity.this, NavigationActivity.class));
                        } else {
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            emailEditText.getText().clear();
                            passwordEditText.getText().clear();
                            emailEditText.setError("Authentication failed");
                            emailEditText.requestFocus();
                        }
                    }
                });
    }

//    public void Register(View view) {
//        if(loginButton.getText().toString().equals("Login"))
//        {
//            loginButton.setText("Go back to Login");
//            retypePasswordText.setVisibility(View.VISIBLE);
//            firstNameText.setVisibility(View.VISIBLE);
//            lastNameText.setVisibility(View.VISIBLE);
//        }
//        else {
//            if (validateUserFields()) {
//                mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(),passwordEditText.getText().toString())
//                        .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    // Sign in success, update UI with the signed-in user's information
//                                    Log.d("LoginActivity", "createUserWithEmail:success");
//                                    FirebaseUser user = mAuth.getCurrentUser();
//                                    try {
//                                        String walletFileName = createWalletForUser(passwordEditText.getText().toString());
//                                        File walletDir = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath()
//                                                + "/" + walletFileName);
//
//                                        Credentials credentials = null;
//                                        credentials = WalletUtils.loadCredentials(passwordEditText.getText().toString(), walletDir);
//                                        String walletAddress = credentials.getAddress();
//
////                                        setUserFirstAndLastName(user.getUid(),walletFileName,walletAddress);
//
//                                        Intent walletIntent = new Intent(LoginActivity.this,WalletActivity.class);
//                                        walletIntent.putExtra("userPassword",passwordEditText.getText().toString());
//                                        walletIntent.putExtra("walletFileName",walletFileName);
//                                        startActivity(walletIntent);
//                                    }
//                                    catch(Exception ex) {
//                                        Log.e("LoginActivity", ex.getMessage());
//                                    }
//                                } else {
//                                    // If sign in fails, display a message to the user.
//                                    Log.w("LoginActivity", "createUserWithEmail:failure", task.getException());
//                                }
//                            }
//                        });
//            }
//        }
//    }
//    private boolean validateUserFields() {
//
//        String email = emailEditText.getText().toString();
//        String password = passwordEditText.getText().toString();
//        boolean validation = true;
//
//        if(TextUtils.isEmpty(email)) {
//            emailEditText.setError("Please type email.");
//            validation = false;
//        }
//        if(TextUtils.isEmpty(password)) {
//            passwordEditText.setError("Please insert password.");
//            validation = false;
//        }
//        if(password.length()<6) {
//            passwordEditText.setError("Password should be at least 6 characters.");
//            validation = false;
//        }
//        return validation;
//    }
//    private void setUserFirstAndLastName(String uid,String walletFileName,String walletAddress) {
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        User user = new User(
//                emailEditText.getText().toString(),
//                firstNameText.getText().toString(),
//                lastNameText.getText().toString(),
//                walletFileName,walletAddress
//        );
//        mDatabase.child("Users").child(uid).setValue(user);
//    }
//    private String createWalletForUser(String password) throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
//        String walletPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath();
//        File walletDir = new File(walletPath);
//        String fileName;
//        try{
//            fileName =  WalletUtils.generateLightNewWalletFile(password,walletDir);
//            return fileName;
//        }
//        catch (Exception e){
//            Log.i("Wallet",e.getMessage());
//            throw e;
//        }
//    }
    public void createWalletIfUserDoesNotHave(String userUid) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String walletFileName = snapshot.child("walletFileName").getValue(String.class);
                boolean isAdmin = snapshot.child("isAdmin").getValue(Boolean.class);
                if (walletFileName == null) {
                    try {
                        mDatabase.child("Users").child(userUid).child("walletFileName").setValue(createWallet(userUid,isAdmin));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String createWallet(String walletPasswordUserUid,boolean isAdmin)
            throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        Credentials credential = null;
        if(isAdmin == true) {
            credential = Credentials.create("f2215278f4feba41619b0541de151769fed1ad0ca8511f4acb9ade2faef7c9a3");
        }
        String walletPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getAbsolutePath();
        File walletDir = new File(walletPath);
        String fileName;
        try{
            if(isAdmin == false) {
                fileName =  WalletUtils.generateLightNewWalletFile(walletPasswordUserUid,walletDir);
            }
            else {
                fileName = WalletUtils.generateWalletFile(walletPasswordUserUid,
                        credential.getEcKeyPair(),
                        walletDir,
                        true);
            }
            return fileName;
        }
        catch (Exception e){
            Log.i("Wallet",e.getMessage());
            throw e;
        }
    }
}
