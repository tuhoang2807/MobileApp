package com.example.moblie_app.Adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moblie_app.Activity.DetailActivity;
import com.example.moblie_app.R;
import com.example.moblie_app.ViewModel.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        String price = product.getPrice();
        String priceString = String.valueOf(price);
        StringBuilder formattedPrice = new StringBuilder();
        int length = priceString.length();
        int count = 0;

        for (int i = length - 1; i >= 0; i--) {
            if (count > 0 && count % 3 == 0) {
                formattedPrice.insert(0, '.');
            }
            formattedPrice.insert(0, priceString.charAt(i));
            count++;
        }
        formattedPrice.append(" đ");
        holder.titleTextView.setText(product.getName());
        holder.priceTextView.setText(formattedPrice.toString());
        String imageUrl = product.getImage();
        if (imageUrl != null) {
            imageUrl = "http://10.0.2.2:3003/" + imageUrl.replace("\\", "/");
        } else {
            imageUrl = "http://10.0.2.2:3003/default_image.jpg";
        }
        Picasso.get()
                .load(imageUrl)
                .into(holder.imageView);
        holder.itemView.setOnClickListener(v -> {
            String productId = String.valueOf(product.getId());
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("productId", productId);
            holder.itemView.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView priceTextView, titleTextView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.picProduct);
            priceTextView = itemView.findViewById(R.id.textView9);
            titleTextView = itemView.findViewById(R.id.textView12);
        }
    }
    public void addProducts(List<Product> newProducts) {
        int startPosition = productList.size();
        productList.addAll(newProducts);
        notifyItemRangeInserted(startPosition, newProducts.size());
    }
}

