package com.example.moblie_app.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moblie_app.Adapter.OrderProductAdapter;
import com.example.moblie_app.Domain.OrderApi;
import com.example.moblie_app.R;
import com.example.moblie_app.Utils.DateUtils;
import com.example.moblie_app.ViewModel.OrderItem;
import com.example.moblie_app.ViewModel.Product;
import com.example.moblie_app.api.ApiClient;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OrderDetailActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerViewProducts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        loadInformationDetail();
        loadInfoProductDetail();
    }

    private void updateUI(OrderItem order) {
        TextView tvOrderId = findViewById(R.id.tv_order_id);
        TextView tvOrderDate = findViewById(R.id.tv_order_date);
        TextView tvCustomerName = findViewById(R.id.tv_customer_name);
        TextView tvPhoneNumber = findViewById(R.id.tv_phone_number);
        TextView tvAddress = findViewById(R.id.tv_address);
        TextView tvStatus = findViewById(R.id.tv_status);
        TextView tvTotalPrice = findViewById(R.id.tv_total_price);

        tvOrderId.setText("Mã đơn hàng: " + order.getId());
        String formattedDate = DateUtils.formatDate(order.getcreated_at());
        tvOrderDate.setText("Ngày đặt hàng: " + formattedDate);
        tvCustomerName.setText("Người đặt: " + order.getUsername());
        tvPhoneNumber.setText("Số điện thoại: " + order.getphone_number());
        tvAddress.setText("Địa chỉ nhận hàng: " + order.getAddress());
        tvStatus.setText("Trạng thái: " + order.getStatus());
        try {
            double amount = Double.parseDouble(order.gettotal_amount());
            String formattedAmount = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                    .format(amount)
                    .replace("₫", "đ")
                    .trim();
            tvTotalPrice.setText("Tổng tiền: " + formattedAmount);
        } catch (NumberFormatException e) {
            tvTotalPrice.setText("Tổng tiền: 0 đ");
            Log.e("OrderDetail", "Giá trị không hợp lệ: " + order.gettotal_amount(), e);
        }

        String orderStatus = order.getStatus();
        if ("canceled".equals(orderStatus)) {
            tvStatus.setText("Trạng thái: Đã hủy");
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
        } else if ("pending".equals(orderStatus)) {
            tvStatus.setText("Trạng thái: Chờ xử lý");
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.blue));
        } else if ("shipping".equals(orderStatus)) {
            tvStatus.setText("Trạng thái: Đang giao");
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.orange));
        } else if ("completed".equals(orderStatus)) {
            tvStatus.setText("Trạng thái: Đã giao");
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.green));
        } else {
            tvStatus.setText("Trạng thái: " + orderStatus);
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.black));
        }

    }
    public void loadInformationDetail() {
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        ApiClient.setToken(token);

        Retrofit retrofit = ApiClient.getClient();
        OrderApi orderApi = retrofit.create(OrderApi.class);

        int orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Không tìm thấy đơn hàng.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<OrderItem> call = orderApi.getInformationOrder(orderId);
        call.enqueue(new Callback<OrderItem>() {
            @Override
            public void onResponse(Call<OrderItem> call, Response<OrderItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderItem order = response.body();
                    updateUI(order);
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Không thể tải thông tin đơn hàng.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderItem> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Lỗi khi tải dữ liệu: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadInfoProductDetail() {
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        ApiClient.setToken(token);
        Retrofit retrofit = ApiClient.getClient();
        OrderApi orderApi = retrofit.create(OrderApi.class);
        int orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Không tìm thấy đơn hàng.", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<List<Product>> call = orderApi.getProductItem(orderId);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> productList = response.body();
                    for (Product product : productList) {
                        Log.d("Product Details", "Name: " + product.getProductName() + ", Quantity: " + product.getQuantity() + ", Price: " + product.getPrice());
                    }
                    OrderProductAdapter productAdapter = new OrderProductAdapter(getApplicationContext(), productList);
                    recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerViewProducts.setAdapter(productAdapter);
                } else {
                    Toast.makeText(getApplicationContext(), "Lỗi phản hồi từ server.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("ProductDetail", "Error: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Không thể kết nối đến server. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onBack(View view){
        finish();
    }
}