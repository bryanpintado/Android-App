package com.example.photosapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.photosapp.model.Photo;
import android.widget.Button;
import com.example.photosapp.model.Album;
import com.example.photosapp.model.User;
import com.example.photosapp.model.UserManager;
import java.util.ArrayList;
import java.util.List;
import android.widget.ListView;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.Toast;

import com.example.photosapp.model.Tag;


public class PhotoDisplayActivity extends AppCompatActivity {

    private ImageView fullImageView;
    private List<Photo> photos;
    private int position;
    private Button btnPrevious;
    private Button btnNext;

    private ListView   tagListView;
    private Button     btnAddTag;
    private ArrayAdapter<String> tagAdapter;
    private List<String>       tagStrings;
    private Photo              currentPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display);

        fullImageView = findViewById(R.id.fullImageView);
        btnPrevious   = findViewById(R.id.btnPrevious);
        btnNext       = findViewById(R.id.btnNext);
        tagListView   = findViewById(R.id.tagListView);
        btnAddTag     = findViewById(R.id.btnAddTag);

        Intent intent     = getIntent();
        String albumName  = intent.getStringExtra("album_name");
        position          = intent.getIntExtra("position", 0);

        User owner = UserManager.getInstance().getUserByUsername("owner");
        if (owner != null) {
            Album album = owner.getAlbumByName(albumName);
            photos = (album != null) ? album.getPhotos() : new ArrayList<>();
        } else {
            photos = new ArrayList<>();
        }

        currentPhoto = (photos.size() > position ? photos.get(position) : null);
        tagStrings   = new ArrayList<>();
        tagAdapter   = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tagStrings);
        tagListView.setAdapter(tagAdapter);

        if (currentPhoto != null) {
            for (Tag t : currentPhoto.getTags()) {
                tagStrings.add(t.getType() + ": " + t.getValue());
            }
        }
        tagAdapter.notifyDataSetChanged();

        if (currentPhoto != null) {
            Uri uri = Uri.parse(currentPhoto.getFileUri());
            fullImageView.setImageURI(uri);
        }

        btnPrevious.setOnClickListener(v -> {
            if (position > 0) {
                position--;
                currentPhoto = photos.get(position);
                fullImageView.setImageURI(Uri.parse(currentPhoto.getFileUri()));
                // reload tags for new photo
                tagStrings.clear();
                for (Tag t : currentPhoto.getTags()) {
                    tagStrings.add(t.getType() + ": " + t.getValue());
                }
                tagAdapter.notifyDataSetChanged();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (position < photos.size() - 1) {
                position++;
                currentPhoto = photos.get(position);
                fullImageView.setImageURI(Uri.parse(currentPhoto.getFileUri()));
                // reload tags for new photo
                tagStrings.clear();
                for (Tag t : currentPhoto.getTags()) {
                    tagStrings.add(t.getType() + ": " + t.getValue());
                }
                tagAdapter.notifyDataSetChanged();
            }
        });

        btnAddTag.setOnClickListener(v -> {
            String[] types = {"person", "location"};
            new AlertDialog.Builder(this)
                    .setTitle("Select tag type")
                    .setItems(types, (dialog, which) -> {
                        String chosenType = types[which];
                        EditText input = new EditText(this);
                        new AlertDialog.Builder(this)
                                .setTitle("Enter " + chosenType)
                                .setView(input)
                                .setPositiveButton("OK", (d, w) -> {
                                    String val = input.getText().toString().trim();
                                    if (!val.isEmpty() && currentPhoto != null) {
                                        Tag newTag = new Tag(chosenType, val);
                                        if (currentPhoto.getTags().contains(newTag)) {
                                            Toast.makeText(PhotoDisplayActivity.this,
                                                    "Tag \"" + chosenType + ": " + val + "\" already exists",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            currentPhoto.getTags().add(newTag);
                                            UserManager.getInstance().saveUsers(PhotoDisplayActivity.this);
                                            tagStrings.add(chosenType + ": " + val);
                                            tagAdapter.notifyDataSetChanged();
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    })
                    .show();
        });

        tagListView.setOnItemLongClickListener((parent, view, pos, id) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete tag")
                    .setMessage("Remove this tag?")
                    .setPositiveButton("Delete", (d, w) -> {
                        if (currentPhoto != null) {
                            currentPhoto.getTags().remove(pos);
                            UserManager.getInstance().saveUsers(this);
                            tagStrings.remove(pos);
                            tagAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });
    }
}