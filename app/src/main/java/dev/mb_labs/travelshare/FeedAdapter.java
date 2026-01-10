package dev.mb_labs.travelshare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FrameViewHolder> {

    private final List<Frame> postList;

    public FeedAdapter(List<Frame> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public FrameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_frame, parent, false);
        return new FrameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FrameViewHolder holder, int position) {
        Frame post = postList.get(position);


        holder.authorName.setText(post.authorName);
        holder.location.setText(post.location);
        holder.description.setText(post.description);

        CarouselAdapter carouselAdapter = new CarouselAdapter(post.imagesIds);
        holder.viewPager.setAdapter(carouselAdapter);

        if (post.isLiked()) {
            holder.likeButton.setImageResource(R.drawable.icon_full_heart);
            holder.likeButton.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.colorAccent));
        } else {
            holder.likeButton.setImageResource(R.drawable.icon_hollow_heart);
            holder.likeButton.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.black));
        }

        holder.likeButton.setOnClickListener(v -> {

            post.setLiked(!post.isLiked());

            if (post.isLiked()) {
                holder.likeButton.setImageResource(R.drawable.icon_full_heart);
                holder.likeButton.setColorFilter(v.getContext().getResources().getColor(R.color.colorAccent));
            } else {
                holder.likeButton.setImageResource(R.drawable.icon_hollow_heart);
                holder.likeButton.setColorFilter(v.getContext().getResources().getColor(R.color.black));
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    private void toggleLikeState(Frame post, ImageView likeButton) {
        if (post.isLiked()) {

            likeButton.setImageResource(R.drawable.icon_hollow_heart);
            likeButton.setColorFilter(likeButton.getContext().getResources().getColor(R.color.black));
            post.setLiked(false);
        } else {

            likeButton.setImageResource(R.drawable.icon_full_heart);
            likeButton.setColorFilter(likeButton.getContext().getResources().getColor(R.color.colorAccent));
            post.setLiked(true);
        }
    }


    static class FrameViewHolder extends RecyclerView.ViewHolder {
        TextView authorName, location, description;
        ViewPager2 viewPager; // Le carrousel
        ImageView likeButton;

        public FrameViewHolder(@NonNull View itemView) {
            super(itemView);
            authorName = itemView.findViewById(R.id.frame_author_name);
            location = itemView.findViewById(R.id.frame_location);
            description = itemView.findViewById(R.id.frame_description);

            viewPager = itemView.findViewById(R.id.media_carousel);
            likeButton = itemView.findViewById(R.id.action_like_post);
        }
    }
}