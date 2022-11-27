package com.example.scannr.registration_login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;

import com.example.scannr.R;
import com.example.scannr.Validation;
import com.example.scannr.dashboard.Dashboard;
import com.example.scannr.family_manager.FamilyManager;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{
    Validation validate = new Validation();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText editFirstName, editLastName, editEmail, editPassword,
        editPhoneNumber, editDOB, editMiddleInitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        editFirstName = findViewById(R.id.firstNameRegister);
        editMiddleInitial = findViewById(R.id.middleInitialRegister);
        editLastName = findViewById(R.id.lastNameRegister);
        editPhoneNumber = findViewById(R.id.phoneRegister);

        editPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        editDOB = findViewById(R.id.dobRegister);
        editDOB.setOnClickListener(this);
        editEmail= findViewById(R.id.emailRegister);
        editPassword= findViewById(R.id.passwordRegister);

        Button register = findViewById(R.id.registerButton);
        register.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.registerButton) {
            registerUser();
        }
    }
    private void registerUser(){
        String fName = editFirstName.getText().toString().trim();
        String mInitial = editMiddleInitial.getText().toString().trim();
        String lName = editLastName.getText().toString().trim();
        String dob = editDOB.getText().toString().trim();
        String phoneNumber = editPhoneNumber.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();


        Map<String, Object> user = new HashMap<>();
        user.put("fName", fName);
        user.put("mInitial", mInitial);
        user.put("lName", lName);
        user.put("dob", dob);
        user.put("email", email);
        user.put("phoneNumber", phoneNumber);
        user.put("children", new ArrayList<>());
        user.put("numChildren", 0);
        user.put("numRewards", 0);


        //path validation
        if (validate.isEmptyFirstName(fName)) {
            editFirstName.setError("First Name is required!");
            editFirstName.requestFocus();
            return;
        }
        //path validation
        if (validate.isEmptyLastName(lName)) {
            editLastName.setError("First Name is required!");
            editLastName.requestFocus();
            return;
        }
        //path validation
        if (validate.isEmptyPhoneNumber(phoneNumber)) {
            editPhoneNumber.setError("First Name is required!");
            editPhoneNumber.requestFocus();
            return;
        }
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
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        // If register fails, display a message to the user.
                        Toast.makeText(RegisterUser.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        // CREATE USER IN DATABASE FOR ADDITIONAL INFORMATION
                        FirebaseUser user1 = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fName + " " + mInitial + ". " + lName)
                                .build();
                        assert user1 != null;
                        user1.updateProfile(profileUpdates);

                        db.collection("users")
                                .document(user1.getUid())
                                .set(user);

                        startActivity(new Intent(RegisterUser.this, MainActivity.class));
                    }
                });

    }


}