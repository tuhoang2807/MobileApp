package com.example.moblie_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moblie_app.Domain.AuthApi;
import com.example.moblie_app.R;
import com.example.moblie_app.ViewModel.Author;
import com.example.moblie_app.api.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InforMenuActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private TextView userName;
    private TextView userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor_menu);
        EdgeToEdge.enable(this);
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);

        if (token == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        getInformation();
    }
    public void openHomePage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
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
    public void openInforActivePage(View view) {
        Intent intent = new Intent(this, PersonalInfoActivity.class);
        startActivity(intent);
    }
    public void logOut(View view) {
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
                        userName.setText(author.getUsername());
                        userEmail.setText(author.getEmail());
                    } else {
                        Toast.makeText(InforMenuActivity.this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(InforMenuActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Author>> call, Throwable t) {
                Toast.makeText(InforMenuActivity.this, "Không thể kết nối tới server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}