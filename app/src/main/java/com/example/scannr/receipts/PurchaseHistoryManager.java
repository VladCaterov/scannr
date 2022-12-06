package com.example.scannr.receipts;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.scannr.R;
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
        String[] cache = new String[3]; // stores business name, date, and total
        Arrays.fill(cache, ""); // fill with empty strings (to replace)
        ArrayList<Float> moneyList = new ArrayList<>(); // stores all money value candidates

        StringTokenizer st = new StringTokenizer(text.trim(), System.lineSeparator());
        int countIncrement = 0; // used for businessName
        String biggestName = "";

        while (st.hasMoreTokens()) { // We need 3 things: Business name (0), Date (1), Receipt Total (2)
            String currToken = st.nextToken();

            System.out.println("currToken: " + currToken);

            int digits = 0;
            int characters = 0;
            for (int i = 0; i < currToken.length(); i++) {
                if (currToken.charAt(i) >= 48 && currToken.charAt(i) <= 57) {
                    digits++;
                } else {
                    characters++;
                }
            }

            if (countIncrement < 7 && !(currToken.contains("/") || currToken.contains(":") || currToken.contains("#") || currToken.contains(".")) &&
                    !(currToken == "Total" || currToken == "Tip" || currToken.contains("BLVD") || currToken.contains("feedback") || currToken.matches("^[0-9]{5}(?:-[0-9]{4})?$") || currToken.matches("d{1,3}.?\\d{0,3}\\s[a-zA-Z]{2,30}\\s[a-zA-Z]{2,15}") || currToken.matches("^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$")) && (characters > digits)) { // Business name

                if (currToken.length() > biggestName.length()) {
                    biggestName = currToken;
                }
            }

            countIncrement++;
        }

        st = new StringTokenizer(text.trim());

        while (st.hasMoreTokens()) { // We need: Date (1), Receipt Total (2)
            String currToken = st.nextToken();

            if ((currToken.contains("/") || currToken.contains("-") || currToken.contains("http")) && (currToken.matches(".*/.*/.*") || currToken.matches(".*-.*-.*"))) { // Date
                cache[1] = currToken;
            } else if ((currToken.contains("$") || currToken.contains(".")) && currToken.replaceAll("[^\\d.]", "").matches("[-+]?[0-9]*\\.?[0-9]+")) { // Receipt Total (with $)
                moneyList.add(Float.parseFloat(currToken.replaceAll("[^\\d.]", "")));
            }
        }

        cache[0] = biggestName;

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

        promptReceipt(activity, cache[0], cache[1], cache[2]);
    }

    // use dialog_receipt_add
    public static void promptReceipt(FragmentActivity activity, String bName, String date, String total) {
        ViewGroup view = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_receipt_add, view, false);

        // set default values
        EditText bNameField = dialogView.findViewById(R.id.receiptBusinessName);
        EditText dateField = dialogView.findViewById(R.id.receiptDate);
        EditText totalField = dialogView.findViewById(R.id.receiptTotalAmount);
        bNameField.setText(bName);
        dateField.setText(date);
        totalField.setText(total);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);

        builder.setPositiveButton("OK", (dialog, id) -> {
            // update fName, mInitial, and lName in firebase if value exists
            String businessName = bNameField.getText().toString();
            String receiptDate = dateField.getText().toString();
            String receiptTotal = totalField.getText().toString();

            // guard and make sure input is required
            if (businessName.isEmpty() || receiptDate.isEmpty() || receiptTotal.isEmpty()) {
                Toast.makeText(activity, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // add to PHM
            try {
                addReceipt(bNameField.getText().toString(), dateField.getText().toString(), Float.parseFloat(totalField.getText().toString()));
                Toast.makeText(activity, "Receipt added successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "addReceipt: Error adding document" + e);
                Toast.makeText(activity, "Error adding receipt", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel",
                (dialog, id) -> dialog.cancel());
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
