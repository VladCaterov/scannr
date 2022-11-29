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

import com.example.scannr.MainActivity;
import com.example.scannr.R;
import com.example.scannr.authentication.LoginUser;
import com.example.scannr.authentication.RegisterUser;
import com.example.scannr.authentication.Validation;
import com.example.scannr.rewards.CreateRewardForm;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class FamilyListChildAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> firstName;
    private final ArrayList<String> lastName;

    private final ArrayList<String> childID;
    public FamilyListChildAdapter(Activity context, ArrayList<String> firstNames, ArrayList<String> lastNames, ArrayList<String> childIDs) {
        super(context, R.layout.activity_family_manager_list_child, firstNames);
        this.context = context;
        this.childID = childIDs;
        this.firstName = firstNames;
        this.lastName = lastNames;

    }


    public View getView(int position, View convertView, ViewGroup container) {
        LayoutInflater inflater = context.getLayoutInflater();

        View rowView = inflater.inflate(R.layout.activity_family_manager_list_child, null, true);
        Button delete =  rowView.findViewById(R.id.deleteChild);
        Button update =  rowView.findViewById(R.id.updateChild);

        delete.setOnClickListener(v -> Toast.makeText(context, "DEMO: DELETE CHILD - " + childID.get(position),
                Toast.LENGTH_SHORT).show());

        update.setOnClickListener(v -> {
            View view = inflater.inflate(R.layout.dialog_update_user,null, true);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            // Add the buttons
            builder.setView(view)
                    .setPositiveButton(R.string.ok, (dialog, id) -> {
                        EditText editFirstName, editLastName, editEmail, editPassword, editBankRoutingNumber,
                                editBankAccountNumber, editSpendingLimit;
                        editFirstName = view.findViewById(R.id.fNameUpdateChild);
                        editLastName = view.findViewById(R.id.lNameUpdateChild);
                        editEmail=  view.findViewById(R.id.emailUpdateChild);
                        editPassword= view.findViewById(R.id.passwordUpdateChild);
                        editBankRoutingNumber = view.findViewById(R.id.bankRoutingNumberUpdateChild);
                        editBankAccountNumber = view.findViewById(R.id.bankAccountNumberUpdateChild);
                        editSpendingLimit = view.findViewById(R.id.spendingLimitUpdateChild);
                        boolean valid = validateInput(editFirstName,editLastName,editEmail,editPassword,editBankRoutingNumber,editBankAccountNumber,editSpendingLimit);
                        if(valid){
                            Toast.makeText(context, "DEMO UPDATE USER SUCCESSFULLY", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());

            AlertDialog dialog = builder.create();
            dialog.show();

        });
        TextView fName = rowView.findViewById(R.id.firstNameChild);
        fName.setText(firstName.get(position));
        TextView lName = rowView.findViewById(R.id.lastNameChild);
        lName.setText(lastName.get(position));
        return rowView;

////        LayoutInflater inflater = (L    ayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//        if (convertView == null) {
////            convertView = getLayoutInflater().inflate(R.layout.list_item, container, false);
//        }
//        return convertView;
    }
    private boolean validateInput(EditText fName, EditText lName, EditText email, EditText password,
                               EditText bRoutingNumber, EditText bAccountNumber, EditText spendingLimit){
        Validation validate = new Validation();

        if (validate.isEmptyFirstName(fName.getText().toString())) {
            Toast.makeText(context, "First Name is Required",
                    Toast.LENGTH_SHORT).show();
        return false;
        }
        if (validate.isEmptyLastName(lName.getText().toString())) {
            Toast.makeText(context, "Last Name is Required",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (validate.isEmptyEmail(email.getText().toString())) {
            Toast.makeText(context, "Email is Required",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!validate.validateEmail(email.getText().toString())) {
            Toast.makeText(context, "Please Enter A Valid Email",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (validate.isEmptyPassword(password.getText().toString())) {
            Toast.makeText(context, "Password is Required",
                    Toast.LENGTH_SHORT).show();
            password.setError("Password required!");
            password.requestFocus();
            return false;
        }
        if (validate.validatePassword(password.getText().toString())) {
            Toast.makeText(context, "Please Enter a Valid Password",
                    Toast.LENGTH_SHORT).show();
            password.setError("Min password length is 6 char!");
            password.requestFocus();
            return false;
        }
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
}
