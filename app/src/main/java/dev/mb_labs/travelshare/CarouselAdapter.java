package dev.mb_labs.travelshare;

import android.annotation.SuppressLint;
import android.content.Context;
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
                .load(fullUrl)
                .centerCrop()
                .placeholder(R.drawable.frame_photo_placeholder)
                .into(holder.imageView);

        //set GPS Coordinates with dot separator (using Locale.US)
        //ex of format: "48.8500, 2.3500"
        String gpsText = String.format(Locale.US, "%.4f, %.4f", photo.getLatitude(), photo.getLongitude());
        holder.gpsText.setText(gpsText);

        holder.gpsContainer.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("TravelShare location Coordinates", gpsText);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Coordinates copied to clipboard.", Toast.LENGTH_LONG).show();
        });

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
                                ((MainActivity) context).showZoomImage(fullUrl);
                                //block thee scrolling of the ViewPager during the zoom on the photo
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
                        }
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoList != null ? photoList.size() : 0;
    }

    static class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView gpsText;
        LinearLayout gpsContainer;

        public CarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.carousel_image_item);
            gpsText = itemView.findViewById(R.id.gps_coordinates_text);
            gpsContainer = itemView.findViewById(R.id.gps_tag_container);
        }
    }
}