package com.licenta.yogi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.facebook.login.Login;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licenta.yogi.models.TransferEvents;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private Menu menu;
    private TextView nameTextView;
    private TextView emailTextView;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private Toolbar toolbar;
    private boolean isAdmin = false;
    private boolean isManufacturer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        nameTextView = navigationView.getHeaderView(0).findViewById(R.id.nameLabelText);
        emailTextView = navigationView.getHeaderView(0).findViewById(R.id.emailLabelText);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        menu = navigationView.getMenu();

        if(currentUser != null) {
            mDatabase.child("Users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    nameTextView.setText(snapshot.child("firstName").getValue(String.class) + " " + snapshot.child("lastName").getValue(String.class));
                    emailTextView.setText(currentUser.getEmail());
                    isAdmin = snapshot.child("isAdmin").exists() ? snapshot.child("isAdmin").getValue(boolean.class) : false  ;
                    isManufacturer = snapshot.child("userType").exists() ? snapshot.child("userType").getValue(Integer.class) == 0 : false;
                    if(isAdmin ==true) {
                        menu.findItem(R.id.nav_add_users).setVisible(true);
                        menu.findItem(R.id.nav_delete_users).setVisible(true);
                    }
                    else {
                        menu.findItem(R.id.nav_add_users).setVisible(false);
                        menu.findItem(R.id.nav_delete_users).setVisible(false);
                    }
                    if(isManufacturer == true || isAdmin == true) {
                        menu.findItem(R.id.nav_add_products).setVisible(true);
                    }
                    else {
                        menu.findItem(R.id.nav_add_products).setVisible(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            startActivity(new Intent(NavigationActivity.this, LoginActivity.class));
        }


        setSupportActionBar(toolbar);
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_wallet,R.id.nav_navigation_products,R.id.nav_add_products,
                R.id.nav_add_users, R.id.nav_delete_users, R.id.nav_approve_transactions)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void log_out(MenuItem item) {
        mAuth.signOut();
        startActivity(new Intent(NavigationActivity.this,LoginActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        data.setAction("result");
        LocalBroadcastManager.getInstance(this).sendBroadcast(data);
        ArrayList<TransferEvents> transferEventsArrayList = data.getParcelableArrayListExtra("transferEvents");
        Log.i("TRANSFER",transferEventsArrayList.get(0).getAddress());
        super.onActivityResult(requestCode, resultCode, data);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    }
}