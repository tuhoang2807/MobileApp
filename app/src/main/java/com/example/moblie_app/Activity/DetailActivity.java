package com.example.moblie_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moblie_app.Domain.CartApi;
import com.example.moblie_app.Domain.ProductApi;
import com.example.moblie_app.R;
import com.example.moblie_app.ViewModel.CartItem;
import com.example.moblie_app.ViewModel.Product;
import com.example.moblie_app.api.ApiClient;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetailActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private TextView titleTextView, priceTextView, descriptionTextView, categoryTextView, stockTextView;
    private ImageView productImageView;

    private final Map<Integer, String> categoryMap = new HashMap<Integer, String>() {{
        put(4, "Bếp");
        put(1, "Chảo");
        put(3, "Máy hút bụi");
        put(5, "Máy xay");
        put(2, "Nồi");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        titleTextView = findViewById(R.id.textView8);
        priceTextView = findViewById(R.id.textView10);
        descriptionTextView = findViewById(R.id.textView13);
        categoryTextView = findViewById(R.id.textView15);
        stockTextView = findViewById(R.id.textView17);
        productImageView = findViewById(R.id.imageView3);
        String id = getIntent().getStringExtra("productId");
        fetchProductDetails(Integer.parseInt(id));
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
    }

    private void fetchProductDetails(int productId) {
        Retrofit retrofit = ApiClient.getClient();
        ProductApi productApi = retrofit.create(ProductApi.class);
        Call<Product> call = productApi.getProductById(productId);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body();
                    Log.d("DetailActivity", "Response body: " + product.toString());
                    double price = Double.parseDouble(product.getPrice());
                    NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                    String formattedPrice = formatter.format(price);
                    priceTextView.setText(formattedPrice + " đ");
                    titleTextView.setText(product.getName());
                    descriptionTextView.setText(product.getDescription());
                    int categoryId = product.getCategoryId();
                    String categoryName = categoryMap.getOrDefault(categoryId, "Không rõ");
                    categoryTextView.setText(categoryName);

                    stockTextView.setText(String.valueOf(product.getQuantity()));
                    String imageUrl = "http://10.0.2.2:3003/" + product.getImage().replace("\\", "/");
                    Picasso.get()
                            .load(imageUrl)
                            .into(productImageView);
                } else {
                    Log.e("DetailActivity", "Response error: Code = " + response.code() +
                            ", Message = " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("DetailActivity", "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("DetailActivity", "Error parsing error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(DetailActivity.this, "Không thể lấy dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e("DetailActivity", "API call failed: " + t.getMessage());
                Toast.makeText(DetailActivity.this, "Lỗi khi gọi API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onAddToCart(View view) {
        String productId = getIntent().getStringExtra("productId");
        if (productId == null) {
            Toast.makeText(DetailActivity.this, "ID sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(DetailActivity.this, "Bạn cần đăng nhập để thêm sản phẩm vào giỏ", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> userData = new HashMap<>();
        userData.put("product_id", productId);
        userData.put("quantity", "1");

        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json"), new Gson().toJson(userData));

        ApiClient.setToken(token);

        Retrofit retrofit = ApiClient.getClient();
        CartApi cartApi = retrofit.create(CartApi.class);
        Call<CartItem> call = cartApi.createCart(requestBody);

        call.enqueue(new Callback<CartItem>() {
            @Override
            public void onResponse(Call<CartItem> call, Response<CartItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(DetailActivity.this, "Đã thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    Gson gson = new Gson();
                    String jsonResponse = gson.toJson(response.body());
                    Log.d("DetailActivity", "Response success: " + jsonResponse);
                } else {
                    Toast.makeText(DetailActivity.this, "Không thể thêm sản phẩm vào giỏ", Toast.LENGTH_SHORT).show();
                    Log.e("DetailActivity", "Error: " + response.code() + ", " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CartItem> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Lỗi khi gọi API", Toast.LENGTH_SHORT).show();
                Log.e("DetailActivity", "API call failed: " + t.getMessage());
            }
        });
    }


    public void onBack(View view) {
        finish();
    }
}
