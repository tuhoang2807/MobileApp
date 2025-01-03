package com.example.moblie_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moblie_app.Domain.OrderApi;
import com.example.moblie_app.R;
import com.example.moblie_app.ViewModel.OrderItem;
import com.example.moblie_app.api.ApiClient;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class OrderActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        int totalAmount = getIntent().getIntExtra("totalAmount", 0);
        TextView totalAmountTextView = findViewById(R.id.totalAmount);
        totalAmountTextView.setText(formatCurrency(totalAmount));
    }

    private String formatCurrency(int amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + " đ";
    }

    public void onBack(View view) {
        finish();
    }

    public void createOrder(View view) {
        TextView addressTextView = findViewById(R.id.inputAddress);
        TextView phoneNumberTextView = findViewById(R.id.inputPhone);

        RadioGroup paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        int selectedPaymentMethodId = paymentMethodGroup.getCheckedRadioButtonId();
        RadioButton selectedPaymentMethod = findViewById(selectedPaymentMethodId);

        String address = addressTextView.getText().toString().trim();
        String phoneNumber = phoneNumberTextView.getText().toString().trim();

        if (address.isEmpty() || phoneNumber.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (selectedPaymentMethod == null) {
            showToast("Vui lòng chọn phương thức thanh toán!");
            return;
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("address", address);
        params.put("phone_number", phoneNumber);

        Gson gson = new Gson();
        String json = gson.toJson(params);

        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            showToast("Bạn chưa đăng nhập!");
            return;
        }

        ApiClient.setToken(token);
        Retrofit retrofit = ApiClient.getClient();
        OrderApi orderApi = retrofit.create(OrderApi.class);


        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);


        orderApi.createOrder(requestBody).enqueue(new retrofit2.Callback<OrderItem>() {
            @Override
            public void onResponse(Call<OrderItem> call, retrofit2.Response<OrderItem> response) {
                if (response.isSuccessful()) {
                    OrderItem orderItem = response.body();
                    if (selectedPaymentMethod.getText().toString().equals("Thanh toán Online: ZaloPay")) {
                        int orderId = orderItem.getOrderId();
                        processOnlinePayment(orderId);
                    } else {
                        showToast("Đặt hàng thành công");
                        Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    showToast("Đặt hàng thất bại! Vui lòng thử lại.");
                }
            }

            @Override
            public void onFailure(Call<OrderItem> call, Throwable t) {
                showToast("Có lỗi xảy ra: " + t.getMessage());
            }
        });
    }


    private void processOnlinePayment(int orderId) {
        HashMap<String, Object> paymentParams = new HashMap<>();
        paymentParams.put("order_id", orderId);
        paymentParams.put("platform", "android"); // Thêm platform
        Gson gson = new Gson();
        String paymentJson = gson.toJson(paymentParams);
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            showToast("Bạn chưa đăng nhập!");
            return;
        }

        ApiClient.setToken(token);
        RequestBody paymentRequestBody = RequestBody.create(MediaType.parse("application/json"), paymentJson);
        OrderApi orderApi = ApiClient.getClient().create(OrderApi.class);
        orderApi.paymentOnline(paymentRequestBody).enqueue(new retrofit2.Callback<OrderItem>() {
            @Override
            public void onResponse(Call<OrderItem> call, retrofit2.Response<OrderItem> response) {
                if (response.isSuccessful()) {
                    String paymentLink = response.body().getPaymentLink();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentLink));
                    startActivity(browserIntent);
                    showToast("Đang chuyển hướng tới trang thanh toán...");
                } else {
                    showToast("Thanh toán online thất bại!");
                }
            }

            @Override
            public void onFailure(Call<OrderItem> call, Throwable t) {
                showToast("Có lỗi xảy ra khi thanh toán online!");
            }
        });
    }



    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}