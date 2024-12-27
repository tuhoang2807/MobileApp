package com.example.moblie_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moblie_app.Adapter.CartAdapter;
import com.example.moblie_app.Domain.CartApi;
import com.example.moblie_app.Domain.ProductApi;
import com.example.moblie_app.R;
import com.example.moblie_app.ViewModel.CartItem;
import com.example.moblie_app.ViewModel.Product;
import com.example.moblie_app.api.ApiClient;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<CartItem> cartItems = new ArrayList<>();
    private CartAdapter adapter;
    private SharedPreferences sharedPreferences;
    private TextView emptyCartTextView;


    private CartAdapter.OnItemClickListener onItemClickListener = new CartAdapter.OnItemClickListener() {
        @Override
        public void onPlusClick(CartItem item) {
            item.setQuantity(item.getQuantity() + 1);
            updateCartQuantity(item);
            calculateTotal();
        }

        @Override
        public void onMinusClick(CartItem item) {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                updateCartQuantity(item);
            } else {
                showDeleteConfirmationDialog(item);
            }
            calculateTotal();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        TextView totalDelivery = findViewById(R.id.totalDelivery);
        TextView totalService = findViewById(R.id.totalService);
        TextView totalAll = findViewById(R.id.totalAll);
        totalDelivery.setText("đ 0");
        totalService.setText("đ 0");
        totalAll.setText("đ 0");
        emptyCartTextView = findViewById(R.id.textView7);
        recyclerView = findViewById(R.id.cartView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        AppCompatButton checkOutBtn = findViewById(R.id.checkOutBtn);
        checkOutBtn.setOnClickListener(v -> {
            String totalAmountStr = totalAll.getText().toString().replace("đ", "").replace(".", "").trim();
            int totalAmount = totalAmountStr.isEmpty() ? 0 : Integer.parseInt(totalAmountStr);

            Intent intent = new Intent(CartActivity.this, OrderActivity.class);
            intent.putExtra("totalAmount", totalAmount);
            startActivity(intent);
        });


        calculateTotal();
        getCartItems();
    }

    private void calculateTotal() {
        TextView totalDelivery = findViewById(R.id.totalDelivery);
        TextView totalService = findViewById(R.id.totalService);
        TextView totalAll = findViewById(R.id.totalAll);
        TextView textView24 = findViewById(R.id.textView24);
        int productTotal = calculateProductTotal();
        textView24.setText(formatCurrency(productTotal));
        String deliveryFeeStr = totalDelivery.getText().toString().replace("đ", "").replace(".", "").trim();
        String serviceFeeStr = totalService.getText().toString().replace("đ", "").replace(".", "").trim();
        int deliveryFee = deliveryFeeStr.isEmpty() ? 0 : Integer.parseInt(deliveryFeeStr);
        int serviceFee = serviceFeeStr.isEmpty() ? 0 : Integer.parseInt(serviceFeeStr);
        int total = productTotal + deliveryFee + serviceFee;
        totalAll.setText(formatCurrency(total));
    }

    private String formatCurrency(int amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + " đ";
    }

    private void getCartItems() {
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        ApiClient.setToken(token);
        Retrofit retrofit = ApiClient.getClient();
        CartApi cartApi = retrofit.create(CartApi.class);
        Call<List<CartItem>> call = cartApi.getCartItems();

        call.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItems = response.body();
                    if (cartItems.isEmpty()) {
                        emptyCartTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyCartTextView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        for (CartItem cartItem : cartItems) {
                            int productId = cartItem.getProductId();
                            getProductDetails(productId, cartItem);
                        }
                        adapter = new CartAdapter(cartItems, onItemClickListener);
                        recyclerView.setAdapter(adapter);
                    }
                    calculateTotal();
                } else {
                    Toast.makeText(CartActivity.this, "Không thể tải giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int calculateProductTotal() {
        int productTotal = 0;
        for (CartItem item : cartItems) {
            try {
                int itemTotal = Integer.parseInt(item.getTotalMoney());
                productTotal += itemTotal;
            } catch (NumberFormatException e) {
                Log.e("CartActivity", "Invalid total_money value for item: " + item.getProductName(), e);
            }
        }
        return productTotal;
    }


    private void getProductDetails(int productId, CartItem cartItem) {
        Retrofit retrofit = ApiClient.getClient();
        ProductApi productApi = retrofit.create(ProductApi.class);
        Call<Product> call = productApi.getProductById(productId);

        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body();
                    cartItem.setProductName(product.getName());
                    cartItem.setPrice(product.getPrice());
                    cartItem.setImageUrl(product.getImage());
                    if (adapter != null) {
                        adapter.updateCartItems(cartItems);
                    }
                } else {
                    Toast.makeText(CartActivity.this, "Không thể tải thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi kết nối API sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBack(View view) {
        finish();
    }

    private void updateCartQuantity(CartItem item) {
        int cartId = item.getId();
        int newQuantity = item.getQuantity();
        HashMap<String, Object> params = new HashMap<>();
        params.put("quantity", newQuantity);
        Gson gson = new Gson();
        String json = gson.toJson(params);
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        ApiClient.setToken(token);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        Retrofit retrofit = ApiClient.getClient();
        CartApi cartApi = retrofit.create(CartApi.class);
        Call<CartItem> call = cartApi.updateCartQuantity(cartId, requestBody);

        call.enqueue(new Callback<CartItem>() {
            @Override
            public void onResponse(Call<CartItem> call, Response<CartItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartItem updatedCartItem = response.body();
                    CartItem.UpdatedFields fields = updatedCartItem.getUpdatedFields();

                    if (fields != null) {
                        int quantity = fields.getQuantity();
                        int totalMoney = fields.getTotalMoney();
                        int index = cartItems.indexOf(item);
                        if (index != -1) {
                            CartItem currentItem = cartItems.get(index);
                            currentItem.setQuantity(quantity);
                            currentItem.setTotalMoney(String.valueOf(totalMoney));
                            adapter.notifyItemChanged(index);
                            calculateTotal();
                        }
                    } else {
                        Toast.makeText(CartActivity.this, "Cập nhật giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CartActivity.this, "Cập nhật giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartItem> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog(CartItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteCartItem(item.getId());
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteCartItem(int cartId) {
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        ApiClient.setToken(token);
        Retrofit retrofit = ApiClient.getClient();
        CartApi cartApi = retrofit.create(CartApi.class);

        Call<Void> call = cartApi.deleteCartItem(cartId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API_RESPONSE", "Item deleted successfully.");
                    int index = findItemIndexById(cartId);
                    if (index != -1) {
                        cartItems.remove(index);
                        adapter.notifyItemRemoved(index);
                    }
                } else {
                    Log.d("API_RESPONSE", "Failed to delete item.");
                }
                calculateTotal();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("API_ERROR", "Error: " + t.getMessage());
            }
        });
    }

    private int findItemIndexById(int id) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

}
