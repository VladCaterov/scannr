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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.scannr.MainActivity;
import com.example.scannr.R;
import com.example.scannr.family.ChildAccountManager;
import com.example.scannr.family.FamilyListChildAdapter;
import com.example.scannr.family.FamilyListMemberAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class ChildFamilyFragment extends Fragment {

    public ChildFamilyFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_family_child, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setMembers();

    }

    public void setMembers(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TextView member = requireView().findViewById(R.id.memberTextView);
        ListView memberList = requireActivity().findViewById(R.id.memberListView);
        ArrayList<String> arrayListMembers = new ArrayList<>();
        FamilyListMemberAdapter familyListMemberAdapter = new FamilyListMemberAdapter(getActivity(), arrayListMembers);
        memberList.setAdapter(familyListMemberAdapter);


        db.collection("users")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
//                        if (Objects.requireNonNull(documentSnapshot.get("numChildren")).toString().equals("1")){
//                            String text = "PARE";
//                            numChildrenMessage.setText(text);
//                        }
//                        else{
//                            String text = "You Have " + documentSnapshot.get("numChildren") + " Children";
//                            numChildrenMessage.setText(text);
//                        }
                        String text = "PARENT: " + documentSnapshot.get("parentName");
                        arrayListMembers.add(text);
                        familyListMemberAdapter.notifyDataSetChanged();

                    } catch (Exception e){
                        System.out.println(e);
                    }
                    Log.d(TAG, "numChildren: "+ documentSnapshot.get("numChildren"));
                });
    }
}
