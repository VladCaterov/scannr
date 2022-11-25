package com.example.scannr.registration_login;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scannr.Validation;
import com.example.scannr.R;
import com.example.scannr.dashboard.Dashboard;
import com.example.scannr.family_manager.FamilyManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Validation validate = new Validation();
    private EditText editEmail, editPassword, emailToSend;
    private TextView register, forgotPassword;
    private Button logIn;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editEmail= findViewById(R.id.emailLogin);
        editPassword= findViewById(R.id.passwordLogin);
        logIn = findViewById(R.id.buttonLogin);
        logIn.setOnClickListener(this);

        register = findViewById(R.id.registerLogin);
        register.setOnClickListener(this);

        forgotPassword = findViewById(R.id.forgotPasswordLogin);
        forgotPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.buttonLogin:
                userLogin();
//                startActivity(new Intent(MainActivity.this, FamilyManager.class));

                break;
            case R.id.registerLogin:
                registerUserScreen();
                break;
            case R.id.forgotPasswordLogin:
                forgotPassword();
                break;
        }
    }
    private void userLogin() {
        mAuth= FirebaseAuth.getInstance();

        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        //path validation
        if (validate.isEmptyEmail(email)) {
            editEmail.setError("Email is required!");
            editEmail.requestFocus();
            return;
        }
        if (!validate.validateEmail(email)) {
            editEmail.setError("Please enter a valid email!");
            editEmail.requestFocus();
            return;
        }
        if (validate.isEmptyPassword(password)) {
            editPassword.setError("Password required");
            editPassword.requestFocus();
            return;
        }
        if (validate.validatePassword(password)) {
            editPassword.setError("Min password length is 6 char");
            editPassword.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
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
        });
    }

    private void registerUserScreen(){

        startActivity(new Intent(MainActivity.this,RegisterUser.class));
    }
    private void forgotPassword(){
        View view = getLayoutInflater().inflate(R.layout.dialog_forgot_password,null, true);
        emailToSend = view.findViewById(R.id.emailForgotPass);
        String email = emailToSend.getText().toString().trim();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Add the buttons
        builder.setView(view)

                .setPositiveButton(R.string.ok, (dialog, id) -> {

                    // SEND FORGET PASSWORD EMAIL
                    if (validate.isEmptyEmail(email)) {
                        Toast.makeText(MainActivity.this, "Email Failed To Send:\nAn Email Is Required",
                                Toast.LENGTH_LONG).show();
                    }
                    else if (!validate.validateEmail(email)) {
                        Toast.makeText(MainActivity.this, "Email Failed To Send:\nPlease Enter a Valid Email",
                                Toast.LENGTH_LONG).show();
                    }
                    else{
                        String message = "If you have an account, check your" + email + "email to reset your password!";
                        // QUERY IF EMAIL EXISTS IN SYSTEM
                        db.collection("users")
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (document.get("email") == email){
                                                mAuth.sendPasswordResetEmail(email);
                                            }
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                });
                        Toast.makeText(MainActivity.this, message,
                                Toast.LENGTH_LONG).show();
                    }

                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
