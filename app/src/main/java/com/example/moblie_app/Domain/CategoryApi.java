package com.example.moblie_app.Domain;
import com.example.moblie_app.ViewModel.Category;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryApi {
    @GET("/api/category")
    Call<List<Category>> getCategoryList();
}
