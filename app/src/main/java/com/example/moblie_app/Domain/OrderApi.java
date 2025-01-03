package com.example.moblie_app.Domain;
import com.example.moblie_app.ViewModel.OrderItem;
import com.example.moblie_app.ViewModel.Product;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OrderApi {
    @POST("/api/order")
    Call<OrderItem> createOrder(@Body RequestBody requestBody);

    @GET("api/order/getUserOrder")
    Call<List<OrderItem>> getUserOrder();
    @PUT("api/order/cancel/{orderId}")
    Call<Void> cancelOrder(@Path("orderId") int orderId);

    @GET("api/order/items/{orderId}")
    Call<OrderItem> getDetailOrder(@Path("orderId") int orderId);

    @GET("api/order/{orderId}")
    Call<OrderItem> getInformationOrder(@Path("orderId") int orderId);

    @GET("/api/order/items/{orderId}")
    Call<List<Product>> getProductItem(@Path("orderId")int orderId);

    @POST("/api/order/payment")
    Call<OrderItem>paymentOnline(@Body RequestBody requestBody);

}
