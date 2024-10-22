package com.example.ecommerce.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.ecommerce.R;

public class PaypalActivity extends AppCompatActivity {

    Button paypalBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

//        paypalBtn = findViewById(R.id.pay_btn);
    }
}