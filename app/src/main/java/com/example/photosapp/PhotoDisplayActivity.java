package com.example.photosapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.photosapp.model.Photo;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.photosapp.model.Album;
import com.example.photosapp.model.Photo;
import com.example.photosapp.model.User;
import com.example.photosapp.model.UserManager;
import java.util.ArrayList;
import java.util.List;

public class PhotoDisplayActivity extends AppCompatActivity {

    private ImageView fullImageView;
    private List<Photo> photos;
    private int position;
    private Button btnPrevious;
    private Button btnNext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display);

        fullImageView = findViewById(R.id.fullImageView);
        btnPrevious   = findViewById(R.id.btnPrevious);
        btnNext       = findViewById(R.id.btnNext);

        Intent intent = getIntent();
        String albumName = intent.getStringExtra("album_name");
        position = intent.getIntExtra("position", 0);

        User owner = UserManager.getInstance().getUserByUsername("owner");
        if (owner != null) {
            Album album = owner.getAlbumByName(albumName);
            photos = (album != null) ? album.getPhotos() : new ArrayList<>();
        } else {
            photos = new ArrayList<>();
        }

        if (!photos.isEmpty() && position >= 0 && position < photos.size()) {
            Uri uri = Uri.parse(photos.get(position).getFileUri());
            fullImageView.setImageURI(uri);
        }

        btnPrevious.setOnClickListener(v -> {
            if (position > 0) {
                position--;
                Uri uriPrev = Uri.parse(photos.get(position).getFileUri());
                fullImageView.setImageURI(uriPrev);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (position < photos.size() - 1) {
                position++;
                Uri uriNext = Uri.parse(photos.get(position).getFileUri());
                fullImageView.setImageURI(uriNext);
            }
        });
    }
}