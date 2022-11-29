package com.example.scannr.registration_login;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scannr.Validation;
import com.example.scannr.R;
import com.example.scannr.dashboard.Dashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Validation validate = new Validation();
    private EditText editEmail;
    private EditText editPassword;
    private FirebaseAuth mAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(this);

        Button register_button = findViewById(R.id.register_button);
        register_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.login_button:
                loginUserScreen();
                break;

            case R.id.register_button:
                registerUserScreen();
                break;
        }
    }

    private void registerUserScreen() {
        startActivity(new Intent(MainActivity.this,RegisterUser.class));
    }
    private void loginUserScreen() {
        startActivity(new Intent(MainActivity.this,LoginUser.class));
    }
}
