package com.example.scannr.family;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.scannr.R;

import java.util.ArrayList;

public class FamilyListItemAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> names;
    public FamilyListItemAdapter(Activity context, ArrayList<String> names) {
        super(context, R.layout.activity_family_manager_list_child, names);
        this.context = context;
        this.names = names;
    }


    public View getView(int position, View convertView, ViewGroup container) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.activity_family_manager_list_child, null, true);

        TextView name = (TextView) rowView.findViewById(R.id.childName);
        name.setText(names.get(position));
        return rowView;

////        LayoutInflater inflater = (L    ayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//        if (convertView == null) {
////            convertView = getLayoutInflater().inflate(R.layout.list_item, container, false);
//        }
//        return convertView;
    }
}
