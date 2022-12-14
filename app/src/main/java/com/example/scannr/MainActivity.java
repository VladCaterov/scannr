package com.example.scannr;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scannr.authentication.LoginUser;
import com.example.scannr.authentication.RegisterUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static boolean isParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_landing);

        Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(this);

        Button register_button = findViewById(R.id.register_button);
        register_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        if(viewID == R.id.login_button){
            loginUserScreen();
        }
        else if (viewID == R.id.register_button){
            registerUserScreen();
        }
    }

    private void registerUserScreen() {
        startActivity(new Intent(MainActivity.this, RegisterUser.class));
    }

    private void loginUserScreen() {
        startActivity(new Intent(MainActivity.this, LoginUser.class));
    }
}
