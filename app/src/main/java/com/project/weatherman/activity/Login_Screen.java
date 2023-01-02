package com.project.weatherman.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.project.weatherman.R;

public class Login_Screen extends AppCompatActivity {
Button login_btn_submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        login_btn_submit=(Button)findViewById(R.id.login_btn_submit);
        login_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Login_Screen.this,Verification_Screen.class);
                startActivity(i);
            }
        });

    }
}