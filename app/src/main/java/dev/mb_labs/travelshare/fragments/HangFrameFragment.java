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

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.mb_labs.travelshare.MapPickerActivity;
import dev.mb_labs.travelshare.R;
import dev.mb_labs.travelshare.SelectedPhotoAdapter;
import dev.mb_labs.travelshare.api.APIClient;
import dev.mb_labs.travelshare.model.Frame;
import dev.mb_labs.travelshare.model.SelectedPhoto;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HangFrameFragment extends Fragment {

    private EditText etTitle, etDescription;
    private RadioGroup radioGroupVisibility;
    private RecyclerView recyclerView;
    private Button btnPickPhotos, btnPostFrame;

    private List<SelectedPhoto> selectedPhotos = new ArrayList<>();
    private SelectedPhotoAdapter adapter;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia;

    private int currentEditingPosition = -1;

    private final ActivityResultLauncher<Intent> mapPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    double lat = result.getData().getDoubleExtra("LATITUDE", 0);
                    double lng = result.getData().getDoubleExtra("LONGITUDE", 0);

                    if (currentEditingPosition != -1 && currentEditingPosition < selectedPhotos.size()) {
                        SelectedPhoto photo = selectedPhotos.get(currentEditingPosition);
                        photo.setLocation(lat, lng);
                        adapter.notifyItemChanged(currentEditingPosition);
                    }
                }
            }
    );

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
            currentEditingPosition = position;
            Intent intent = new Intent(getContext(), MapPickerActivity.class);
            if (photo.hasLocation()) {
                intent.putExtra("LATITUDE", photo.getLatitude());
                intent.putExtra("LONGITUDE", photo.getLongitude());
            }
            mapPickerLauncher.launch(intent);
        });
        recyclerView.setAdapter(adapter);

        pickMultipleMedia = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(8), uris -> {
            if (!uris.isEmpty()) {
                for (Uri uri : uris) {
                    SelectedPhoto photo = new SelectedPhoto(uri);
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

        for (SelectedPhoto p : selectedPhotos) {
            if (!p.hasLocation()) {
                Toast.makeText(getContext(), "Please set location for all photos", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        btnPostFrame.setEnabled(false);
        Toast.makeText(getContext(), "Uploading...", Toast.LENGTH_SHORT).show();

        try {
            List<MultipartBody.Part> photoParts = new ArrayList<>();
            List<Map<String, Double>> metadataList = new ArrayList<>();

            for (SelectedPhoto photo : selectedPhotos) {
                File file = getFileFromUri(photo.getUri());
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("photos", file.getName(), requestFile);
                photoParts.add(body);

                Map<String, Double> meta = new HashMap<>();
                meta.put("latitude", photo.getLatitude());
                meta.put("longitude", photo.getLongitude());
                metadataList.add(meta);
            }

            String metadataJson = new Gson().toJson(metadataList);

            RequestBody titlePart = RequestBody.create(MediaType.parse("text/plain"), title);
            RequestBody descPart = RequestBody.create(MediaType.parse("text/plain"), description);
            RequestBody visPart = RequestBody.create(MediaType.parse("text/plain"), visibility);
            RequestBody metaPart = RequestBody.create(MediaType.parse("text/plain"), metadataJson);

            APIClient.getInstance().createFrame(titlePart, descPart, visPart, metaPart, photoParts).enqueue(new Callback<Frame>() {
                @Override
                public void onResponse(Call<Frame> call, Response<Frame> response) {
                    btnPostFrame.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Frame posted!", Toast.LENGTH_LONG).show();
                        etTitle.setText("");
                        etDescription.setText("");
                        selectedPhotos.clear();
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Upload failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Frame> call, Throwable t) {
                    btnPostFrame.setEnabled(true);
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            btnPostFrame.setEnabled(true);
            e.printStackTrace();
            Toast.makeText(getContext(), "Error preparing files", Toast.LENGTH_SHORT).show();
        }
    }

    private File getFileFromUri(Uri uri) throws Exception {
        InputStream is = getContext().getContentResolver().openInputStream(uri);
        File file = new File(getContext().getCacheDir(), "upload_" + System.currentTimeMillis() + ".jpg");
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        fos.close();
        is.close();
        return file;
    }
}