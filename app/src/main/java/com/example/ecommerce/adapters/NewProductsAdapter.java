package com.example.ecommerce.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.example.ecommerce.activities.DetailedActivity;
import com.example.ecommerce.models.NewProductModel;

import java.util.List;

public class NewProductsAdapter extends RecyclerView.Adapter<NewProductsAdapter.ViewHolder> {

    private Context context;
    private List<NewProductModel> list;

    public NewProductsAdapter(Context context, List<NewProductModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NewProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.new_products, parent, false ));

    }

    @Override
    public void onBindViewHolder(@NonNull NewProductsAdapter.ViewHolder holder, int position) {

        Glide.with(context).load(list.get(position).getImg_url()).into(holder.newImg);
        holder.newName.setText(list.get(position).getName());
        holder.newPrice.setText(String.valueOf(list.get(position).getPrice()));
//        holder.newName.setText(list.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailedActivity.class);
                intent.putExtra("detailed", list.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView newImg;
        TextView newName, newPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            newImg = itemView.findViewById(R.id.new_img);
            newName = itemView.findViewById(R.id.new_product_name);
            newPrice = itemView.findViewById(R.id.new_price);


        }
    }
}
