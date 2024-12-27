package com.example.moblie_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moblie_app.Domain.AuthApi;
import com.example.moblie_app.R;
import com.example.moblie_app.Utils.DateUtils;
import com.example.moblie_app.ViewModel.Author;
import com.example.moblie_app.api.ApiClient;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PersonalInfoActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etDob;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etDob = findViewById(R.id.etDob);
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        onLoadPersonalInfo();
    }

    private void onLoadPersonalInfo() {
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
                Log.d("PersonalInfo", "API URL: " + call.request().url());
                if (response.isSuccessful() && response.body() != null) {
                    List<Author> authors = response.body();
                    if (!authors.isEmpty()) {
                        Author author = authors.get(0);
                        etUsername.setText(author.getUsername());
                        etEmail.setText(author.getEmail());
                        String formattedDob = DateUtils.formatDate(author.getBirthday());
                        etDob.setText(formattedDob);
                    } else {
                        Toast.makeText(PersonalInfoActivity.this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.errorBody() != null) {
                        try {
                            String errorResponse = response.errorBody().string();
                            Toast.makeText(PersonalInfoActivity.this, "Lỗi: " + errorResponse, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(PersonalInfoActivity.this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    Toast.makeText(PersonalInfoActivity.this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Author>> call, Throwable t) {
                Log.e("PersonalInfo", "Lỗi API: " + t.getMessage());
                Toast.makeText(PersonalInfoActivity.this, "Không thể kết nối tới server!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updatePersonalInfo(View view) {
        String email = etEmail.getText().toString().trim();
        String birthday = etDob.getText().toString().trim();

        if (email.isEmpty() || birthday.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = sharedPreferences.getString("token", null);
        if (token == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để cập nhật thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        String formattedBirthday = formatDate(birthday);
        if (formattedBirthday == null) {
            Toast.makeText(PersonalInfoActivity.this, "Ngày tháng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }


        HashMap<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("birthday", formattedBirthday);

        RequestBody requestBody = RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                new Gson().toJson(userData)
        );

        int userId = sharedPreferences.getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy ID người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.setToken(token);
        Retrofit retrofit = ApiClient.getClient();
        AuthApi authApi = retrofit.create(AuthApi.class);

        Call<Author> call = authApi.updateUser(userId, requestBody);
        call.enqueue(new Callback<Author>() {
            @Override
            public void onResponse(Call<Author> call, Response<Author> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(PersonalInfoActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(PersonalInfoActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PersonalInfoActivity.this, "Lỗi khi cập nhật thông tin!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Author> call, Throwable t) {
                Toast.makeText(PersonalInfoActivity.this, "Không thể kết nối tới server!", Toast.LENGTH_SHORT).show();
            }
        });
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

    public void onBack(View view) {
        finish();
    }

    private String formatDate(String birthday) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = inputFormat.parse(birthday);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
