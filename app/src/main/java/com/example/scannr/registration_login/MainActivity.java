package com.example.scannr.registration_login;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editEmail, editPassword;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView register = findViewById(R.id.registerLogin);
        register.setOnClickListener(this);

        Button signIn = findViewById(R.id.buttonLogin);
        signIn.setOnClickListener(this);

        editEmail= findViewById(R.id.emailLogin);
        editPassword= findViewById(R.id.passwordLogin);

        TextView forgotPassword = findViewById(R.id.forgotPasswordLogin);
        forgotPassword.setOnClickListener(this);

        mAuth= FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.registerLogin:
                registerUserScreen();
                break;
            case R.id.buttonLogin:
                userLogin();
                break;
            case R.id.forgotPasswordLogin:
                forgotPassword();
                break;
        }
    }
    private void userLogin() {
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
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new
               OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult>task){
                       if(task.isSuccessful()) {
                           FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                           if (user.isEmailVerified()) {
                               startActivity(new Intent(MainActivity.this, Dashboard.class));
                           } else {
                               user.sendEmailVerification();
                               Toast.makeText(MainActivity.this, "Check your email to verify your account!",
                                       Toast.LENGTH_LONG).show();
                           }
                       }
                       else
                       {
                           Toast.makeText(MainActivity.this,"Failed to login! Please check credentials",Toast.LENGTH_LONG).show();
                       }
                   }
               });
    }

    private void registerUserScreen(){

        startActivity(new Intent(this,RegisterUser.class));
    }
    private void forgotPassword(){
        FirebaseUser user = mAuth.getCurrentUser();

    }

}
