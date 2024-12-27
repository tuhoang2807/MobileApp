package com.example.moblie_app.Domain;

import com.example.moblie_app.ViewModel.Product;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;
import java.util.Map;

public interface ProductApi {
    @GET("/api/product")
    Call<List<Product>> getProductList(@Query("start") int startIndex, @Query("end") int endIndex);

    @GET("/api/product/{id}")
    Call<Product> getProductById(@Path("id") int productId);

    @POST("/api/product/search")
    Call<List<Product>> searchProducts(@Body Map<String, String> body);

    @GET("/api/category/{id}")
    Call<List<Product>> getProductsByCategoryId(@Path("id") int id);
}
