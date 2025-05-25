package com.example.rm4rysowanie;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BrowseActivity extends AppCompatActivity implements ItemFragment.ItemClickedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browse);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
    @Override
    public void itemClicked(Image item) {
        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            Intent intent = new Intent(this, ViewActivity.class);
            intent.putExtra("item", item);
            startActivity(intent);
        }
        else {
            ImageFragment imageFragment = (ImageFragment)
                    getSupportFragmentManager().findFragmentById(R.id.imageFragment);
            imageFragment.setImage(item);
        }
    }
}