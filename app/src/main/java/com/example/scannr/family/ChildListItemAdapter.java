package com.example.scannr.family;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class ChildListItemAdapter extends ArrayAdapter {
    public ChildListItemAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) Context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//            convertView = getLayoutInflater().inflate(R.layout.activity_family_manager_list_child, container, false);
        }
        return convertView;
    }
}
