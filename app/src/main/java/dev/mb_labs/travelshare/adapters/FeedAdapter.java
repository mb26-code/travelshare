package dev.mb_labs.travelshare.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.viewpager2.widget.ViewPager2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import dev.mb_labs.travelshare.R;
import dev.mb_labs.travelshare.api.APIClient;
import dev.mb_labs.travelshare.fragments.CommentsBottomSheetFragment;
import dev.mb_labs.travelshare.model.Frame;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private final Context context;
    private final List<Frame> frameList;
    private final boolean isGuest;

    private long lastClickTime = 0;
    private static final long COOLDOWN_MS = 1000;

    public FeedAdapter(Context context, List<Frame> frameList, boolean isGuest) {
        this.context = context;
        this.frameList = frameList;
        this.isGuest = isGuest;
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
        holder.tvUsername.setText(frame.getAuthorName() != null ? frame.getAuthorName() : "User " + frame.getUserId());
        holder.tvDate.setText(formatDate(frame.getCreatedAt()));

        CarouselAdapter carouselAdapter = new CarouselAdapter(context, frame.getPhotos());
        holder.viewPager.setAdapter(carouselAdapter);
        holder.viewPager.setOffscreenPageLimit(1);

        //handle likes
        updateLikeUI(holder, frame);
        holder.likeButton.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime < COOLDOWN_MS) {
                return;
            }
            lastClickTime = currentTime;

            if (isGuest) {
                Toast.makeText(context, "You must be signed in to like a Frame.", Toast.LENGTH_SHORT).show();
                updateLikeUI(holder, frame);
            } else {
                toggleLike(holder, frame);
            }
        });

        //comments
        holder.tvCommentsButton.setText("Comments (" + frame.getCommentCount() + ")");
        holder.commentsButtonLayout.setOnClickListener(v -> {
            CommentsBottomSheetFragment bottomSheet = CommentsBottomSheetFragment.newInstance(frame.getId(), isGuest);
            if (context instanceof AppCompatActivity) {
                bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), "CommentsBottomSheet");
            }
        });
    }

    private void updateLikeUI(FeedViewHolder holder, Frame frame) {
        if (frame.isLiked()) {
            holder.likeButton.setImageResource(R.drawable.icon_full_heart);
            holder.likeButton.setColorFilter(context.getColor(R.color.neon_green));
        } else {
            holder.likeButton.setImageResource(R.drawable.icon_hollow_heart);
            holder.likeButton.setColorFilter(context.getColor(R.color.black));
        }
        holder.tvLikeCount.setText(String.valueOf(frame.getLikeCount()));
    }

    private void toggleLike(FeedViewHolder holder, Frame frame) {
        boolean isCurrentlyLiked = frame.isLiked();
        int currentCount = frame.getLikeCount();

        frame.setLiked(!isCurrentlyLiked);
        frame.setLikeCount(isCurrentlyLiked ? currentCount - 1 : currentCount + 1);
        updateLikeUI(holder, frame);

        String token = "Bearer " + getToken();
        Call<ResponseBody> call;

        if (isCurrentlyLiked) {
            call = APIClient.getInstance().unlikeFrame(token, frame.getId());
        } else {
            call = APIClient.getInstance().likeFrame(token, frame.getId());
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    //revert UI on failure
                    frame.setLiked(isCurrentlyLiked);
                    frame.setLikeCount(currentCount);
                    updateLikeUI(holder, frame);
                    Toast.makeText(context, "You must be signed in to like Frames.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //revert UI on error
                frame.setLiked(isCurrentlyLiked);
                frame.setLikeCount(currentCount);
                updateLikeUI(holder, frame);
            }
        });
    }

    private String getToken() {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            return sharedPreferences.getString("auth_token", "");
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public int getItemCount() {
        return frameList.size();
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvUsername, tvDate, tvLikeCount, tvCommentsButton;
        ViewPager2 viewPager;
        ImageView likeButton;
        LinearLayout commentsButtonLayout;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.frame_title);
            tvDescription = itemView.findViewById(R.id.frame_description);
            tvUsername = itemView.findViewById(R.id.frame_author_name);
            tvDate = itemView.findViewById(R.id.frame_hanging_date_time);
            viewPager = itemView.findViewById(R.id.photo_carousel);

            likeButton = itemView.findViewById(R.id.like_button);
            tvLikeCount = itemView.findViewById(R.id.like_count_text);

            commentsButtonLayout = itemView.findViewById(R.id.comments_button);
            tvCommentsButton = itemView.findViewById(R.id.tv_comment_button_text);
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
            return isoDateString;
            //fallback to raw string if parsing fails
        }
    }
}