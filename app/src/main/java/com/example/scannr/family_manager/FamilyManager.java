package com.example.scannr.family_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.scannr.R;
import com.example.scannr.registration_login.MainActivity;

public class FamilyManager extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_manager);


    }

    @Override
    public void onClick(View v) {
//        switch(v.getId()){
//
//            case R.id.buttonLogin:
////                userLogin();
//                startActivity(new Intent(MainActivity.this, FamilyManager.class));
//
//                break;
//
//        }
    }
}