package com.example.moblie_app.Domain;

import com.example.moblie_app.ViewModel.CartItem;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartApi {
    @POST("/api/cart")
    Call<CartItem> createCart(@Body RequestBody requestBody);

    @GET("/api/cart")
    Call<List<CartItem>> getCartItems();

    @PUT("/api/cart/{id}")
    Call<CartItem> updateCartQuantity(@Path("id") int id, @Body RequestBody requestBody);

    @DELETE("/api/cart/{id}")
    Call<Void> deleteCartItem(@Path("id") int id);

}
