package dev.mb_labs.travelshare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

import dev.mb_labs.travelshare.R;
import dev.mb_labs.travelshare.model.SelectedPhoto;

public class SelectedPhotoAdapter extends RecyclerView.Adapter<SelectedPhotoAdapter.PhotoViewHolder> {

    private Context context;
    private List<SelectedPhoto> photos;
    private OnLocationClickListener locationListener;

    public interface OnLocationClickListener {
        void onLocationClick(int position, SelectedPhoto photo);
    }

    public SelectedPhotoAdapter(Context context, List<SelectedPhoto> photos, OnLocationClickListener listener) {
        this.context = context;
        this.photos = photos;
        this.locationListener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_selected_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        SelectedPhoto photo = photos.get(position);

        Glide.with(context).load(photo.getUri()).centerCrop().into(holder.thumbnail);

        if (photo.hasLocation()) {
            holder.status.setText("Loc OK");
            holder.status.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.mapIcon.setColorFilter(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.status.setText("Set Loc");
            holder.status.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.mapIcon.setColorFilter(context.getResources().getColor(android.R.color.darker_gray));
        }

        holder.mapIcon.setOnClickListener(v -> locationListener.onLocationClick(position, photo));
    }

    @Override
    public int getItemCount() { return photos.size(); }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, mapIcon;
        TextView status;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.img_thumbnail);
            mapIcon = itemView.findViewById(R.id.btn_edit_location);
            status = itemView.findViewById(R.id.tv_status);
        }
    }
}