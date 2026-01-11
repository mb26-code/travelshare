package dev.mb_labs.travelshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

        String rawDate = frame.getCreatedAt();
        //ex: "2026-01-09T21:27:11.000Z"
        holder.tvDate.setText(formatDate(rawDate));

        CarouselAdapter carouselAdapter = new CarouselAdapter(context, frame.getPhotos());
        holder.viewPager.setAdapter(carouselAdapter);
        holder.viewPager.setOffscreenPageLimit(1);

        updateLikeIcon(holder.likeButton, frame.isLiked());

        holder.likeButton.setOnClickListener(v -> {
            boolean newState = !frame.isLiked();
            frame.setLiked(newState);
            updateLikeIcon(holder.likeButton, newState);

            //String msg = newState ? "Liked!" : "Unliked";
            //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void updateLikeIcon(ImageView view, boolean isLiked) {
        if (isLiked) {
            view.setImageResource(R.drawable.icon_full_heart);
            view.setColorFilter(ContextCompat.getColor(context, R.color.neon_green));
        } else {
            view.setImageResource(R.drawable.icon_hollow_heart);
            view.setColorFilter(ContextCompat.getColor(context, R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return frameList.size();
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvUsername, tvDate;
        ViewPager2 viewPager;
        ImageView likeButton;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.frame_title);
            tvDescription = itemView.findViewById(R.id.frame_description);
            tvUsername = itemView.findViewById(R.id.frame_author_name);
            tvDate = itemView.findViewById(R.id.frame_hanging_date_time);

            viewPager = itemView.findViewById(R.id.photo_carousel);
            likeButton = itemView.findViewById(R.id.like_button);
        }
    }

    private String formatDate(String isoDateString) {
        if (isoDateString == null) return "";
        try {
            //parse the input date (UTC)
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = inputFormat.parse(isoDateString);

            //format the output date (Local system time)
            //pattern: "on 09/01/2026 at 21:27"
            SimpleDateFormat outputFormat = new SimpleDateFormat("'on' dd/MM/yyyy 'at' HH:mm", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return isoDateString;
            //fallback to raw string if parsing fails
        }
    }
}