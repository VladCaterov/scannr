package com.example.scannr.family_manager;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.scannr.R;
import com.example.scannr.Validation;
import com.example.scannr.dashboard.Dashboard;
import com.example.scannr.registration_login.MainActivity;
import com.example.scannr.registration_login.RegisterUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChildAccountManager extends AppCompatActivity implements View.OnClickListener {

    private EditText editFirstName, editLastName, editEmail, editPassword, editBankRoutingNumber,
    editBankAccountNumber, editSpendingLimit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_child);

        FloatingActionButton backButton = findViewById(R.id.backButtonFamilyManagerCreateChild);
        backButton.setOnClickListener(this);

        editFirstName = findViewById(R.id.firstNameRegisterChild);
        editLastName = findViewById(R.id.lastNameRegisterChild);
        editEmail= findViewById(R.id.emailRegisterChild);
        editPassword= findViewById(R.id.passwordRegisterChild);
        editBankRoutingNumber = findViewById(R.id.bankRoutingNumberRegisterChild);
        editBankAccountNumber = findViewById(R.id.bankAccountNumberRegisterChild);
        editSpendingLimit = findViewById(R.id.spendingLimitRegisterChild);

        Button registerChildUseButton = findViewById(R.id.registerChildButton);
        registerChildUseButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backButtonFamilyManagerCreateChild:
                finish();
                break;
            case R.id.registerChildButton:
                registerChildUser();
                break;
        }
    }

    private void registerChildUser(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser parentUser = mAuth.getCurrentUser();
        assert parentUser != null;
        String parentUID = parentUser.getUid();

        String fName = editFirstName.getText().toString().trim();
        String lName = editLastName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String bRoutingNumber = editBankRoutingNumber.getText().toString().trim();
        String bAccountNumber = editBankAccountNumber.getText().toString().trim();
        String spendingLimit = editSpendingLimit.getText().toString().trim();

        Map<String, Object> child = new HashMap<>();
        child.put("fName", fName);
        child.put("lName", lName);
        child.put("email", email);
        child.put("isChild", true);
        child.put("routingNumber", bRoutingNumber);
        child.put("accountNumber", bAccountNumber);
        child.put("spendingLimit", spendingLimit);
        child.put("parentUID", parentUID);

        validateInput(fName, lName, email, password, bRoutingNumber, bAccountNumber, spendingLimit);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){

                        DocumentReference docRef = db.collection("users").document(parentUID);
                        docRef.update("numChildren", FieldValue.increment(1));

                        FirebaseUser childUser = mAuth.getCurrentUser();
                        docRef.update("children", FieldValue.arrayUnion(childUser.getUid()));

                        String displayName;
                        displayName = fName + " " + lName;
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName)
                                .build();
                        childUser.updateProfile(profileUpdates);
                        db.collection("users")
                                .document(childUser.getUid())
                                .set(child)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

                        startActivity(new Intent(ChildAccountManager.this, MainActivity.class));

                        Toast.makeText(ChildAccountManager.this,
                                "Successfully Created Child. Please Sign Back In",
                                Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(ChildAccountManager.this, "Unable to create child.",
                                Toast.LENGTH_SHORT).show();
                    }
                });



    }

    private void validateInput(String fName, String lName, String email, String password,
                               String bRoutingNumber, String bAccountNumber, String spendingLimit){
        Validation validate = new Validation();

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
            editPassword.setError("Password required!");
            editPassword.requestFocus();
            return;
        }
        if (validate.validatePassword(password)) {
            editPassword.setError("Min password length is 6 char!");
            editPassword.requestFocus();
            return;
        }
        if (validate.isEmptyBankAccountNumber(bAccountNumber)){
            editBankAccountNumber.setError("Bank account number required!");
            editBankAccountNumber.requestFocus();
            return;
        }
        if (!validate.validateBankAccountNumber(bAccountNumber)){
            editBankAccountNumber.setError("Min account number is 8 char!");
            editBankAccountNumber.requestFocus();
            return;
        }
        if (validate.isEmptyBankRoutingNumber(bRoutingNumber)){
            editBankRoutingNumber.setError("Bank routing number required!");
            editBankRoutingNumber.requestFocus();
            return;
        }
        if (validate.isEmptySpendingLimit(spendingLimit)){
            editSpendingLimit.setError("Spending Limit Must Be At Least 0");
            editSpendingLimit.requestFocus();
            return;
        }
    }
}
