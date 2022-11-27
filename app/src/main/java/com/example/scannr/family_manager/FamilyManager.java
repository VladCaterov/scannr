package com.example.scannr.family_manager;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scannr.Validation;
import com.example.scannr.R;
import com.example.scannr.dashboard.Dashboard;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class FamilyManager extends AppCompatActivity implements View.OnClickListener{
    Validation validate = new Validation();
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private int numChildren;
    private TextView numChildrenMessage;

    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> childArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_manager);
        FloatingActionButton flb = findViewById(R.id.backButtonFamilyManager);
        flb.setOnClickListener(this);

        FloatingActionButton addChildBtn = findViewById(R.id.buttonAddChild);
        addChildBtn.setOnClickListener(this);
        setNumChildrenText();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backButtonFamilyManager:
                startActivity(new Intent(FamilyManager.this, Dashboard.class));
                break;
            case R.id.buttonAddChild:
                startActivity(new Intent(FamilyManager.this, ChildAccountManager.class));
                break;

        }
    }
    private void setNumChildrenText(){
        numChildrenMessage = findViewById(R.id.numChildren);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(mAuth.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String text = "You Have " + documentSnapshot.get("numChildren") + " Children";
                    numChildrenMessage.setText(text);
                    Log.d(TAG, "numChildren: "+ documentSnapshot.get("numChildren"));
                });
    }
}