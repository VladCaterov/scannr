package com.example.scannr.rewards;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scannr.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CreateRewardForm extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reward);

        FloatingActionButton backButton = findViewById(R.id.backButtonRewardsCreateReward);
        backButton.setOnClickListener(this);

        Button createReward = findViewById(R.id.createRewardButtonSubmit);
        createReward.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backButtonRewardsCreateReward:
                finish();
                break;
            case R.id.createRewardButtonSubmit:
                Toast.makeText(CreateRewardForm.this, "CREATED REWARD", Toast.LENGTH_SHORT).show();
                finish();
                break;

        }
    }
}
