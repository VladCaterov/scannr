package com.example.scannr.family_manager;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.scannr.Validation;
import com.example.scannr.R;
import com.example.scannr.dashboard.Dashboard;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class FamilyManager extends AppCompatActivity implements View.OnClickListener{
    Validation validate = new Validation();
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private int numChildren;
    private TextView numChildrenMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_manager);
        FloatingActionButton flb = findViewById(R.id.backButtonFamilyManager);
        flb.setOnClickListener(this);

        setNumChildrenText();
        setChildUsers();

        FloatingActionButton addChildBtn = findViewById(R.id.buttonAddChild);
        addChildBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backButtonFamilyManager:
                finish();
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
                .document(Objects.requireNonNull(mAuth.getUid()))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String text = "You Have " + documentSnapshot.get("numChildren") + " Children";
                    numChildrenMessage.setText(text);
                    Log.d(TAG, "numChildren: "+ documentSnapshot.get("numChildren"));
                });
    }
    private void setChildUsers(){
        class ChildListItemAdapter extends ArrayAdapter {
            public ChildListItemAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
                super(context, resource, objects);
            }

            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup container) {
//                if (convertView == null) {
//                    convertView = getLayoutInflater().inflate(R.layout.activity_family_manager_list_child, container, true);
//                }
                return getLayoutInflater().inflate(R.layout.activity_family_manager_list_child, container, true);
            }

        }
        ArrayList<String> arrayList;
        arrayList = new ArrayList<>();
        ChildListItemAdapter childListItemAdapter = new ChildListItemAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        ListView childList = findViewById(R.id.childrenListView);
        childList.setAdapter(childListItemAdapter);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        usersRef
                .whereEqualTo("parentUID", mAuth.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        String name;
                        for(DocumentSnapshot documentSnapshot : task.getResult()){
                            String fName = documentSnapshot.getString("fName");
                            String lName = documentSnapshot.getString("lName");
                            name = fName + lName;
                            arrayList.add(name);
                            childListItemAdapter.notifyDataSetChanged();

                        }
                    }
                });
    }
}