package com.example.scannr.family_manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scannr.R;
import com.example.scannr.dashboard.Dashboard;

public class ChildAccountManager extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_child);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backButtonFamilyManagerCreateChild:
                startActivity(new Intent(ChildAccountManager.this, FamilyManager.class));
            case R.id.registerButton:
                registerUser();
        }
    }

    private void registerUser(){

    }
}
