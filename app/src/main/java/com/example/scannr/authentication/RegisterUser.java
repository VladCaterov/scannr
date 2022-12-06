package com.example.scannr.authentication;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.scannr.MainActivity;
import com.example.scannr.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{
    private final Validation validate = new Validation();
    private EditText editFirstName, editMiddleInitial, editLastName,
        editPhoneNumber, editDOB, editEmail, editPassword;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_register_user);

        FloatingActionButton flb = findViewById(R.id.backButtonRegister);
        flb.setOnClickListener(this);

        editFirstName = findViewById(R.id.firstNameRegister);
        editMiddleInitial = findViewById(R.id.middleInitialRegister);
        editLastName = findViewById(R.id.lastNameRegister);
        editPhoneNumber = findViewById(R.id.phoneRegister);
        editPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        editDOB = findViewById(R.id.dobRegister);
        editEmail= findViewById(R.id.emailRegister);
        editPassword= findViewById(R.id.passwordRegister);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String email = extras.getString("email");
            editEmail.setText(email);
            //The key argument here must match that used in the other activity
        }

        Button register = findViewById(R.id.registerButtonSubmit);
        register.setOnClickListener(this);

        Button login = findViewById(R.id.buttonLogin);
        login.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int viewID = v.getId();

        if (viewID == R.id.backButtonRegister){
            finish();
        }
        else if (viewID == R.id.registerButtonSubmit){
            registerUser();
        }
        else if (viewID == R.id.buttonLogin){
            finish();
            startActivity(new Intent(RegisterUser.this, LoginUser.class));
        }
    }
    private void registerUser(){
        String fName = editFirstName.getText().toString().trim();
        String mInitial = editMiddleInitial.getText().toString().trim().toUpperCase();
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
        user.put("rewards", new ArrayList<>());
        user.put("numRewards", 0);
        user.put("isParent", true);

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
        if (validate.isEmptyFirstName(fName)) {
            editFirstName.setError("First Name is required!");
            editFirstName.requestFocus();
            return;
        }
        if (validate.isEmptyLastName(lName)) {
            editLastName.setError("Last Name is required!");
            editLastName.requestFocus();
            return;
        }
        if (validate.isEmptyPhoneNumber(phoneNumber)) {
            editPhoneNumber.setError("Phone Number is required!");
            editPhoneNumber.requestFocus();
            return;
        }
        if (validate.isEmptyDateOfBirth(dob)) {
            editDOB.setError("Date of Birth is required!");
            editDOB.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // CREATE USER IN DATABASE FOR ADDITIONAL INFORMATION
                        FirebaseUser user1 = mAuth.getCurrentUser();
                        String displayName;
                        displayName = fName.toUpperCase() + " " + lName.toUpperCase();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName)
                                .build();
                        assert user1 != null;
                        user1.updateProfile(profileUpdates);
                        db.collection("users")
                                .document(user1.getUid())
                                .set(user)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                        finish();
                        startActivity(new Intent(RegisterUser.this, MainActivity.class));
                        Toast.makeText(RegisterUser.this, "Parent Registration Successful.",
                                Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        editEmail.setText("");
                        editPassword.setText("");
                        // If register fails, display a message to the user.
                        Toast.makeText(RegisterUser.this, "Registration failed. Account Already Exists",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}