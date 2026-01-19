package dev.mb_labs.travelshare.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Locale;

import android.content.ClipData;
import android.content.ClipboardManager;

import dev.mb_labs.travelshare.activities.MainActivity;
import dev.mb_labs.travelshare.R;
import dev.mb_labs.travelshare.model.Frame;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {

    private final Context context;
    private final List<Frame.Photo> photoList;
    private static final String BASE_PHOTO_URL = "https://api.travelshare.mb-labs.dev/media/photos/";

    public CarouselAdapter(Context context, List<Frame.Photo> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_carousel_image, parent, false);
        return new CarouselViewHolder(view);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        Frame.Photo photo = photoList.get(position);

        //load image
        String fullUrl = BASE_PHOTO_URL + photo.getFilename();
        Glide.with(context)
                .load(BASE_PHOTO_URL + photo.getFilename())
                .placeholder(R.drawable.frame_photo_placeholder)
                .error(R.drawable.frame_photo_placeholder)
                .centerCrop()
                .into(holder.imageView);

        //handle GPS decimal coordinates
        //set GPS Coordinates with dot separator (using Locale.US)
        //ex of format: "48.8500° N, 2.3500° E"
        if (photo.getLatitude() != 0.0 && photo.getLongitude() != 0.0) {
            String coords = String.format(Locale.US, "%.4f° N, %.4f° E", photo.getLatitude(), photo.getLongitude());
            holder.gpsText.setText(coords);
            holder.gpsContainer.setVisibility(View.VISIBLE);

            //set up google maps pin
            if (isGoogleMapsInstalled()) {
                holder.mapPinButton.setVisibility(View.VISIBLE);
                holder.mapPinButton.setOnClickListener(v -> openGoogleMaps(photo.getLatitude(), photo.getLongitude()));
            } else {
                holder.mapPinButton.setVisibility(View.GONE);
            }

            holder.gpsContainer.setOnLongClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("GPS Coordinates", coords);
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Coordinates copied!", Toast.LENGTH_SHORT).show();
                }
                return true;
            });

        } else {
            holder.gpsContainer.setVisibility(View.GONE);
        }

        //handling long push for zoom
        holder.imageView.setOnTouchListener(new View.OnTouchListener() {
            private Runnable showZoomRunnable;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //long push + hold = zoom on photo
                        showZoomRunnable = () -> {
                            if (context instanceof MainActivity) {
                                ((MainActivity) context).showZoomImage(BASE_PHOTO_URL + photo.getFilename());
                                holder.gpsContainer.setVisibility(View.GONE);
                                v.getParent().requestDisallowInterceptTouchEvent(true);
                            }
                        };
                        v.postDelayed(showZoomRunnable, 500);
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        //stop zoom if the finger is lifted (no longer holding)
                        if (showZoomRunnable != null) {
                            v.removeCallbacks(showZoomRunnable);
                        }
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).hideZoomImage();
                            v.getParent().requestDisallowInterceptTouchEvent(false);

                            if (photo.getLatitude() != 0.0 && photo.getLongitude() != 0.0) {
                                holder.gpsContainer.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private boolean isGoogleMapsInstalled() {
        PackageManager pm = context.getPackageManager();
        try {
            //check specifically for the Google Maps package on the device
            pm.getPackageInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    
    private void openGoogleMaps(double lat, double lon) {
        //"geo:lat,lon?q=lat,lon(Label)" syntax creates a pin at that location in Google Maps
        Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon + "(Photo Location)");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        try {
            context.startActivity(mapIntent);
        } catch (Exception e) {
            Toast.makeText(context, "Could not open Maps", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return photoList != null ? photoList.size() : 0;
    }

    static class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView gpsText;
        LinearLayout gpsContainer;
        ImageView mapPinButton;

        public CarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.carousel_image_item);
            gpsText = itemView.findViewById(R.id.gps_coordinates_text);
            gpsContainer = itemView.findViewById(R.id.gps_tag_container);
            mapPinButton = itemView.findViewById(R.id.btn_open_maps);
        }
    }
}