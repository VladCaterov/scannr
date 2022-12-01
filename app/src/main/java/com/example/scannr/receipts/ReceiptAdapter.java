package com.example.scannr.receipts;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.scannr.R;

import java.util.ArrayList;

public class ReceiptAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> businessList;
    private final ArrayList<String> dateList;
    private final ArrayList<Float> totalList;

    public ReceiptAdapter(Activity context, ArrayList<String> businessNames, ArrayList<String> dates, ArrayList<Float> receiptTotal) {
        super(context, R.layout.activity_receipt_list_item, businessNames);
        this.context = context;
        this.businessList = businessNames;
        this.dateList = dates;
        this.totalList = receiptTotal;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = context.getLayoutInflater().inflate(R.layout.activity_receipt_list_item, parent, true);
        }

//        LayoutInflater inflater = context.getLayoutInflater();

//        View rowView = inflater.inflate(R.layout.activity_receipt_list_item, null, true);

        // Lookup view for data population
        TextView businessNameView = (TextView) convertView.findViewById(R.id.businessName);
        TextView dateView = (TextView) convertView.findViewById(R.id.date);
        TextView totalView = (TextView) convertView.findViewById(R.id.totalAmount);

        //print out businessList
        System.out.println("lmao: " + businessList);

        // Populate data
        businessNameView.setText(businessList.get(position));
        dateView.setText(dateList.get(position));
        totalView.setText(totalList.get(position).toString());

        return convertView;
    }
}
