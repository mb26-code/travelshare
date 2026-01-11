package dev.mb_labs.travelshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;
import dev.mb_labs.travelshare.model.Frame;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private final Context context;
    private final List<Frame> frameList;

    public FeedAdapter(Context context, List<Frame> frameList) {
        this.context = context;
        this.frameList = frameList;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_frame, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        Frame frame = frameList.get(position);

        holder.tvTitle.setText(frame.getTitle());
        holder.tvDescription.setText(frame.getDescription());


        if (frame.getAuthorName() != null) {
            holder.tvUsername.setText(frame.getAuthorName());
        } else {
            holder.tvUsername.setText("User " + frame.getUserId());
        }

        //extract filenames from Photo objects
        List<String> imageUrls = new ArrayList<>();
        if (frame.getPhotos() != null) {
            for (Frame.Photo photo : frame.getPhotos()) {
                imageUrls.add(photo.getFilename());
            }
        }

        //setup carousel with the extracted strings
        CarouselAdapter carouselAdapter = new CarouselAdapter(context, imageUrls);
        holder.viewPager.setAdapter(carouselAdapter);
        holder.viewPager.setOffscreenPageLimit(1);
    }

    @Override
    public int getItemCount() {
        return frameList.size();
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvUsername;
        ViewPager2 viewPager;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.frame_title);
            tvDescription = itemView.findViewById(R.id.frame_description);
            tvUsername = itemView.findViewById(R.id.frame_author_name);
            viewPager = itemView.findViewById(R.id.photo_carousel);
        }
    }
}