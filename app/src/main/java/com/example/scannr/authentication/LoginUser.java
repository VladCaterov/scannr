package com.example.scannr.authentication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scannr.MainActivity;
import com.example.scannr.R;
import com.example.scannr.dashboard.Dashboard;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LoginUser extends AppCompatActivity implements View.OnClickListener {
    Validation validate = new Validation();
    private EditText editEmail, editPassword;
    private TextView forgotPassword;
    private Button logIn;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail= findViewById(R.id.emailLogin);
        editPassword= findViewById(R.id.passwordLogin);

        logIn = findViewById(R.id.buttonLogin);
        logIn.setOnClickListener(this);

        Button register = findViewById(R.id.registerLogin);
        register.setOnClickListener(this);

        FloatingActionButton flb = findViewById(R.id.backButtonRegister);
        flb.setOnClickListener(this);

        forgotPassword = findViewById(R.id.forgotPasswordLogin);
        forgotPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonLogin:
                userLogin();
                break;
            case R.id.backButtonRegister:
                finish();
                break;
            case R.id.registerLogin:
                finish();
                startActivity(new Intent(LoginUser.this, RegisterUser.class));
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
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            mAuth.signOut();
        }
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            FirebaseUser user1 = mAuth.getCurrentUser();

            if(task.isSuccessful()) {
                // CHECK IF USER IS A PARENT
                final boolean[] isParent = new boolean[1];
                db.collection("deletedUsers")
                        .document(mAuth.getUid())
                        .get()
                        .addOnSuccessListener(d -> {
                            if(d.exists()){
                                Log.d(TAG, "isParent21: " + d.get("isParent"));
                                db.collection("deletedUsers").document(user1.getUid()).delete();
                                user1.delete();
                                overridePendingTransition(0, 0);
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                Toast.makeText(LoginUser.this, "Your parent has deleted your account", Toast.LENGTH_LONG).show();
                            } else {
                                db.collection("users").document(user1.getUid())
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            try {
                                                isParent[0] = (boolean) documentSnapshot.get("isParent");
                                                MainActivity.isParent = isParent[0];
                                                Log.d(TAG, "isParent: " + MainActivity.isParent);
                                                if (mAuth.getCurrentUser().isEmailVerified()) {
                                                    startActivity(new Intent(LoginUser.this, Dashboard.class));

                                                } else {
                                                    mAuth.getCurrentUser().sendEmailVerification();
                                                    Toast.makeText(LoginUser.this, "Check your email to verify your account!",
                                                            Toast.LENGTH_LONG).show();
                                                    mAuth.signOut();
                                                    overridePendingTransition(0, 0);
                                                    finish();
                                                    overridePendingTransition(0, 0);
                                                    startActivity(getIntent());
                                                }
                                            } catch (Exception e) {
                                                System.out.println(e);
                                            }
                                        });
                            }

//                                db.collection("deletedUsers").document(mAuth.getUid()).delete();
//                                FirebaseAuth.getInstance().getCurrentUser().delete();
                        });
//                db.collection("users").document(user1.getUid())
//                        .get()
//                        .addOnSuccessListener(documentSnapshot -> {
//                            try {
//                                isParent[0] = (boolean) documentSnapshot.get("isParent");
//                                MainActivity.isParent = isParent[0];
//                                Log.d(TAG, "isParent: " + MainActivity.isParent);
//                                if (isParent[0]){
//                                    if (mAuth.getCurrentUser().isEmailVerified()) {
//                                        startActivity(new Intent(LoginUser.this, Dashboard.class));
//
//                                    } else {
//                                        mAuth.getCurrentUser().sendEmailVerification();
//                                        Toast.makeText(LoginUser.this, "Check your email to verify your account!",
//                                                Toast.LENGTH_LONG).show();
//                                        mAuth.signOut();
//                                        overridePendingTransition(0, 0);
//                                        finish();
//                                        overridePendingTransition(0, 0);
//                                        startActivity(getIntent());
//                                    }
//                                }
//                            } catch (Exception e) {
//                                System.out.println(e);
//                        }
//                    });
            }
            else
            {
                Toast.makeText(LoginUser.this,"Failed to login! Please check credentials",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void forgotPassword(){
        mAuth= FirebaseAuth.getInstance();

        View view = getLayoutInflater().inflate(R.layout.dialog_forgot_password,null, true);
        EditText emailToSend = view.findViewById(R.id.emailForgotPass);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Add the buttons
        builder.setView(view)
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    String email = emailToSend.getText().toString().trim();

                    // SEND FORGET PASSWORD EMAIL
                    if (validate.isEmptyEmail(email)) {
                        Toast.makeText(LoginUser.this, "Email Failed To Send:\nAn Email Is Required",
                            Toast.LENGTH_LONG).show();
                    }
                    if (!validate.validateEmail(email)) {
                        Toast.makeText(LoginUser.this, "Email Failed To Send:\nPlease Enter a Valid Email",
                            Toast.LENGTH_LONG).show();
                    }
                    else{
                        String message = "Check your " + email + " email to reset your password!";
                        // QUERY IF EMAIL EXISTS IN SYSTEM
                        db.collection("users")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (Objects.equals(document.getData().get("email"), email)){
                                            mAuth.sendPasswordResetEmail(email);
                                            Toast.makeText(LoginUser.this, message,Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                    }
                                    Toast.makeText(LoginUser.this, "No email found",Toast.LENGTH_LONG).show();
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            });
                    }

                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
