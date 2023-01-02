package com.project.weatherman.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.project.weatherman.R;

public class GifActivity extends AppCompatActivity {
ImageView imgGifView;
    MaterialCardView cardView1,cardView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);
        init();
        function();
    }
    private void init(){
        imgGifView=(ImageView)findViewById(R.id.imgGifView);
        cardView1=(MaterialCardView) findViewById(R.id.card_1);
        cardView2=(MaterialCardView)findViewById(R.id.card_2);
    }
    private void function(){
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(GifActivity.this).load(R.drawable.radar_gif).into(imgGifView);
            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(GifActivity.this).load(R.drawable.dynamic_gif).into(imgGifView);
            }
        });
    }
}