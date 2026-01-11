package dev.mb_labs.travelshare.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import dev.mb_labs.travelshare.R;
import dev.mb_labs.travelshare.SelectedPhotoAdapter;
import dev.mb_labs.travelshare.model.SelectedPhoto;

public class HangFrameFragment extends Fragment {

    private EditText etTitle, etDescription;
    private RadioGroup radioGroupVisibility;
    private RecyclerView recyclerView;
    private Button btnPickPhotos, btnPostFrame;

    private List<SelectedPhoto> selectedPhotos = new ArrayList<>();
    private SelectedPhotoAdapter adapter;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hang_frame, container, false);

        etTitle = view.findViewById(R.id.et_title);
        etDescription = view.findViewById(R.id.et_description);
        radioGroupVisibility = view.findViewById(R.id.radio_group_visibility);
        recyclerView = view.findViewById(R.id.recycler_selected_photos);
        btnPickPhotos = view.findViewById(R.id.btn_pick_photos);
        btnPostFrame = view.findViewById(R.id.btn_post_frame);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new SelectedPhotoAdapter(getContext(), selectedPhotos, (position, photo) -> {
            //open Google Map Activity here to pick location manually

            Toast.makeText(getContext(), "Open Map for photo " + position, Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);


        pickMultipleMedia = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(8), uris -> {
            if (!uris.isEmpty()) {
                for (Uri uri : uris) {
                    SelectedPhoto photo = new SelectedPhoto(uri);
                    //try to extract EXIF data from the image
                    extractExifLocation(photo);
                    selectedPhotos.add(photo);
                }
                adapter.notifyDataSetChanged();
            }
        });

        btnPickPhotos.setOnClickListener(v ->
                pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build())
        );

        btnPostFrame.setOnClickListener(v -> postFrame());

        return view;
    }

    private void extractExifLocation(SelectedPhoto photo) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(photo.getUri());
            if (inputStream != null) {
                ExifInterface exif = new ExifInterface(inputStream);
                float[] latLong = new float[2];
                if (exif.getLatLong(latLong)) {
                    //EXIF data found!
                    photo.setLocation(latLong[0], latLong[1]);
                }
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void postFrame() {
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();

        String visibility = "public";
        int selectedId = radioGroupVisibility.getCheckedRadioButtonId();
        if (selectedId == R.id.rb_group) visibility = "user_group";
        else if (selectedId == R.id.rb_private) visibility = "private";

        if (title.isEmpty() || selectedPhotos.isEmpty()) {
            Toast.makeText(getContext(), "Title and Photos are required", Toast.LENGTH_SHORT).show();
            return;
        }

        //check if all of the photos have defined locations
        for (SelectedPhoto p : selectedPhotos) {
            if (!p.hasLocation()) {
                Toast.makeText(getContext(), "Please set location for all photos", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //prepare Multipart Request and call API
        Toast.makeText(getContext(), "Ready to upload " + selectedPhotos.size() + " photos!", Toast.LENGTH_SHORT).show();
    }
}