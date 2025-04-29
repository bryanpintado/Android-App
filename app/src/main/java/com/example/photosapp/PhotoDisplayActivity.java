package com.example.photosapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.photosapp.model.Photo;

public class PhotoDisplayActivity extends AppCompatActivity {

    private ImageView fullImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display);

        fullImageView = findViewById(R.id.fullImageView);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("photo")) {
            Photo photo = (Photo) intent.getSerializableExtra("photo");
            if (photo != null) {
                try {
                    Uri uri = Uri.parse(photo.getFileUri());
                    fullImageView.setImageURI(uri);
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "No photo to display.", Toast.LENGTH_SHORT).show();
        }
    }
}