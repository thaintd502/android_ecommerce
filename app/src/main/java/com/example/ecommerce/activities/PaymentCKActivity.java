package com.example.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.example.ecommerce.models.OrderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;


import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class PaymentCKActivity extends AppCompatActivity   {

    private static final String TAG = "paymentExample";

    private static final String PAYPAL_KEY = "ASL59tvqK0DuKVMIkb5LmiyNuZH3xT5BHGo8U9_DPUiJl2fXnTDGGVbV1UspkBGAk-nT7P1y4qQ2AwUy";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static  PayPalConfiguration config;
    PayPalPayment thingsToBuy;


    Toolbar toolbar;
    TextView subTotal, discount, shipping, total;

    Button btnPaypal, btnNhanhang;
    ImageView imageView;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;


    public static double amount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        toolbar = findViewById(R.id.payment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

//        double amount = 0.0;
        amount = getIntent().getDoubleExtra("amount", 0.0);

        subTotal = findViewById(R.id.sub_total);
        discount = findViewById(R.id.textView17);
        shipping = findViewById(R.id.textView18);
        total = findViewById(R.id.total_amt);
        imageView = findViewById(R.id.pay_btn);
        btnNhanhang = findViewById(R.id.pay_btn_2);

        subTotal.setText(amount + "S");
        total.setText(amount + "S");



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MakePayment();
            }

        });
        configPaypal();



        btnNhanhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    saveOrderToFirebase();

            }
        });

    }

    private void saveOrderToFirebase() {

        OrderModel order = new OrderModel(amount);

        firestore.collection("Order").document(auth.getCurrentUser().getUid())
                .collection("User").add(order).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(PaymentCKActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PaymentCKActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }
    private void configPaypal() {
        config = new PayPalConfiguration()
                .environment(CONFIG_ENVIRONMENT)
                .clientId(PAYPAL_KEY)
                .merchantName("Paypal login")
                .merchantPrivacyPolicyUri(Uri.parse("https://www.example.ecommerce.com/privacy"))
                .merchantUserAgreementUri(Uri.parse("https://www.example.ecommerce.com/legal"))
        ;


    }

    private void MakePayment() {
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        thingsToBuy = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "USD", "Payment", PayPalPayment.PAYMENT_INTENT_SALE);
        thingsToBuy.payeeEmail("thaimeo570257@gmail.com");

        Intent payment = new Intent(this, PaymentActivity.class);
        payment.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);
        payment.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startActivityForResult(payment, REQUEST_CODE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {

                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirm != null) {

                    try {
                        System.out.println(confirm.toJSONObject().toString(4));
                        System.out.println(confirm.getPayment().toJSONObject().toString(4));
                        Toast.makeText(this, "Payment  successful", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Payment has been cancelled", Toast.LENGTH_SHORT).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
            }

        }else if(requestCode == REQUEST_CODE_FUTURE_PAYMENT){
            if(resultCode == Activity.RESULT_OK){
                PayPalAuthorization auth = data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);

                if(auth != null){
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));
                        String authorization_code = auth.getAuthorizationCode();
                        Log.d("FuturePaymentExample", authorization_code);

                        Log.e("paypal", "future Payment code received from Paypal: " + authorization_code);
                    }catch (JSONException e){

                        Toast.makeText(this, "failure Occurred", Toast.LENGTH_SHORT).show();
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred ", e);
                    }
                }
            }
        }
    }




}