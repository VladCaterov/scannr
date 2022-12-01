package com.example.scannr.family;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.scannr.MainActivity;
import com.example.scannr.R;
import com.example.scannr.authentication.LoginUser;
import com.example.scannr.authentication.RegisterUser;
import com.example.scannr.authentication.Validation;
import com.example.scannr.rewards.CreateRewardForm;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;
import java.util.zip.Inflater;

public class FamilyListChildAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> firstName;
    private final ArrayList<String> lastName;

    private final ArrayList<String> childIDArray;
    public FamilyListChildAdapter(Activity context, ArrayList<String> firstNames, ArrayList<String> lastNames, ArrayList<String> childIDs) {
        super(context, R.layout.activity_family_manager_list_child, firstNames);
        this.context = context;
        this.childIDArray = childIDs;
        this.firstName = firstNames;
        this.lastName = lastNames;
    }


    public View getView(int position, View convertView, ViewGroup container) {
        LayoutInflater inflater = context.getLayoutInflater();

        View rowView = inflater.inflate(R.layout.activity_family_manager_list_child, null, true);
        Button delete =  rowView.findViewById(R.id.deleteChild);
        Button update =  rowView.findViewById(R.id.updateChild);

        delete.setOnClickListener(v -> deleteChildUser(childIDArray, position));
        update.setOnClickListener(v -> updateChildUser(inflater, position));

        TextView fName = rowView.findViewById(R.id.firstNameChild);
        fName.setText(firstName.get(position));
        TextView lName = rowView.findViewById(R.id.lastNameChild);
        lName.setText(lastName.get(position));
        return rowView;
    }
    private boolean validateInput(EditText bRoutingNumber, EditText bAccountNumber, EditText spendingLimit){
        Validation validate = new Validation();

//        if (validate.isEmptyFirstName(fName.getText().toString())) {
////            Toast.makeText(context, "First Name is Required",
////                    Toast.LENGTH_SHORT).show();
//            fName.setError("First Name is Required");
//            fName.requestFocus();
//            return false;
//        }
//        if (validate.isEmptyLastName(lName.getText().toString())) {
////            Toast.makeText(context, "Last Name is Required",
////                    Toast.LENGTH_SHORT).show();
//            lName.setError("Last Name is Required");
//            lName.requestFocus();
//            return false;
//        }
//        if (validate.isEmptyEmail(email.getText().toString())) {
////            Toast.makeText(context, "Email is Required",
////                    Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (!validate.validateEmail(email.getText().toString())) {
////            Toast.makeText(context, "Please Enter A Valid Email",
////                    Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (validate.isEmptyBankAccountNumber(bAccountNumber.getText().toString())){
            Toast.makeText(context, "Bank account number required!",
                    Toast.LENGTH_SHORT).show();
            bAccountNumber.setError("Bank account number required!");
            bAccountNumber.requestFocus();
            return false;
        }
        if (!validate.validateBankAccountNumber(bAccountNumber.getText().toString())){
            Toast.makeText(context, "Min account number is 8 char!",
                    Toast.LENGTH_SHORT).show();
            bAccountNumber.setError("Min account number is 8 char!");
            bAccountNumber.requestFocus();
            return false;
        }
        if (validate.isEmptyBankRoutingNumber(bRoutingNumber.getText().toString())){
            Toast.makeText(context, "Bank routing number required!",
                    Toast.LENGTH_SHORT).show();
            bRoutingNumber.setError("Bank routing number required!");
            bRoutingNumber.requestFocus();
            return false;
        }
        if (validate.isEmptySpendingLimit(spendingLimit.getText().toString())){
            Toast.makeText(context, "Spending Limit Must Be At Least 0",
                    Toast.LENGTH_SHORT).show();
            spendingLimit.setError("Spending Limit Must Be At Least 0");
            spendingLimit.requestFocus();
            return false;
        }
        return true;
    }

    private void updateChildUser(LayoutInflater inflater, int position){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRefChild = db.collection("users").document(childIDArray.get(position));
        View view = inflater.inflate(R.layout.dialog_update_user,null, true);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        EditText eBankRoutingNumber, eBankAccountNumber, eSpendingLimit;

        eBankRoutingNumber = view.findViewById(R.id.bankRoutingNumberUpdateChild);
        eBankAccountNumber = view.findViewById(R.id.bankAccountNumberUpdateChild);
        eSpendingLimit = view.findViewById(R.id.spendingLimitUpdateChild);
        docRefChild.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                eBankRoutingNumber.setText(documentSnapshot.get("routingNumber").toString());
                eBankAccountNumber.setText(documentSnapshot.get("accountNumber").toString());
                eSpendingLimit.setText(documentSnapshot.get("spendingLimit").toString());
            }
        });

        // Add the buttons
        builder.setView(view)
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    EditText editBankRoutingNumber, editBankAccountNumber, editSpendingLimit;

                    editBankRoutingNumber = view.findViewById(R.id.bankRoutingNumberUpdateChild);
                    editBankAccountNumber = view.findViewById(R.id.bankAccountNumberUpdateChild);
                    editSpendingLimit = view.findViewById(R.id.spendingLimitUpdateChild);
                    boolean valid = validateInput(editBankRoutingNumber,editBankAccountNumber,editSpendingLimit);
                    if(valid){
                        docRefChild.update("routingNumber", editBankRoutingNumber.getText().toString());
                        docRefChild.update("accountNumber", editBankAccountNumber.getText().toString());
                        docRefChild.update("spendingLimit", editSpendingLimit.getText().toString());

                        Toast.makeText(context, "DEMO UPDATE USER SUCCESSFULLY", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(context, "DEMO FAILED TO UPDATE USER", Toast.LENGTH_LONG).show();
                    }

                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void deleteChildUser(ArrayList<String> childIDs, int position){
        String childID = childIDs.get(position);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference currentUsers = db.collection("users");
        CollectionReference deletedUsers = db.collection("deletedUsers");

        DocumentReference docRefParent = currentUsers.document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        //DELETE THE CHILD FROM USER DATABASE
        moveFirestoreDocument(currentUsers.document(childID), deletedUsers.document(childID));
        //DELETE THE CHILD REFERENCE FROM PARENT
        currentUsers.document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        docRefParent.update("children", FieldValue.arrayRemove(childID));
                        docRefParent.update("numChildren", FieldValue.increment(-1));
                        Toast.makeText(context, "Deleted child " + childID + " user successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Unable to delete child user", Toast.LENGTH_SHORT).show();
                    }
                });
//        Intent intent = context.getIntent();
//        context.finish();
//        context.startActivity(new Intent(intent));
    }

    //
    //USED: https://stackoverflow.com/questions/47244403/how-to-move-a-document-in-cloud-firestore
    public void moveFirestoreDocument(DocumentReference fromPath, final DocumentReference toPath) {
        fromPath.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    toPath.set(document.getData())
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                fromPath.delete()
                                        .addOnSuccessListener(aVoid1 -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                                        .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
                            })
                            .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }
}
