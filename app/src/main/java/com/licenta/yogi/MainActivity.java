package com.licenta.yogi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    public static String INFURA_URL = "https://rinkeby.infura.io/v3/f64a78a20caf43ec93823aab895af984";
    public static String INFURA_WS_URL = "wss://rinkeby.infura.io/ws/v3/f64a78a20caf43ec93823aab895af984";
    public static String CONTRACT_CHAIN_ADDRESS = "0xE09d9a1E9F6E6F26fcf2c852a360e15E8f6CD0d9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        setupBouncyCastle();
    }
    public void onStart() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)  {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        startActivity(new Intent(MainActivity.this, NavigationActivity.class));
        super.onStart();
    }

    private void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            // Web3j will set up the provider lazily when it's first used.
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            // BC with same package name, shouldn't happen in real life.
            return;
        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }
}
