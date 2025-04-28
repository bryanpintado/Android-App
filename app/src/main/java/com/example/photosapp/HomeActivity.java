package com.example.photosapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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

        // Initialize Views
        albumListView = findViewById(R.id.albumListView);
        addAlbumButton = findViewById(R.id.addAlbumButton);

        UserManager.getInstance().loadUsers(this);

        albumNames = new ArrayList<>();
        loadAlbums();

        albumAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, albumNames);
        albumListView.setAdapter(albumAdapter);

        addAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewAlbum();
            }
        });

        // Click on album to open (later)
        albumListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedAlbum = albumNames.get(position);
            Toast.makeText(HomeActivity.this, "Clicked Album: " + selectedAlbum, Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Album screen later
        });
    }

    private void loadAlbums() {
        albumNames.clear(); // Clear old list first

        UserManager userManager = UserManager.getInstance();
        User owner = userManager.getUserByUsername("owner");

        if (owner == null) {
            owner = new User("owner");
            userManager.addUser(owner);
            userManager.saveUsers(this); // <-- correct line
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

        // Add to list
        albumNames.add(newAlbumName);
        albumAdapter.notifyDataSetChanged();

        // Save into user manager
        UserManager userManager = UserManager.getInstance();
        User owner = userManager.getUserByUsername("owner");
        if (owner != null) {
            owner.addAlbum(new Album(newAlbumName));
            userManager.saveUsers(this);
        }
    }
}