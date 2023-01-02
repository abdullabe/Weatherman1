package com.project.weatherman.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.project.weatherman.R;

public class BlogDetailsActivity extends AppCompatActivity {
    String title,body_content,image;
    private Toolbar toolbar;
    ImageView img;
    TextView txtDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Blog");
        toolbar.setTitleTextColor(ContextCompat.getColor(BlogDetailsActivity.this, R.color.white));
        setActionBar(toolbar);
        title = getIntent().getStringExtra("title");
        body_content = getIntent().getStringExtra("body_content");
        image = getIntent().getStringExtra("image");

        img=(ImageView)findViewById(R.id.img);
        txtDescription=(TextView)findViewById(R.id.txtDescription);

        Glide.with(this).load(image).into(img);
        txtDescription.setText(body_content);


        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //What to do on back clicked
            }
        });
    }
}