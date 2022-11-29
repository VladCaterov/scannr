package com.example.scannr.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.scannr.R;
import com.example.scannr.authentication.Validation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class FamilyFragment extends Fragment {
    Validation validate = new Validation();
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private int numChildren;
    private TextView numChildrenMessage;

    public FamilyFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_family, container, false);
//        FloatingActionButton flb = findViewById(R.id.backButtonFamilyManager);
//        flb.setOnClickListener(this);
//
//        setNumChildrenText();
//        setChildUsers();
//
//        FloatingActionButton addChildBtn = findViewById(R.id.buttonAddChild);
//        addChildBtn.setOnClickListener(this);
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.backButtonFamilyManager:
//                finish();
//                break;
//            case R.id.buttonAddChild:
//                startActivity(new Intent(FamilyManager.this, ChildAccountManager.class));
//                break;
//
//        }
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void setNumChildrenText() {
        numChildrenMessage = getView().findViewById(R.id.numChildren);

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
//        ArrayList<String> arrayList;
//        arrayList = new ArrayList<>();
//        ChildListItemAdapter childListItemAdapter = new ChildListItemAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
//        ListView childList = findViewById(R.id.childrenListView);
//        childList.setAdapter(childListItemAdapter);
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
//                        arrayList.add(name);
//                        childListItemAdapter.notifyDataSetChanged();

                    }
                }
            });
    }
}