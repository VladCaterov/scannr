package com.example.scannr.receipts;

import android.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.scannr.authentication.RegisterUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

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

    public static void sortContents(String text, FragmentActivity activity) { // TODO: move to PHM
        String[] cache = new String[3];
        Arrays.fill(cache, "");
//        cache[1] = "2022.11.29"; // TODO: REMOVE
//        cache[2] = "2.09"; // TODO: REMOVE
        ArrayList<Float> moneyList = new ArrayList<>();

        StringTokenizer st = new StringTokenizer(text.trim());
        while (st.hasMoreTokens()) { // We need 3 things: Business name (0), Date (1), Receipt Total (2)
            String currToken = st.nextToken();
            if (currToken.contains("University") || currToken.contains("BON") || currToken.contains("Walmart")) { // Business name
                cache[0] = currToken;
            } else if (currToken.contains("/") || currToken.contains("-") && (currToken.matches(".*/.*/.*") || currToken.matches(".*-.*-.*"))) { // Date
                cache[1] = currToken;
            } else if ((currToken.contains("$") || currToken.contains(".")) && currToken.replaceAll("[^\\d.]", "").matches("[-+]?[0-9]*\\.?[0-9]+")) { // Receipt Total (with $)
                moneyList.add(Float.parseFloat(currToken.replaceAll("[^\\d.]", "")));
            }
//            else if (currToken.contains(".")) {// (with .)
//                // TODO: Handle decimals
//            }
        }

        if (moneyList.size() > 0) {
            //traverse moneyList and find the biggest value and put it in cache[3]
            float max = 0;
            int index = 0;
            for (int i = 0; i < moneyList.size(); i++) {
                if (moneyList.get(i) > max) {
                    max = moneyList.get(i);
                    index = i;
                }
            }

            //set cache[3] to the biggest value
            cache[2] = moneyList.get(index).toString() ;
        }

        // alert dialog builder to show business name, date, and total
        final EditText bNameOutputField = new EditText(activity);
        final EditText dateField = new EditText(activity);
        final EditText totalField = new EditText(activity);
        bNameOutputField.setText(cache[0]);
        dateField.setText(cache[1]);
        totalField.setText(cache[2]);

        // add hints
        bNameOutputField.setHint("Business Name");
        dateField.setHint("Date");
        totalField.setHint("Receipt Total");

        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(bNameOutputField);
        linearLayout.addView(dateField);
        linearLayout.addView(totalField);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Receipt Information");
//        builder.setMessage("Business Name: " + cache[0] + "\nDate: " + cache[1] + "\nTotal: " + cache[2]);
        builder.setView(linearLayout);
        builder.setPositiveButton("Save", (dialog, id) -> {
            cache[0] = bNameOutputField.getText().toString();
            cache[1] = dateField.getText().toString();
            cache[2] = totalField.getText().toString();

            // add to PHM
            try {
                PurchaseHistoryManager.addReceipt(cache[0], cache[1], Float.parseFloat(cache[2]));
                Toast.makeText(activity, "Receipt added successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "addReceipt: Error adding document" + e);
                Toast.makeText(activity, "Error adding receipt", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setCancelable(true);
        builder.setNegativeButton("Cancel",
                (dialog, id) -> dialog.cancel());
        builder.create().show();
    }
}
