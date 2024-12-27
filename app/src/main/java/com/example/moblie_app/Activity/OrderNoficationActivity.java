package com.example.moblie_app.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moblie_app.Adapter.OrderAdapter;
import com.example.moblie_app.Domain.OrderApi;
import com.example.moblie_app.R;
import com.example.moblie_app.ViewModel.OrderItem;
import com.example.moblie_app.api.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OrderNoficationActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_nofication);

        recyclerView = findViewById(R.id.recycler_view_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadOrder();
    }

    public void loadOrder() {
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        ApiClient.setToken(token);

        Retrofit retrofit = ApiClient.getClient();
        OrderApi orderApi = retrofit.create(OrderApi.class);
        Call<List<OrderItem>> call = orderApi.getUserOrder();

        call.enqueue(new Callback<List<OrderItem>>() {
            @Override
            public void onResponse(Call<List<OrderItem>> call, Response<List<OrderItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderItem> orders = response.body();
                    orderAdapter = new OrderAdapter(orders, new OrderAdapter.OnCancelOrderListener() {
                        @Override
                        public void onCancelOrder(int orderId) {
                            cancelOrder(orderId);
                        }
                    });
                    recyclerView.setAdapter(orderAdapter);
                } else {
                    Toast.makeText(OrderNoficationActivity.this, "Không có đơn hàng nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderItem>> call, Throwable t) {
                Toast.makeText(OrderNoficationActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cancelOrder(int orderId) {
        Retrofit retrofit = ApiClient.getClient();
        OrderApi orderApi = retrofit.create(OrderApi.class);
        Call<Void> call = orderApi.cancelOrder(orderId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderNoficationActivity.this, "Đơn hàng đã được hủy", Toast.LENGTH_SHORT).show();
                    loadOrder();
                } else {
                    Toast.makeText(OrderNoficationActivity.this, "Không thể hủy đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(OrderNoficationActivity.this, "Lỗi khi hủy đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
