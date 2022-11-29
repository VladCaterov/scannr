package com.example.scannr.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.example.scannr.R;
import com.example.scannr.family.ChildAccountManager;

public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {
        // require a empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button addChildButton = requireView().findViewById(R.id.addChildButton);
        addChildButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), ChildAccountManager.class)));
    }
}