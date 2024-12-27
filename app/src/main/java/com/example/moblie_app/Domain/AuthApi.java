package com.example.moblie_app.Domain;

import com.example.moblie_app.ViewModel.Author;
import com.example.moblie_app.ViewModel.Product;

import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AuthApi {
    @POST("api/user/register")
    Call<Author> register(@Body RequestBody requestBody);

    @POST("/api/user/login")
    Call<Author> login(@Body RequestBody requestBody);

    @GET("/api/user/getUser/{id}")
    Call<List<Author>> getUserById(@Path("id") int userId);

    @PUT("api/user/update/{id}")
    Call<Author> updateUser(@Path("id") int userId, @Body RequestBody requestBody);

}

