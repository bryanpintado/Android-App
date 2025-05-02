package com.example.photosapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.app.AlertDialog;
import android.widget.EditText;

import com.example.photosapp.model.Album;
import com.example.photosapp.model.User;
import com.example.photosapp.model.UserManager;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ListView albumListView;
    private Button addAlbumButton;
    private ArrayAdapter<String> albumAdapter;
    private ArrayList<String> albumNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        albumListView = findViewById(R.id.albumListView);
        addAlbumButton = findViewById(R.id.addAlbumButton);

        UserManager.getInstance().loadUsers(this);

        albumNames = new ArrayList<>();
        loadAlbums();

        albumAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, albumNames);
        albumListView.setAdapter(albumAdapter);

        albumListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedAlbum = albumNames.get(position);
            Intent intent = new Intent(HomeActivity.this, AlbumActivity.class);
            intent.putExtra("album_name", selectedAlbum);
            startActivity(intent);
        });

        addAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewAlbum();
            }
        });

        albumListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String selectedAlbum = albumNames.get(position);
            User owner = UserManager.getInstance().getUserByUsername("owner");

            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle(selectedAlbum)
                    .setItems(new CharSequence[]{"Rename", "Delete"}, (dialog, which) -> {
                        if (which == 0) {
                            // RENAME
                            EditText input = new EditText(HomeActivity.this);
                            input.setText(selectedAlbum);
                            new AlertDialog.Builder(HomeActivity.this)
                                    .setTitle("Rename Album")
                                    .setView(input)
                                    .setPositiveButton("OK", (d, i) -> {
                                        String newName = input.getText().toString().trim();
                                        if (newName.isEmpty()) {
                                            Toast.makeText(HomeActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                                        } else if (owner.getAlbumByName(newName) != null) {
                                            Toast.makeText(HomeActivity.this, "Album name already exists", Toast.LENGTH_SHORT).show();
                                        } else {
                                            owner.getAlbumByName(selectedAlbum).setName(newName);
                                            UserManager.getInstance().saveUsers(HomeActivity.this);
                                            loadAlbums();
                                        }
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        } else {
                            new AlertDialog.Builder(HomeActivity.this)
                                    .setTitle("Delete Album")
                                    .setMessage("Delete \"" + selectedAlbum + "\"?")
                                    .setPositiveButton("Delete", (d, i) -> {
                                        owner.removeAlbumByName(selectedAlbum);
                                        UserManager.getInstance().saveUsers(HomeActivity.this);
                                        loadAlbums();
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        }
                    })
                    .show();
            return true;
        });
    }

    private void loadAlbums() {
        albumNames.clear();

        UserManager userManager = UserManager.getInstance();
        User owner = userManager.getUserByUsername("owner");

        if (owner == null) {
            owner = new User("owner");
            userManager.addUser(owner);
            userManager.saveUsers(this); //
        }

        for (Album album : owner.getAlbums()) {
            albumNames.add(album.getName());
        }

        if (albumAdapter != null) {
            albumAdapter.notifyDataSetChanged();
        }
    }
    private void addNewAlbum() {
        int newAlbumNumber = albumNames.size() + 1;
        String newAlbumName = "Album " + newAlbumNumber;

        albumNames.add(newAlbumName);
        albumAdapter.notifyDataSetChanged();

        UserManager userManager = UserManager.getInstance();
        User owner = userManager.getUserByUsername("owner");
        if (owner != null) {
            owner.addAlbum(new Album(newAlbumName));
            userManager.saveUsers(this);
        }
        loadAlbums();
    }

}