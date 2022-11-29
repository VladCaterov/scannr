package com.example.scannr.dashboard;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.scannr.HomeFragment;
import com.example.scannr.RewardFragment;
import com.example.scannr.CaptureFragment;
import com.example.scannr.FamilyFragment;
import com.example.scannr.SettingsFragment;
import com.example.scannr.R;
import com.example.scannr.family_manager.FamilyManager;
import com.example.scannr.notifications.Notifications;
import com.example.scannr.purchase_history_manager.PurchaseHistoryManager;
import com.example.scannr.receipt_manager.ReceiptManager;
import com.example.scannr.registration_login.MainActivity;
import com.example.scannr.rewards.Rewards;
import com.example.scannr.settings.Settings;


import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.os.Bundle;
import android.view.MenuItem;

public class Dashboard extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener  {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

    }
    HomeFragment homeFragment = new HomeFragment();
    RewardFragment rewardFragment = new RewardFragment();
    CaptureFragment captureFragment = new CaptureFragment();
    FamilyFragment familyFragment = new FamilyFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_layout, homeFragment).commit();
                return true;

            case R.id.rewards:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_layout, rewardFragment).commit();
                return true;

            case R.id.capture:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_layout, captureFragment).commit();
                return true;

            case R.id.family:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_layout, familyFragment).commit();
                return true;

            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_layout, settingsFragment).commit();
                return true;
        }
//         return false;
        else{
            greeting = "Hello User";
        }
        welcomeMessage.setText(greeting);
    }

    private void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
        LayoutInflater inflater = this.getLayoutInflater();
        // Add the buttons
        builder.setView(inflater.inflate(R.layout.dialog_logout, null))
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    // Sign Out
                    mAuth.signOut();
                    finish();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    dialog.cancel();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

//public class Dashboard extends AppCompatActivity implements View.OnClickListener {
//    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//    private Button settings, familyManager, purchaseHistoryManager,
//    receiptManager, rewards, notifications, logOut;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dashboard);
//
//        welcomeMessage();
//
//        settings = findViewById(R.id.settings_routing);
//        settings.setOnClickListener(this);
//
//        familyManager = findViewById(R.id.family_manager_routing);
//        familyManager.setOnClickListener(this);
//
//        purchaseHistoryManager = findViewById(R.id.purchase_history_manager_routing);
//        purchaseHistoryManager.setOnClickListener(this);
//
//        receiptManager = findViewById(R.id.receipt_uploader_routing);
//        receiptManager.setOnClickListener(this);
//
//        rewards = findViewById(R.id.rewards_routing);
//        rewards.setOnClickListener(this);
//
//        notifications = findViewById(R.id.notification_routing);
//        notifications.setOnClickListener(this);
//
//        logOut = findViewById(R.id.logoutButton);
//        logOut.setOnClickListener(this);
//
//
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch(v.getId()){
//            case R.id.logoutButton:
//                logout();
//                break;
//            case R.id.settings_routing:
//                startActivity(new Intent(Dashboard.this, Settings.class));
//                break;
//            case R.id.notification_routing:
//                startActivity(new Intent(Dashboard.this, Notifications.class));
//                break;
//            case R.id.family_manager_routing:
//                startActivity(new Intent(Dashboard.this, FamilyManager.class));
//                break;
//            case R.id.purchase_history_manager_routing:
//                startActivity(new Intent(Dashboard.this, PurchaseHistoryManager.class));
//                break;
//            case R.id.rewards_routing:
//                startActivity(new Intent(Dashboard.this, Rewards.class));
//                break;
//            case R.id.receipt_uploader_routing:
//                startActivity(new Intent(Dashboard.this, ReceiptManager.class));
//                break;
//        }
//    }
//    private void welcomeMessage(){
//        String greeting;
//        FirebaseUser user = mAuth.getCurrentUser();
//
//        TextView welcomeMessage = findViewById(R.id.welcomeMessage);
//        if (user != null){
//            String dName = user.getDisplayName();
//            greeting = "Hello " + dName;
//        }
//        else{
//            greeting = "Hello User";
//        }
//        welcomeMessage.setText(greeting);
//    }
//
//    private void logout(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
//        LayoutInflater inflater = this.getLayoutInflater();
//        // Add the buttons
//        builder.setView(inflater.inflate(R.layout.dialog_logout, null))
//                .setPositiveButton(R.string.ok, (dialog, id) -> {
//                    // Sign Out
//                    mAuth.signOut();
//                    startActivity(new Intent(Dashboard.this, MainActivity.class));
//                })
//                .setNegativeButton(R.string.cancel, (dialog, id) -> {
//                    dialog.cancel();
//                });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//}