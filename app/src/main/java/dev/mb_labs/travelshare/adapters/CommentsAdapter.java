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
import dev.mb_labs.travelshare.model.Comment;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private final Context context;
    private final List<Comment> commentList;
    private static final String BASE_AVATAR_URL = "https://api.travelshare.mb-labs.dev/media/avatars/";

    public CommentsAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        holder.tvAuthor.setText(comment.getAuthorName());
        holder.tvContent.setText(comment.getContent());
        holder.tvDate.setText(comment.getPostedOn() + " " + comment.getPostedAt());

        String avatarUrl = comment.getAuthorAvatar();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(context)
                    .load(BASE_AVATAR_URL + avatarUrl)
                    .circleCrop()
                    .placeholder(R.drawable.icon_launcher_background)
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.icon_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvContent, tvDate;
        ImageView imgAvatar;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_comment_author);
            tvContent = itemView.findViewById(R.id.tv_comment_content);
            tvDate = itemView.findViewById(R.id.tv_comment_date);
            imgAvatar = itemView.findViewById(R.id.img_comment_avatar);
        }
    }
}