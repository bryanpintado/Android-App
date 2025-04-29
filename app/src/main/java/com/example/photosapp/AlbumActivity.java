package com.example.photosapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import com.example.photosapp.PhotoAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.example.photosapp.model.Photo;
import com.example.photosapp.model.UserManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.photosapp.model.Album;
import com.example.photosapp.model.Photo;
import com.example.photosapp.model.User;
import com.example.photosapp.model.UserManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1;

    private ListView photoListView;
    private Button addPhotoButton;
    private PhotoAdapter photoAdapter;
    private List<Photo> photoList;

    private Album currentAlbum;
    private String albumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        albumName = getIntent().getStringExtra("album_name");
        if (albumName == null) {
            Toast.makeText(this, "No album specified", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        photoListView = findViewById(R.id.photoListView);
        addPhotoButton = findViewById(R.id.addPhotoButton);

        photoList = new ArrayList<>();

        // Initialize adapter before loading data
        photoAdapter = new PhotoAdapter(this, R.layout.list_item_photo, photoList);
        photoListView.setAdapter(photoAdapter);
        // Open full-screen view on single tap
        photoListView.setOnItemClickListener((parent, view, position, id) -> {
            Photo clicked = photoList.get(position);
            Intent i = new Intent(AlbumActivity.this, PhotoDisplayActivity.class);
            i.putExtra("photo", clicked);
            startActivity(i);
        });
        // Remove photo on long press
        photoListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Photo toRemove = photoList.get(position);
            new AlertDialog.Builder(AlbumActivity.this)
                    .setTitle("Delete Photo")
                    .setMessage("Remove this photo from the album?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        currentAlbum.removePhoto(toRemove);                    // update model
                        UserManager.getInstance().saveUsers(AlbumActivity.this); // persist change
                        photoList.remove(position);                            // update list
                        photoAdapter.notifyDataSetChanged();                   // refresh UI
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;  // consume the long-click
        });

        // Now load the album (safe to notify the adapter)
        loadAlbum();

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });
    }

    private void loadAlbum() {
        photoList.clear();
        User owner = UserManager.getInstance().getUserByUsername("owner");
        if (owner != null) {
            Album album = owner.getAlbumByName(albumName);
            currentAlbum = album;
            if (album != null) {
                photoList.addAll(album.getPhotos());
            }
        }
        photoAdapter.notifyDataSetChanged();
    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null && currentAlbum != null) {
                try {
                    getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Photo newPhoto = new Photo(selectedImageUri);
                currentAlbum.addPhoto(newPhoto);
                UserManager.getInstance().saveUsers(this);

                photoList.add(newPhoto);
                photoAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Error selecting photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getDisplayNameFromUri(Uri uri) {
        String displayName = "Unknown";

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    displayName = cursor.getString(nameIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return displayName;
    }
    private String getRealPathFromURI(Uri uri) {
        String fileName = getDisplayNameFromUri(uri);
        File file = new File(getCacheDir(), fileName);

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {

            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }
}