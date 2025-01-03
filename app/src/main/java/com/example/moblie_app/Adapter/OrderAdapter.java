package com.example.moblie_app.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moblie_app.Activity.OrderDetailActivity;
import com.example.moblie_app.R;
import com.example.moblie_app.ViewModel.OrderItem;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderItem> orderList;
    private OnCancelOrderListener onCancelOrderListener;
    private OnOrderClickListener onOrderClickListener;

    public OrderAdapter(List<OrderItem> orderList, OnCancelOrderListener onCancelOrderListener) {
        this.orderList = orderList;
        this.onCancelOrderListener = onCancelOrderListener;
    }
    public void setOnOrderClickListener(OnOrderClickListener onOrderClickListener) {
        this.onOrderClickListener = onOrderClickListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderItem order = orderList.get(position);
        holder.accountName.setText(order.getUsername());
        holder.phoneNumber.setText(order.getphone_number());
        holder.shippingAddress.setText(order.getAddress());
        String formattedDate = formatDate(order.getcreated_at());
        holder.orderDate.setText(formattedDate);
        String formattedAmount = formatCurrency(order.gettotal_amount());
        holder.totalAmount.setText(formattedAmount);
        String orderStatus = order.getStatus();
        if ("pending".equals(orderStatus)) {
            holder.orderStatus.setText("Chờ xử lý");
        } else if ("canceled".equals(orderStatus)) {
            holder.orderStatus.setText("Đã hủy");
        } else if ("shipping".equals(orderStatus)) {
            holder.orderStatus.setText("Đang giao");
        } else if ("completed".equals(orderStatus)) {
            holder.orderStatus.setText("Đã giao");
        } else {
            holder.orderStatus.setText(orderStatus);
        }
        holder.cancelOrderButton.setOnClickListener(v -> {
            if (onCancelOrderListener != null) {
                onCancelOrderListener.onCancelOrder(order.getId());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (onOrderClickListener != null) {
                onOrderClickListener.onOrderClick(order.getId());
            }
            Intent intent = new Intent(v.getContext(), OrderDetailActivity.class);
            intent.putExtra("order_id", order.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private String formatCurrency(String amount) {
        try {
            double value = Double.parseDouble(amount);
            DecimalFormat formatter = new DecimalFormat("#,###");
            formatter.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.getDefault()));
            formatter.setDecimalFormatSymbols(new DecimalFormatSymbols() {{
                setGroupingSeparator('.');
            }});
            return formatter.format(value) + " đ";
        } catch (NumberFormatException e) {
            return amount;
        }
    }

    private String formatDate(String isoDate) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setLenient(false);
            Date date = isoFormat.parse(isoDate);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            return isoDate;
        }
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView accountName, phoneNumber, shippingAddress, orderDate, totalAmount, orderStatus;
        View cancelOrderButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.account_name);
            phoneNumber = itemView.findViewById(R.id.phone_number);
            shippingAddress = itemView.findViewById(R.id.shipping_address);
            orderDate = itemView.findViewById(R.id.order_date);
            totalAmount = itemView.findViewById(R.id.total_amount);
            orderStatus = itemView.findViewById(R.id.order_status);
            cancelOrderButton = itemView.findViewById(R.id.button_cancel_order);
        }
    }

    public interface OnCancelOrderListener {
        void onCancelOrder(int orderId);
    }

    public interface OnOrderClickListener {
        void onOrderClick(int orderId);
    }

}
