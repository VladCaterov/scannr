package com.example.scannr.dashboard;

import android.app.AlertDialog;
import android.os.Bundle;

import com.example.scannr.R;
import com.example.scannr.registration_login.MainActivity;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.scannr.databinding.ActivityDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Dashboard extends AppCompatActivity {
    private FirebaseAuth mAuth;
//    FirebaseUser user = mAuth.getCurrentUser();
    private AppBarConfiguration appBarConfiguration;
    private ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        welcomeMessage();
    }
    private TextView welcomeMessage(){
        TextView welcomeMessage = findViewById(R.id.welcomeMessage);
        String greeting = "Hello";
        welcomeMessage.setText(greeting);
        return welcomeMessage;
    }

    private void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
        LayoutInflater inflater = this.getLayoutInflater();
    }
}