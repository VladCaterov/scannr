package com.example.scannr.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.scannr.R;
import com.example.scannr.receipts.ReceiptAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends ListFragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public HomeFragment() {
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            ListView receiptView = getListView();
            TextView totalSpending = view.findViewById(R.id.totalSpending);

            ArrayList<String> businessList = new ArrayList<>();
            ArrayList<String> dateList = new ArrayList<>();
            ArrayList<String> totalList = new ArrayList<>();


            ReceiptAdapter adapter = new ReceiptAdapter(getActivity(), businessList, dateList, totalList);
            receiptView.setAdapter(adapter);

            // check database for "receipts" with userId of current user
            db.collection("receipts")
                    .whereEqualTo("userId", mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(receipt -> {
                        for (int i = 0; i < receipt.getResult().size(); i++) {
                            businessList.add(receipt.getResult().getDocuments().get(i).get("businessName").toString());
                            dateList.add(receipt.getResult().getDocuments().get(i).get("date").toString());
                            totalList.add("$" + receipt.getResult().getDocuments().get(i).get("receiptTotal").toString());
                            adapter.notifyDataSetChanged();
                        }
                    });


        //calculate total from receiptTotal in receipts from database
            db.collection("receipts")
                    .whereEqualTo("userId", mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        double total = 0;
                        for (int i = 0; i < task.getResult().size(); i++) {
                            total += Double.parseDouble(task.getResult().getDocuments().get(i).get("receiptTotal").toString());
                        }
                        totalSpending.setText(mAuth.getCurrentUser().getDisplayName() + "'s Total Spending: $" + String.format("%.2f", total));
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}