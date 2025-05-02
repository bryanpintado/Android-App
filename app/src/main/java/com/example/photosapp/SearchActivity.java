package com.example.photosapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.example.photosapp.model.UserManager;
import com.example.photosapp.model.User;
import com.example.photosapp.model.Album;
import com.example.photosapp.model.Photo;
import com.example.photosapp.model.Tag;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {

    private Spinner spinnerType1;
    private Spinner spinnerType2;
    private AutoCompleteTextView inputValue1;
    private AutoCompleteTextView inputValue2;
    private RadioGroup radioGroupOperator;
    private Button btnSearch;
    private ListView listSearchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        spinnerType1       = findViewById(R.id.spinnerType1);
        spinnerType2       = findViewById(R.id.spinnerType2);
        inputValue1        = findViewById(R.id.inputValue1);
        inputValue2        = findViewById(R.id.inputValue2);
        radioGroupOperator = findViewById(R.id.radioGroupOperator);
        btnSearch          = findViewById(R.id.btnSearch);
        listSearchResults  = findViewById(R.id.listSearchResults);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"person", "location"}
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType1.setAdapter(typeAdapter);
        spinnerType2.setAdapter(typeAdapter);

        User owner = UserManager.getInstance().getUserByUsername("owner");
        Set<String> persons   = new HashSet<>();
        Set<String> locations = new HashSet<>();
        if (owner != null) {
            for (Album album : owner.getAlbums()) {
                for (Photo photo : album.getPhotos()) {
                    for (Tag tag : photo.getTags()) {
                        String t = tag.getType().toLowerCase();
                        if ("person".equals(t)) {
                            persons.add(tag.getValue());
                        } else if ("location".equals(t)) {
                            locations.add(tag.getValue());
                        }
                    }
                }
            }
        }
        ArrayAdapter<String> personAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(persons)
        );
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>(locations)
        );
        inputValue1.setAdapter(personAdapter);
        inputValue2.setAdapter(personAdapter);
        spinnerType2.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                String sel = spinnerType2.getSelectedItem().toString();
                inputValue2.setAdapter(
                        "location".equalsIgnoreCase(sel) ? locationAdapter : personAdapter
                );
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnSearch.setOnClickListener(v -> {
            String type1 = spinnerType1.getSelectedItem().toString();
            String val1  = inputValue1.getText().toString().trim().toLowerCase();
            String type2 = spinnerType2.getSelectedItem().toString();
            String val2  = inputValue2.getText().toString().trim().toLowerCase();
            boolean useAnd = radioGroupOperator.getCheckedRadioButtonId() == R.id.radioAnd;

            List<Photo> results = searchPhotos(type1, val1, type2, val2, useAnd);
            PhotoAdapter adapter = new PhotoAdapter(this,
                    R.layout.list_item_photo,
                    results);
            listSearchResults.setAdapter(adapter);
        });
    }

    private List<Photo> searchPhotos(String type1, String val1,
                                     String type2, String val2,
                                     boolean useAnd) {
        List<Photo> matches = new ArrayList<>();
        User owner = UserManager.getInstance().getUserByUsername("owner");
        if (owner == null) return matches;

        boolean firstFilled  = val1  != null && !val1.isEmpty();
        boolean secondFilled = val2  != null && !val2.isEmpty();

        for (Album album : owner.getAlbums()) {
            for (Photo photo : album.getPhotos()) {
                boolean m1 = firstFilled  && hasTag(photo, type1, val1);
                boolean m2 = secondFilled && hasTag(photo, type2, val2);

                boolean ok;
                if (firstFilled && secondFilled) {
                    ok = useAnd ? (m1 && m2) : (m1 || m2);
                } else if (firstFilled) {
                    ok = m1;
                } else if (secondFilled) {
                    ok = m2;
                } else {
                    ok = false;
                }

                if (ok && !matches.contains(photo)) {
                    matches.add(photo);
                }
            }
        }
        return matches;
    }

    private boolean hasTag(Photo photo, String type, String val) {
        for (Tag t : photo.getTags()) {
            if (t.getType().equalsIgnoreCase(type) &&
                    t.getValue().toLowerCase().startsWith(val)) {
                return true;
            }
        }
        return false;
    }
}