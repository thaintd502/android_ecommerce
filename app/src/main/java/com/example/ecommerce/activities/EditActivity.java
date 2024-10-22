package com.example.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.example.ecommerce.models.MyCartModel;
import com.example.ecommerce.models.NewProductModel;
import com.example.ecommerce.models.PopularProductsModel;
import com.example.ecommerce.models.ShowAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EditActivity extends AppCompatActivity {

    ImageView detailedImg;
    TextView rating, name, description, price, quantity;
    Button update, buyNow;
    ImageView addItems, removeItems;
    Toolbar toolbar;

    String documentId = "";


    int totalQuantity = 1;
    int totalPrice = 0;

    RatingBar ratingBar;

    NewProductModel newProductModel = null;
    // Popular products
    PopularProductsModel popularProductsModel = null;
    ShowAllModel showAllModel = null;
    MyCartModel myCartModel;
    FirebaseAuth auth;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        toolbar = findViewById(R.id.edt_detailed_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        final Object obj = getIntent().getSerializableExtra("editDetail");

        myCartModel = (MyCartModel) obj;
        documentId = myCartModel.getDocumentId();


        detailedImg = findViewById(R.id.edt_detailed_img);
        quantity = findViewById(R.id.edt_quantity);
        name = findViewById(R.id.edt_detailed_name);
        rating = findViewById(R.id.edt_rating);
//        description = findViewById(R.id.edt_detailed_desc);
        price = findViewById(R.id.edt_detailed_price);

        ratingBar = findViewById(R.id.edt_my_rating);

        update = findViewById(R.id.edt_add_to_cart);
//        buyNow = findViewById(R.id.edt_buy_now);

        addItems = findViewById(R.id.edt_add_item);
        removeItems = findViewById(R.id.edt_remove_item);

        //New Products
        if(myCartModel != null){
            name.setText(myCartModel.getProductName());
//            price.setText(String.valueOf(totalPrice));
            name.setText(myCartModel.getProductName());
            quantity.setText(myCartModel.getTotalQuantity());
            ratingBar.setRating((float)Double.parseDouble("4.8"));

            price.setText(myCartModel.getProductPrice());

        }

        totalQuantity = Integer.parseInt(myCartModel.getTotalQuantity());

        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(totalQuantity < 10){
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));

                    if(myCartModel != null){
                        totalPrice = Integer.parseInt(myCartModel.getProductPrice()) * totalQuantity;

                    }

                }
            }
        });

        removeItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totalQuantity > 1){
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));

                    if(myCartModel != null){
                        totalPrice = Integer.parseInt(myCartModel.getProductPrice()) * totalQuantity;

                    }
                }

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateItem();
            }
        });


    }

    private void updateItem() {
        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("currentTime", saveCurrentTime);
        cartMap.put("currentDate", saveCurrentDate);

        cartMap.put("totalQuantity", quantity.getText().toString());
        cartMap.put("totalPrice", totalPrice);


        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User").document(documentId).update(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(EditActivity.this, CartActivity.class);
                            startActivity(intent);
                            Toast.makeText(EditActivity.this, "Item updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditActivity.this, "Failed to update item", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}