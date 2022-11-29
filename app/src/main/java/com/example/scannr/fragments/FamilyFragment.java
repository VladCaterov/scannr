package com.example.scannr.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.scannr.MainActivity;
import com.example.scannr.R;
import com.example.scannr.authentication.RegisterUser;
import com.example.scannr.authentication.Validation;
import com.example.scannr.family.ChildAccountManager;
import com.example.scannr.family.FamilyListItemAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.prefs.BackingStoreException;

public class FamilyFragment extends Fragment {
    ListView list;

    Validation validate = new Validation();
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    ChildFamilyFragment childFamilyFragment = new ChildFamilyFragment();

    public FamilyFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_family, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!MainActivity.isParent){
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_layout, childFamilyFragment).commit();
        }
        else{
            setNumChildrenText();
            setChildUsers();
            Button addChildButton = requireView().findViewById(R.id.addChildButton);
            addChildButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), ChildAccountManager.class)));
        }
    }

    private void setNumChildrenText() {
        TextView numChildrenMessage = requireView().findViewById(R.id.numChildren);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        if (Objects.requireNonNull(documentSnapshot.get("numChildren")).toString().equals("1")){
                            String text = "You Have 1 Child";
                            numChildrenMessage.setText(text);
                        }
                        else{
                            String text = "You Have " + documentSnapshot.get("numChildren") + " Children";
                            numChildrenMessage.setText(text);
                        }

                    } catch (Exception e){
                        System.out.println(e);
                    }
                    Log.d(TAG, "numChildren: "+ documentSnapshot.get("numChildren"));
                });
    }
    private void setChildUsers(){
        ListView childList = requireActivity().findViewById(R.id.childrenListView);
        ArrayList<String> arrayList = new ArrayList<>();
        FamilyListItemAdapter childListItemAdapter = new FamilyListItemAdapter(getActivity(), arrayList)  ;
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