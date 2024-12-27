package com.example.moblie_app.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moblie_app.R;
import com.example.moblie_app.ViewModel.CartItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnItemClickListener onItemClickListener;

    public CartAdapter(List<CartItem> cartItems, OnItemClickListener onItemClickListener) {
        this.cartItems = cartItems;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.titleTxt.setText(item.getProductName());
        String feeEachItem = item.getPrice();
        if (feeEachItem != null) {
            feeEachItem = formatCurrency(feeEachItem) + " đ";
        }
        holder.feeEachItem.setText(feeEachItem);

        String totalEachItem = item.getTotalMoney();
        if (totalEachItem != null) {
            totalEachItem = formatCurrency(totalEachItem) + " đ";
        }
        holder.totalEachItem.setText(totalEachItem);
        holder.numberItemTxt.setText(String.valueOf(item.getQuantity()));
        holder.plusCartBtn.setOnClickListener(v -> onItemClickListener.onPlusClick(item));
        holder.minusCartBtn.setOnClickListener(v -> onItemClickListener.onMinusClick(item));
        String imageUrl = item.getImageUrl();
        if (imageUrl != null) {
            imageUrl = "http://10.0.2.2:3003/" + imageUrl.replace("\\", "/");
        } else {
            imageUrl = "http://10.0.2.2:3003/default_image.jpg";
        }
        Picasso.get()
                .load(imageUrl)
                .into(holder.picCart);
    }

    private String formatCurrency(String amount) {
        try {
            long value = Long.parseLong(amount);
            return String.format("%,d", value).replace(",", ".");
        } catch (NumberFormatException e) {
            return amount;
        }
    }



    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItems = newCartItems;
        notifyDataSetChanged();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView picCart;
        TextView titleTxt, feeEachItem, totalEachItem, numberItemTxt;
        TextView plusCartBtn, minusCartBtn;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            picCart = itemView.findViewById(R.id.picCart);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            numberItemTxt = itemView.findViewById(R.id.numberItemTxt);
            plusCartBtn = itemView.findViewById(R.id.plusCartBtn);
            minusCartBtn = itemView.findViewById(R.id.minusCartBtn);
        }
    }

    public interface OnItemClickListener {
        void onPlusClick(CartItem item);
        void onMinusClick(CartItem item);
    }
}
