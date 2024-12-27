package com.example.moblie_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moblie_app.R;

public class ConnectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connect);
    }
    public void openPersonalInfoPage(View view) {
            Intent intent = new Intent(this, InforMenuActivity.class);
            startActivity(intent);
    }
    public void openHomePage(View view) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
    }
    public void openCartPage(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }
}