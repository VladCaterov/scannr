package com.example.scannr.authentication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scannr.MainActivity;
import com.example.scannr.R;
import com.example.scannr.dashboard.Dashboard;

import com.example.scannr.database.DatabaseHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class LoginUser extends AppCompatActivity implements View.OnClickListener {
    Validation validate = new Validation();
    DatabaseHandler databaseHandler = new DatabaseHandler();
    private EditText editEmail, editPassword;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FloatingActionButton flb = findViewById(R.id.backButtonLogin);
        flb.setOnClickListener(this);

        editEmail= findViewById(R.id.emailLogin);
        editPassword= findViewById(R.id.passwordLogin);

        Button logIn = findViewById(R.id.buttonLogin);
        logIn.setOnClickListener(this);

        Button register = findViewById(R.id.registerLogin);
        register.setOnClickListener(this);

        TextView forgotPassword = findViewById(R.id.forgotPasswordLogin);
        forgotPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();

        if (viewID == R.id.backButtonLogin){
            finish();
        }
        else if (viewID == R.id.buttonLogin){
            userLogin();
        }
        else if (viewID == R.id.registerLogin){
            finish();
            Intent intent = new Intent(LoginUser.this,RegisterUser.class);
            if (validate.validateEmail(editEmail.getText().toString()) && !editEmail.getText().toString().equals("")){
                Log.d(TAG, "Email Passed Successfully");
                intent.putExtra("email", editEmail.getText().toString());
            }
            startActivity(intent);
        }
        else if (viewID == R.id.forgotPasswordLogin){
            forgotPassword(editEmail.getText().toString().trim());
        }
    }
    private void userLogin() {

        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        // EMAIL/PASSWORD validation
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
            editPassword.setError("Minimum password length is 6 char");
            editPassword.requestFocus();
            return;
        }
        // CHECK IF THERE IS A USER LOGGED IN
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }
        //CHECK IF A CHILD HAS BEEN CREATED
//        db.collection("recentlyCreatedChildren")
//                .whereEqualTo("email", editEmail.getText().toString().trim())
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
//                        if (snapshot.exists()){
//                            mAuth.createUserWithEmailAndPassword(email, password);
//                            DocumentReference createdChild = db.collection("users").document(snapshot.getId());
//                            DocumentReference toUsers = db.collection("users").document(Objects.requireNonNull(mAuth.getUid()));
//                            databaseHandler.moveFirestoreDocument(createdChild,toUsers);
//                            overridePendingTransition(0, 0);
//                            finish();
//                            overridePendingTransition(0, 0);
//                            startActivity(getIntent());
//                        }
//                    }
//                });
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if(task.isSuccessful()) {
                final boolean[] isParent = new boolean[1];
                //CHECK IF A USER HAS BEEN DELETED
                db.collection("deletedUsers")
                        .document(user.getUid())
                        .get()
                        .addOnSuccessListener(d -> {
                            if(d.exists()){
                                Log.d(TAG, "ALL DATA: " + d.getData());
                                Log.d(TAG, "User is parent: " + d.get("isParent"));
                                db.collection("deletedUsers").document(user.getUid()).delete();
                                user.delete();
                                overridePendingTransition(0, 0);
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                if (Objects.equals(d.get("isParent"), false)){
                                    Toast.makeText(LoginUser.this, "Your parent has deleted your account. Goodbye!", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(LoginUser.this, "Your account has been deleted. Goodbye!", Toast.LENGTH_LONG).show();
                                }
                                Log.d(TAG, "Deleted Successfully");
                            }});
                db.collection("users")
                        .document(user.getUid())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            try {
                                isParent[0] = (boolean) documentSnapshot.get("isParent");
                                MainActivity.isParent = isParent[0];
                                Log.d(TAG, "isParent: " + MainActivity.isParent);
                                if (mAuth.getCurrentUser().isEmailVerified()) {
                                    Intent dashboardIntent = new Intent(LoginUser.this, Dashboard.class);
                                    dashboardIntent.putExtra("fName", Objects.requireNonNull(documentSnapshot.get("fName")).toString());
                                    dashboardIntent.putExtra("lName", Objects.requireNonNull(documentSnapshot.get("lName")).toString());

                                    dashboardIntent.putExtra("userID", user.getUid());
                                    dashboardIntent.putExtra("displayName", user.getDisplayName());
                                    dashboardIntent.putExtra("email", user.getEmail());
                                    dashboardIntent.putExtra("phoneNumber", user.getPhoneNumber());
                                    startActivity(dashboardIntent);
                                } else {
                                    mAuth.getCurrentUser().sendEmailVerification();
                                    Toast.makeText(LoginUser.this, "Check your email to verify your account!",
                                            Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "Verification Sent.");

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
            } else {
                Toast.makeText(LoginUser.this,"Failed to login! No account found.",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void forgotPassword(String email){

        View view = getLayoutInflater().inflate(R.layout.dialog_forgot_password,null, true);
        EditText emailToSend = view.findViewById(R.id.emailForgotPass);
        if (validate.validateEmail(email)){
            emailToSend.setText(email);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setView(view)
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    String sendEmail = emailToSend.getText().toString().trim();
                    // SEND FORGET PASSWORD EMAIL
                    if (validate.isEmptyEmail(sendEmail)) {
                        Toast.makeText(LoginUser.this, "Email Failed To Send:\nAn Email Is Required",
                            Toast.LENGTH_LONG).show();
                    }
                    if (!validate.validateEmail(sendEmail)) {
                        Toast.makeText(LoginUser.this, "Email Failed To Send:\nPlease Enter a Valid Email",
                            Toast.LENGTH_LONG).show();
                    }
                    else{
                        String message = "Check your " + sendEmail + " email to reset your password!";
                        // QUERY IF EMAIL EXISTS IN SYSTEM
                        db.collection("users")
                            .whereEqualTo("email", sendEmail)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (Objects.equals(document.getData().get("email"), sendEmail)){
                                            mAuth.sendPasswordResetEmail(sendEmail);
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
