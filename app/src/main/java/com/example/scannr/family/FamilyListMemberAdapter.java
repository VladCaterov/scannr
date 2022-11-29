package com.example.scannr.family;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.scannr.R;

import java.util.ArrayList;

public class FamilyListMemberAdapter extends ArrayAdapter<String> {
private final Activity context;
private final ArrayList<String> name;

public FamilyListMemberAdapter(Activity context, ArrayList<String> firstNames) {
        super(context, R.layout.activity_family_manager_list_child, firstNames);
        this.context = context;
        this.name = firstNames;

        }


public View getView(int position, View convertView, ViewGroup container) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.activity_family_manager_list_member, null, true);

        TextView parentName = rowView.findViewById(R.id.memberTextView);
        parentName.setText(name.get(position));

        return rowView;

        }
}
