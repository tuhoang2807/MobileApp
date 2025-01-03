package com.example.moblie_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.moblie_app.Adapter.CategoryAdapter;
import com.example.moblie_app.Adapter.ImageSliderAdapter;
import com.example.moblie_app.Adapter.ProductAdapter;
import com.example.moblie_app.Domain.AuthApi;
import com.example.moblie_app.Domain.CategoryApi;
import com.example.moblie_app.Domain.ProductApi;
import com.example.moblie_app.R;
import com.example.moblie_app.ViewModel.Author;
import com.example.moblie_app.ViewModel.Category;
import com.example.moblie_app.ViewModel.Product;
import com.example.moblie_app.api.ApiClient;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private RecyclerView categoryRecyclerView, productRecyclerView;
    private ProgressBar categoryProgressBar, productProgressBar;
    private ViewPager2 viewPager2;
    private ProgressBar bannerProgressBar;
    private DotsIndicator dotsIndicator;
    private List<Product> tempProductList = new ArrayList<>();
    private int currentPage = 0;
    private EditText searchEditText;
    private final Handler handler = new Handler();
    private SharedPreferences sharedPreferences;

    private TextView userNameTextView;
    private final int[] images = new int[]{
            R.drawable.banner1, R.drawable.banner_3, R.drawable.banner_4
    };
    private List<Product> originalProductList = new ArrayList<>();

    private boolean hasMoreProducts = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setupViewPager();
        setupCategoryRecyclerView();
        setupProductRecyclerView();
        setupSearchFunctionality();
        setupProductRecyclerView();
        TextView welcomeTextView = findViewById(R.id.textView4);
        userNameTextView = findViewById(R.id.textView5);
        Button loginButton = findViewById(R.id.loginButton);
        String token = sharedPreferences.getString("token", null);
        if (token == null) {
            loginButton.setVisibility(View.VISIBLE);
            welcomeTextView.setVisibility(View.GONE);
            userNameTextView.setVisibility(View.GONE);
        } else {
            getInformation();
            loginButton.setVisibility(View.GONE);
            welcomeTextView.setVisibility(View.VISIBLE);
            userNameTextView.setVisibility(View.VISIBLE);
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private void initViews() {
        searchEditText = findViewById(R.id.editTextText);
        viewPager2 = findViewById(R.id.viewPager2);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        bannerProgressBar = findViewById(R.id.progressBar);

        categoryRecyclerView = findViewById(R.id.viewCategory);
        categoryProgressBar = findViewById(R.id.progressBarCategory);

        productRecyclerView = findViewById(R.id.viewProduct);
        productProgressBar = findViewById(R.id.progressBarProduct);
    }
    private void setupViewPager() {
        ImageSliderAdapter adapter = new ImageSliderAdapter(this, images);
        viewPager2.setAdapter(adapter);
        viewPager2.setVisibility(View.GONE);
        dotsIndicator.setVisibility(View.GONE);
        bannerProgressBar.setVisibility(View.VISIBLE);

        handler.postDelayed(() -> {
            viewPager2.setVisibility(View.VISIBLE);
            dotsIndicator.setVisibility(View.VISIBLE);
            bannerProgressBar.setVisibility(View.GONE);
            dotsIndicator.setViewPager2(viewPager2);
            autoSlideImages();
        }, 1500);
    }
    private void autoSlideImages() {
        final Runnable runnable = () -> {
            if (currentPage == images.length) currentPage = 0;
            viewPager2.setCurrentItem(currentPage++, true);
            handler.postDelayed(this::autoSlideImages, 6000);
        };

        handler.post(runnable);
    }
    private void setupCategoryRecyclerView() {
        categoryProgressBar.setVisibility(View.VISIBLE);
        categoryRecyclerView.setVisibility(View.GONE);
        Retrofit retrofit = ApiClient.getClient();
        CategoryApi categoryApi = retrofit.create(CategoryApi.class);
        Call<List<Category>> call = categoryApi.getCategoryList();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categoryList = response.body();
                    new Handler().postDelayed(() -> {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        categoryRecyclerView.setLayoutManager(layoutManager);
                        CategoryAdapter categoryAdapter = new CategoryAdapter(categoryList, id -> {
                            Log.d("CategoryClick", "Clicked category ID: " + id);
                            fetchProductsByCategory(id);
                        });
                        categoryRecyclerView.setAdapter(categoryAdapter);
                        categoryProgressBar.setVisibility(View.GONE);
                        categoryRecyclerView.setVisibility(View.VISIBLE);
                    }, 2000);
                } else {
                    Log.e("Category", "Error loading categories: " + response.message());
                    categoryProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("Category", "Failed to load categories", t);
                categoryProgressBar.setVisibility(View.GONE);
            }
        });
    }
    private void fetchProductsByCategory(Object categoryId) {
        productProgressBar.setVisibility(View.VISIBLE);
        productRecyclerView.setVisibility(View.GONE);
        Retrofit retrofit = ApiClient.getClient();
        ProductApi productApi = retrofit.create(ProductApi.class);
        Call<List<Product>> call = productApi.getProductsByCategoryId((Integer) categoryId);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> productList = response.body();
                    tempProductList = productList;
                    ProductAdapter adapter = new ProductAdapter(productList);
                    productRecyclerView.setAdapter(adapter);
                    productProgressBar.setVisibility(View.GONE);
                    productRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(MainActivity.this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                    productProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                productProgressBar.setVisibility(View.GONE);
            }
        });
    }
    private void setupProductRecyclerView() {
        productProgressBar.setVisibility(View.VISIBLE);
        productRecyclerView.setVisibility(View.GONE);

        handler.postDelayed(() -> {
            loadProductData(0, 100);
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
            productRecyclerView.setLayoutManager(layoutManager);
            productRecyclerView.setVisibility(View.VISIBLE);
            productProgressBar.setVisibility(View.GONE);
        }, 2000);
    }
    private void loadProductData(int startIndex, int endIndex) {
        Retrofit retrofit = ApiClient.getClient();
        ProductApi productApi = retrofit.create(ProductApi.class);
        Call<List<Product>> call = productApi.getProductList(startIndex, endIndex);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> productList = response.body();
                    if (startIndex == 0) {
                        originalProductList.clear();
                        originalProductList.addAll(productList);
                        ProductAdapter adapter = new ProductAdapter(productList);
                        productRecyclerView.setAdapter(adapter);
                    } else {
                        ProductAdapter adapter = (ProductAdapter) productRecyclerView.getAdapter();
                        if (adapter != null) {
                            adapter.addProducts(productList);
                        }
                    }
                    productProgressBar.setVisibility(View.GONE);
                    productRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    hasMoreProducts = false;
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                hasMoreProducts = false;
            }
        });
    }
    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString().trim();
                if (!query.isEmpty()) {
                    searchProducts(query);
                } else {
                    restoreOriginalProducts();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    private void restoreOriginalProducts() {
        if (!originalProductList.isEmpty()) {
            ProductAdapter adapter = new ProductAdapter(originalProductList);
            productRecyclerView.setAdapter(adapter);
            productProgressBar.setVisibility(View.GONE);
            productRecyclerView.setVisibility(View.VISIBLE);
        }
    }
    private void searchProducts(String query) {
        productProgressBar.setVisibility(View.VISIBLE);
        productRecyclerView.setVisibility(View.GONE);
        Retrofit retrofit = ApiClient.getClient();
        ProductApi productApi = retrofit.create(ProductApi.class);
        Map<String, String> params = new HashMap<>();
        params.put("term", query);
        Call<List<Product>> call = productApi.searchProducts(params);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> productList = response.body();
                    tempProductList = productList;
                    ProductAdapter adapter = new ProductAdapter(productList);
                    productRecyclerView.setAdapter(adapter);
                    productProgressBar.setVisibility(View.GONE);
                    productRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(MainActivity.this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                    productProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                productProgressBar.setVisibility(View.GONE);
            }
        });
    }
    private void showSavedProducts() {
        if (!tempProductList.isEmpty()) {
            ProductAdapter adapter = new ProductAdapter(tempProductList);
            productRecyclerView.setAdapter(adapter);
            productProgressBar.setVisibility(View.GONE);
            productRecyclerView.setVisibility(View.VISIBLE);
        } else {
            loadProductData(0, 10);
        }
    }
    public void getInformation() {
        String token = sharedPreferences.getString("token", null);
        ApiClient.setToken(token);
        Retrofit retrofit = ApiClient.getClient();
        AuthApi authApi = retrofit.create(AuthApi.class);
        if (token == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        int userId = sharedPreferences.getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy ID người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<List<Author>> call = authApi.getUserById(userId);
        call.enqueue(new Callback<List<Author>>() {
            @Override
            public void onResponse(Call<List<Author>> call, Response<List<Author>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Author> authors = response.body();
                    if (!authors.isEmpty()) {
                        Author author = authors.get(0);
                        userNameTextView.setText(author.getUsername());
                    } else {
                        Toast.makeText(MainActivity.this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = "Lỗi: Mã phản hồi " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (Exception e) {
                            errorMessage += " - Không thể đọc nội dung lỗi.";
                        }
                    }
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Author>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Không thể kết nối tới server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void openPersonalInfoPage(View view) {
        Intent intent = new Intent(this, InforMenuActivity.class);
        startActivity(intent);
    }
    public void openConnectPage(View view) {
        Intent intent = new Intent(this, ConnectActivity.class);
        startActivity(intent);
    }
    public void openCartPage(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }
    public void goToNofication(View view){
        Intent intent = new Intent(this, NoficationActivity.class);
        startActivity(intent);
    }

    public void goToAllProduct(View view){
        Intent intent = new Intent(this, ProductActivity.class);
        startActivity(intent);
    }
}
