package com.example.scannr;

//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//}

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

//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.FirebaseDatabase;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editEmail, editPassword;
//    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView register = findViewById(R.id.register);
        register.setOnClickListener(this);
        Button signIn = findViewById(R.id.loginButton);
        signIn.setOnClickListener(this);
        editEmail= findViewById(R.id.email);
        editPassword= findViewById(R.id.password);
        TextView forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.register:
//                startActivity(new Intent(this,RegisterUser.class));
                break;
            case R.id.loginButton:
                userLogin();
                break;
            case R.id.forgotPassword:
//                startActivity(new Intent(this,ForgotPassword.class));
                break;
        }
    }
    private void userLogin() {
          startActivity(new Intent(this,Dashboard.class));
//        String email=editEmail.getText().toString().trim();
//        String password=editPassword.getText().toString().trim();
//        //path validation
//        if(email.isEmpty()){
//            editEmail.setError("Email is required!");
//            editEmail.requestFocus();
//            return;
//        }
//        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
//            editEmail.setError("Please enter a valid email!");
//            editEmail.requestFocus();
//            return;
//        }
//        if(password.isEmpty()){
//            editPassword.setError("Password required");
//            editPassword.requestFocus();
//            return;
//        }
//        if(password.length()<6){
//            editPassword.setError("Min password length is 6 char");
//            editPassword.requestFocus();
//            return;
//        }
    }
}
