package com.example.scannr.family;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scannr.MainActivity;
import com.example.scannr.R;
import com.example.scannr.authentication.RegisterUser;
import com.example.scannr.rewards.CreateRewardForm;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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
            context.startActivity(new Intent(context, UpdateChildForm.class));

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
}
