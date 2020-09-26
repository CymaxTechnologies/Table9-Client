package com.example.menuapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;

public class ArrivingBillActivity extends AppCompatActivity {
    ImageButton img;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arriving_bill);
        img=(ImageButton)findViewById(R.id.wait);
        Glide.with(getApplicationContext())
                .load(R.drawable.cooking)

                .into(img);
    }
}