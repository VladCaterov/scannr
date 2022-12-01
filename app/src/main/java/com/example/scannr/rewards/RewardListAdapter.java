package com.example.scannr.rewards;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scannr.R;
import com.example.scannr.authentication.Validation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class RewardListAdapter extends ArrayAdapter<String> {
    private final Activity context;

    private final ArrayList<String> statuses;
    private final ArrayList<String> childNames;

    public RewardListAdapter(Activity context, ArrayList<String> childNames, ArrayList<String> statuses) {
        super(context, R.layout.activity_reward_list_created_reward, childNames);
        this.context = context;
        this.statuses = statuses;
        this.childNames = childNames;

    }
    public View getView(int position, View convertView, ViewGroup container) {
        LayoutInflater inflater = context.getLayoutInflater();

        View rowView = inflater.inflate(R.layout.activity_reward_list_created_reward, null, true);
        TextView childName = rowView.findViewById(R.id.rewardChildName);
        childName.setText(childNames.get(position));
        TextView status = rowView.findViewById(R.id.rewardChildStatus);
        status.setText(statuses.get(position));
        return rowView;
    }
}
