package com.example.scannr.registration_login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.scannr.R;
import com.example.scannr.databinding.ActivityDashboardBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.scannr.dashboard.Dashboard;
import com.example.scannr.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{
    private AppBarConfiguration appBarConfiguration;
    private ActivityDashboardBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    private EditText editFirstName, editLastName, editEmail, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        editFirstName = findViewById(R.id.firstNameRegister);
        editLastName = findViewById(R.id.lastNameRegister);
        editEmail= findViewById(R.id.emailRegister);
        editPassword= findViewById(R.id.passwordRegister);

        Button register = findViewById(R.id.registerButton);
        register.setOnClickListener(this);
        mAuth= FirebaseAuth.getInstance();

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.registerButton:
                registerUser();
                break;
        }
    }
    private void registerUser(){
        String fName = editFirstName.getText().toString().trim();
        String lName = editLastName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        //path validation
        if (email.isEmpty()) {
            editEmail.setError("Email is required!");
            editEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Please enter a valid email!");
            editEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editPassword.setError("Password required");
            editPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editPassword.setError("Min password length is 6 char");
            editPassword.requestFocus();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(RegisterUser.this,Dashboard.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterUser.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}