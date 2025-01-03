package com.example.moblie_app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moblie_app.Adapter.ProductAdapter;
import com.example.moblie_app.Domain.ProductApi;
import com.example.moblie_app.R;
import com.example.moblie_app.ViewModel.Product;
import com.example.moblie_app.api.ApiClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProductActivity extends AppCompatActivity {
    private ProgressBar productProgressBar;
    private RecyclerView productRecyclerView;
    private List<Product> tempProductList = new ArrayList<>();
    private List<Product> originalProductList = new ArrayList<>();

    private EditText searchEditText;
    private boolean hasMoreProducts = true;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product);
        initViews();
        setupProductRecyclerView();
        setupSearchFunctionality();
    }

    private void initViews() {
        searchEditText = findViewById(R.id.editTextSearch);
        productRecyclerView = findViewById(R.id.viewProduct);
        productProgressBar = findViewById(R.id.progressBarProduct);
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
                    Toast.makeText(ProductActivity.this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                    productProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ProductActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                productProgressBar.setVisibility(View.GONE);
            }
        });
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

    private void setupProductRecyclerView() {
        productProgressBar.setVisibility(View.VISIBLE);
        productRecyclerView.setVisibility(View.GONE);
        Spinner spinnerSort = findViewById(R.id.spinnerSort);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                sortProducts(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        Spinner spinnerPriceRange = findViewById(R.id.spinnerPriceRange);
        spinnerPriceRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                filterByPriceRange(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        handler.postDelayed(() -> {
            loadProductData(0, 100);
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
            productRecyclerView.setLayoutManager(layoutManager);
            productRecyclerView.setVisibility(View.VISIBLE);
            productProgressBar.setVisibility(View.GONE);
        }, 2000);
    }

    private void sortProducts(int sortOption) {
        switch (sortOption) {
            case 1:
                Collections.sort(originalProductList, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        double price1 = Double.parseDouble(p1.getPrice());
                        double price2 = Double.parseDouble(p2.getPrice());
                        return Double.compare(price1, price2);
                    }
                });
                break;
            case 2:
                Collections.sort(originalProductList, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        double price1 = Double.parseDouble(p1.getPrice());
                        double price2 = Double.parseDouble(p2.getPrice());
                        return Double.compare(price2, price1);
                    }
                });
                break;
            default:
                break;
        }
        restoreOriginalProducts();
    }

    private void filterByPriceRange(int priceRangeOption) {
        if (originalProductList == null || originalProductList.isEmpty()) return;

        List<Product> filteredList = new ArrayList<>();
        for (Product product : originalProductList) {
            double price = 0;
            try {
                price = Double.parseDouble(product.getPrice());
            } catch (NumberFormatException e) {
                continue;
            }

            switch (priceRangeOption) {
                case 1:
                    if (price >= 0 && price <= 1000000) {
                        filteredList.add(product);
                    }
                    break;
                case 2:
                    if (price > 1000000 && price <= 10000000) {
                        filteredList.add(product);
                    }
                    break;
                case 3:
                    if (price > 10000000) {
                        filteredList.add(product);
                    }
                    break;
                default:
                    filteredList.addAll(originalProductList);
                    break;
            }
        }
        ProductAdapter adapter = new ProductAdapter(filteredList);
        productRecyclerView.setAdapter(adapter);
    }

    private void restoreOriginalProducts() {
        if (!originalProductList.isEmpty()) {
            ProductAdapter adapter = new ProductAdapter(originalProductList);
            productRecyclerView.setAdapter(adapter);
            productProgressBar.setVisibility(View.GONE);
            productRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void onBack(View view) {
        finish();
    }
}
