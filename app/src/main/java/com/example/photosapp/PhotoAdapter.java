package com.example.photosapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.photosapp.model.Photo;

import java.util.List;

public class PhotoAdapter extends ArrayAdapter<Photo> {
    private int resourceLayout;
    private Context mContext;

    public PhotoAdapter(@NonNull Context context, int resource, @NonNull List<Photo> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @NonNull @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(resourceLayout, parent, false);
        }
        Photo photo = getItem(position);
        if (photo != null) {
            ImageView thumb = view.findViewById(R.id.photoThumbnail);
            TextView name  = view.findViewById(R.id.photoName);

            Uri uri = Uri.parse(photo.getFileUri());
            thumb.setImageURI(uri);

            name.setText(photo.getCaption());
        }
        return view;
    }
}