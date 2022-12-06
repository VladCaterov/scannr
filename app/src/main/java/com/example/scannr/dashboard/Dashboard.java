package com.example.scannr.dashboard;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.example.scannr.MainActivity;
import com.example.scannr.fragments.*;
import com.example.scannr.R;
//import com.example.scannr.family_manager.FamilyManager;


import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;


import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.MenuItem;

public class Dashboard extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener  {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_dashboard);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

    }
    HomeFragment homeFragment = new HomeFragment();
    RewardFragment rewardFragment = new RewardFragment();
    ChildRewardFragment childRewardFragment = new ChildRewardFragment();
    CaptureFragment captureFragment = new CaptureFragment();
    FamilyFragment familyFragment = new FamilyFragment();
    ChildFamilyFragment childFamilyFragment = new ChildFamilyFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.home){
            getSupportFragmentManager().beginTransaction().replace(R.id.home_layout, homeFragment).commit();
            return true;
        }

        else if (itemID == R.id.rewards){
            if(MainActivity.isParent){
                getSupportFragmentManager().beginTransaction().replace(R.id.home_layout, rewardFragment).commit();
            } else{
                getSupportFragmentManager().beginTransaction().replace(R.id.home_layout, childRewardFragment).commit();
            }
            return true;
        }

        else if (itemID == R.id.capture){
            getSupportFragmentManager().beginTransaction().replace(R.id.home_layout, captureFragment).commit();
            return true;
        }

        else if (itemID == R.id.family) {
            if (MainActivity.isParent) {
                getSupportFragmentManager().beginTransaction().replace(R.id.home_layout, familyFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.home_layout, childFamilyFragment).commit();
            }
            return true;
        }

        else if (itemID == R.id.settings){
            getSupportFragmentManager().beginTransaction().replace(R.id.home_layout, settingsFragment).commit();
            return true;
        }
     return false;
    }
}