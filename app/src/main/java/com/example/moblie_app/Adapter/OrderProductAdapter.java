package com.example.moblie_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moblie_app.R;
import com.example.moblie_app.ViewModel.Product;


import java.text.DecimalFormat;
import java.util.List;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.OrderProductViewHolder> {

    private List<Product> productList;
    private Context context;

    public OrderProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }
    @NonNull
    @Override
    public OrderProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new OrderProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProductViewHolder holder, int position) {
        Product product = productList.get(position);
        String productName = product.getProductName() != null ? product.getProductName() : "Không có tên sản phẩm";
        String productQuantity = product.getQuantity() != 0 ? String.valueOf(product.getQuantity()) : "0";
        String productPrice = product.getPrice() != null ? formatPrice(Double.parseDouble(product.getPrice())) : "0 đ";

        holder.tvProductName.setText(productName);
        holder.tvProductQuantity.setText(productQuantity);
        holder.tvProductPrice.setText(productPrice);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    private String formatPrice(double price) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(price) + " đ";
    }
    public class OrderProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductQuantity, tvProductPrice;
        public OrderProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}
