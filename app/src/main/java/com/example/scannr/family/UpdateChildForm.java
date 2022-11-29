package com.example.scannr.family;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scannr.R;

public class UpdateChildForm extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_child_user);

        Button updateUserButtonSubmit = findViewById(R.id.updateUserButtonSubmit);
        updateUserButtonSubmit.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.updateUserButtonSubmit) {
            updateChild();
        }
    }

    private void updateChild() {
        finish();
        Toast.makeText(UpdateChildForm.this, "DEMO: USER UPDATED SUCCESSFULLY",Toast.LENGTH_SHORT).show();;

    }
}
