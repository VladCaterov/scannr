package com.example.scannr.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.scannr.MainActivity;
import com.example.scannr.R;
import com.example.scannr.family.ChildAccountManager;
import com.example.scannr.family.FamilyListChildAdapter;
import com.example.scannr.rewards.CreateRewardForm;
import com.example.scannr.rewards.RewardListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RewardFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public RewardFragment() {
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reward, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        TextView rewardsHeaderText = requireView().findViewById(R.id.rewardsHeaderText);
        db.collection("users")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        if (Objects.requireNonNull(documentSnapshot.get("numChildren")).toString().equals("0")){
                            String text = "You Have 0 Children. Please create a child account";
                            rewardsHeaderText.setText(text);
                            Button createRewardButton = requireView().findViewById(R.id.createRewardButton);
                            createRewardButton.setEnabled(false);
                        }
                        else{
                            Button createRewardButton = requireView().findViewById(R.id.createRewardButton);
                            String text = "Your Children Need Rewards. Please Create a Reward";
                            rewardsHeaderText.setText(text);
                            createRewardButton.setEnabled(true);
                            createRewardButton.setOnClickListener(v -> Toast.makeText(getContext(), "CREATE REWARD", Toast.LENGTH_SHORT));
                        }

                    } catch (Exception e){
                        System.out.println(e);
                    }
                });
        setCreatedRewards();

    }

    private void setCreatedRewards(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ListView rewardList = requireActivity().findViewById(R.id.createdRewardsListView);
        ArrayList<String> childNameArrayList = new ArrayList<>();
        ArrayList<String> statusArraysList = new ArrayList<>();

        RewardListAdapter rewardListAdapter = new RewardListAdapter(getActivity(),childNameArrayList,statusArraysList)  ;
        rewardList.setAdapter(rewardListAdapter);

        CollectionReference usersRef = db.collection("rewards");
        usersRef
                .whereEqualTo("creatorUID", mAuth.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for(DocumentSnapshot documentSnapshot : task.getResult()){
                            String fName = documentSnapshot.getString("fName");
                            String lName = documentSnapshot.getString("lName");
                            String name = fName + " " + lName;
                            childNameArrayList.add(name);
                            statusArraysList.add(documentSnapshot.get("status").toString());
                            rewardListAdapter.notifyDataSetChanged();
                        }
                    }
                });

    }
}