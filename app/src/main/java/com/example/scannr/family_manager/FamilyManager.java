package com.example.scannr.family_manager;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.scannr.Validation;
import com.example.scannr.R;
import com.example.scannr.dashboard.Dashboard;
import com.example.scannr.family_manager.FamilyManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FamilyManager extends AppCompatActivity implements View.OnClickListener{
    Validation validate = new Validation();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> childArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LinearLayout linearLayout;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_manager);

//        list = findViewById(R.id.childListView);

        String parentUid = mAuth.getUid();
        assert parentUid != null;


        DocumentReference documentReferenceParent = db.collection("users").document(parentUid);
        documentReferenceParent.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + Objects.requireNonNull(document.getData()).get("numChildren"));
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
//
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        QuerySnapshot document = task.getResult();
//                        int numChildren = (int) document.get("numChildren");
//
//                        if (numChildren == 0){
//                            TextView textView = new TextView(this);
//                            textView.setText("NO CHILD ACCOUNTS");
//                        }
//                        else{
//
//                        }
//                        for (String uid : childArrayList) {
//                            if (document.get("email") == email){
//                                mAuth.sendPasswordResetEmail(email);
//                            }
//                        }
//                    } else {
//                        Log.d(TAG, "Error getting documents: ", task.getException());
//                    }
//                });
//        childArrayList = new ArrayList<>();
//        list = findViewById(R.id.childListView);
//        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, childArrayList);
//        list.setAdapter(adapter);
//
//        childArrayList.add("HELLO");
//        childArrayList.add("HELLO");
//        childArrayList.add("HELLO");
//        childArrayList.add("HELLO");
//        childArrayList.add("HELLO");
//        childArrayList.add("HELLO");
//        childArrayList.add("HELLO");
//        childArrayList.add("HELLO");
//        childArrayList.add("HELLO");
//        childArrayList.add("HELLO");
//        childArrayList.add("HELLO");
//        childArrayList.add("HELLO");
//        childArrayList.add("HELLO");
//
//
//        // next thing you have to do is check if your adapter has changed
//        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
//        switch(v.getId()){
//
//            case R.id.buttonAddChild:
////                userLogin();
//                startActivity(new Intent(FamilyManager.this, ChildAccountManager.class));
//
//                break;
//
//        }
    }
}