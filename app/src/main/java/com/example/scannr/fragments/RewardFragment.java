package com.example.scannr.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.scannr.MainActivity;
import com.example.scannr.R;
import com.example.scannr.family.ChildAccountManager;
import com.example.scannr.rewards.CreateRewardForm;

public class RewardFragment extends Fragment {
    ChildRewardFragment childRewardFragment = new ChildRewardFragment();

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
        if (!MainActivity.isParent){
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_layout, childRewardFragment).commit();
        }
        else{
            Button createRewardButton = requireView().findViewById(R.id.createRewardButton);
            createRewardButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), CreateRewardForm.class)));
        }
    }
}