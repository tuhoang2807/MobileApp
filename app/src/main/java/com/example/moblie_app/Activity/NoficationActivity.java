package com.example.moblie_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moblie_app.R;

public class NoficationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nofication);
    }

    public void goToOrderNofication(View view){
        Intent intent = new Intent(this, OrderNoficationActivity.class);
        startActivity(intent);
    }
     public void onBack(){
        finish();
     }
}