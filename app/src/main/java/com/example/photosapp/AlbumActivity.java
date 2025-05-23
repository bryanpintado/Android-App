package com.example.photosapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;
import com.example.photosapp.model.Photo;
import com.example.photosapp.model.UserManager;
import androidx.appcompat.app.AppCompatActivity;
import com.example.photosapp.model.Album;
import com.example.photosapp.model.User;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static final int REQUEST_DISPLAY_PHOTO = 100;
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
            Intent i = new Intent(this, PhotoDisplayActivity.class);
            i.putExtra("album_name", albumName);
            i.putExtra("position", position);
            startActivityForResult(i, REQUEST_DISPLAY_PHOTO);
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
            return true;
        });

        loadAlbum();

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });
        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            Intent searchIntent = new Intent(AlbumActivity.this, SearchActivity.class);
            startActivity(searchIntent);
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

        if (requestCode == REQUEST_DISPLAY_PHOTO && resultCode == RESULT_OK) {
            loadAlbum();
            return;
        }

        if (requestCode == REQUEST_CODE_PICK_IMAGE
                && resultCode == RESULT_OK
                && data != null) {

            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null && currentAlbum != null) {
                try {
                    getContentResolver()
                            .takePersistableUriPermission(
                                    selectedImageUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String path    = getRealPathFromURI(selectedImageUri);
                String caption = getDisplayNameFromUri(selectedImageUri);

                Photo newPhoto = new Photo(path, caption);
                currentAlbum.addPhoto(newPhoto);
                UserManager.getInstance().saveUsers(this);

                photoList.clear();
                photoList.addAll(currentAlbum.getPhotos());
                photoAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Error selecting photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getDisplayNameFromUri(Uri uri) {
        String name = "Unknown";
        Cursor cursor = getContentResolver().query(
                uri,
                new String[]{OpenableColumns.DISPLAY_NAME},
                null, null, null
        );
        if (cursor != null && cursor.moveToFirst()) {
            int idx = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME);
            name = cursor.getString(idx);
            cursor.close();
        }
        return name;
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