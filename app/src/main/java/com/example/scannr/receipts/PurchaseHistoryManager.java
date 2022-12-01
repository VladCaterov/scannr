package com.example.scannr.receipts;

import android.util.Log;
import android.widget.Toast;

import com.example.scannr.authentication.RegisterUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PurchaseHistoryManager {
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    static String TAG = "PurchaseHistoryManager";

    public static Map<String, Object> createReceipt(String userId, String businessName, String date, Float receiptTotal) {
        Map<String, Object> receipt = new HashMap<>();
        receipt.put("userId", userId);
        receipt.put("businessName", businessName);
        receipt.put("date", date);

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        receipt.put("receiptTotal", df.format(receiptTotal));
        return receipt;
    }

    public static void addReceipt(String businessName, String date, Float receiptTotal) {
        db.collection("receipts")
                .add(createReceipt(Objects.requireNonNull(mAuth.getUid()), businessName, date, receiptTotal))
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "addReceipt: DocumentSnapshot added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "addReceipt: Error adding document" + e);
                });
    }


}
