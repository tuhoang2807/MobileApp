package com.example.moblie_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moblie_app.R;
import com.example.moblie_app.ViewModel.Author;
import com.example.moblie_app.api.ApiClient;
import com.example.moblie_app.Domain.AuthApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, emailInput, birthdayInput, passwordInput, confirmPasswordInput;
    private Button registerButton;
    private TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        birthdayInput = findViewById(R.id.birthdayInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        loginText = findViewById(R.id.loginText);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString();
                String email = emailInput.getText().toString();
                String birthday = birthdayInput.getText().toString();
                String password = passwordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                String formattedBirthday = formatDate(birthday);
                if (formattedBirthday == null) {
                    Toast.makeText(RegisterActivity.this, "Ngày tháng không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("username", username);
                    jsonObject.put("password", password);
                    jsonObject.put("role_id", 2);
                    jsonObject.put("email", email);
                    jsonObject.put("birthday", formattedBirthday);
                    registerUser(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Lỗi khi tạo dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    private void registerUser(JSONObject jsonObject) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        AuthApi authApi = ApiClient.getClient().create(AuthApi.class);
        Call<Author> call = authApi.register(requestBody);
        call.enqueue(new Callback<Author>() {
            @Override
            public void onResponse(Call<Author> call, Response<Author> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        String message = "Đăng ký thành công cho " + response.body().getUsername();
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Dữ liệu trả về từ server là null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        Toast.makeText(RegisterActivity.this, "Lỗi: " + errorResponse, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this, "Lỗi: ", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onFailure(Call<Author> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}

