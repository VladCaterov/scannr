package com.example.scannr.registration_login;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.ui.AppBarConfiguration;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.scannr.R;
import com.example.scannr.Validation;
import com.example.scannr.databinding.ActivityDashboardBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    Validation validate = new Validation();

    private Button register;
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

        register = findViewById(R.id.registerButton);
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
                })
                .addOnSuccessListener(this, task ->{
                    FirebaseUser user1 = mAuth.getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    assert user1 != null;
                    db.collection("users")
                            .document(user1.getUid())
                            .set(user);
                    startActivity(new Intent(RegisterUser.this,Dashboard.class));

                });
    }
//
//    private void dateOfBirth(){
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUser.this);
//        LayoutInflater inflater = this.getLayoutInflater();
//
//        // Add the buttons
//        builder.setView(inflater.inflate(R.layout.dialog_dob, null))
//                .setPositiveButton(R.string.ok, (dialog, id) -> {
////                    int month = date.getMonth();
////                    int year = date.getYear();
//                })
//                .setNegativeButton(R.string.cancel, (dialog, id) -> {
//                    // GO BACK
//                    // NOTHING TO DO
//                    dialog.cancel();
//                });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }

}