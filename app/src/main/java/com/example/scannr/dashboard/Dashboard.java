package com.example.scannr.dashboard;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

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


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Dashboard extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button settings, familyManager, purchaseHistoryManager,
    receiptManager, rewards, notifications, logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        welcomeMessage();

        settings = findViewById(R.id.settings_routing);
        settings.setOnClickListener(this);

        familyManager = findViewById(R.id.family_manager_routing);
        familyManager.setOnClickListener(this);

        purchaseHistoryManager = findViewById(R.id.purchase_history_manager_routing);
        purchaseHistoryManager.setOnClickListener(this);

        receiptManager = findViewById(R.id.receipt_uploader_routing);
        receiptManager.setOnClickListener(this);

        rewards = findViewById(R.id.rewards_routing);
        rewards.setOnClickListener(this);

        notifications = findViewById(R.id.notification_routing);
        notifications.setOnClickListener(this);

        logOut = findViewById(R.id.logoutButton);
        logOut.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.logoutButton:
                logout();
                break;
            case R.id.settings_routing:
                startActivity(new Intent(Dashboard.this, Settings.class));
                break;
            case R.id.notification_routing:
                startActivity(new Intent(Dashboard.this, Notifications.class));
                break;
            case R.id.family_manager_routing:
                startActivity(new Intent(Dashboard.this, FamilyManager.class));
                break;
            case R.id.purchase_history_manager_routing:
                startActivity(new Intent(Dashboard.this, PurchaseHistoryManager.class));
                break;
            case R.id.rewards_routing:
                startActivity(new Intent(Dashboard.this, Rewards.class));
                break;
            case R.id.receipt_uploader_routing:
                startActivity(new Intent(Dashboard.this, ReceiptManager.class));
                break;
        }
    }
    private void welcomeMessage(){
        String greeting;
        FirebaseUser user = mAuth.getCurrentUser();

        TextView welcomeMessage = findViewById(R.id.welcomeMessage);
        if (user != null){
            String dName = user.getDisplayName();
            greeting = "Hello " + dName;
        }
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
                    startActivity(new Intent(Dashboard.this, MainActivity.class));
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    dialog.cancel();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}