package dev.mb_labs.travelshare.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import dev.mb_labs.travelshare.R;
import dev.mb_labs.travelshare.adapters.CommentsAdapter;
import dev.mb_labs.travelshare.api.APIClient;
import dev.mb_labs.travelshare.model.Comment;
import dev.mb_labs.travelshare.model.CommentRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_FRAME_ID = "frame_id";
    private static final String ARG_IS_GUEST = "is_guest";

    private int frameId;
    private boolean isGuest;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvNoComments;
    private EditText etInput;
    private ImageButton btnSend;
    private CommentsAdapter adapter;
    private List<Comment> commentList = new ArrayList<>();

    public static CommentsBottomSheetFragment newInstance(int frameId, boolean isGuest) {
        CommentsBottomSheetFragment fragment = new CommentsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FRAME_ID, frameId);
        args.putBoolean(ARG_IS_GUEST, isGuest);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_comments_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            frameId = getArguments().getInt(ARG_FRAME_ID);
            isGuest = getArguments().getBoolean(ARG_IS_GUEST);
        }

        recyclerView = view.findViewById(R.id.recycler_comments);
        progressBar = view.findViewById(R.id.progress_comments);
        tvNoComments = view.findViewById(R.id.tv_no_comments);
        etInput = view.findViewById(R.id.et_comment_input);
        btnSend = view.findViewById(R.id.btn_send_comment);
        LinearLayout inputLayout = view.findViewById(R.id.layout_comment_input);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CommentsAdapter(getContext(), commentList);
        recyclerView.setAdapter(adapter);

        if (isGuest) {
            inputLayout.setVisibility(View.GONE);
        } else {
            inputLayout.setVisibility(View.VISIBLE);
            btnSend.setOnClickListener(v -> postComment());
        }

        loadComments();
    }

    private void loadComments() {
        progressBar.setVisibility(View.VISIBLE);
        APIClient.getInstance().getFrameComments(frameId).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    commentList.clear();
                    commentList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (commentList.isEmpty()) {
                        tvNoComments.setVisibility(View.VISIBLE);
                    } else {
                        tvNoComments.setVisibility(View.GONE);
                        recyclerView.scrollToPosition(commentList.size() - 1);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load comments.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postComment() {
        String content = etInput.getText().toString().trim();
        if (content.isEmpty()) return;

        btnSend.setEnabled(false);
        String token = "Bearer " + getToken();
        CommentRequest request = new CommentRequest(content);

        APIClient.getInstance().postComment(token, frameId, request).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                btnSend.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    etInput.setText("");

                    Comment newComment = response.body();

                    //manually populate author details for local display
                    //otherwise we get our own comment as "null null" as the header until we refresh the comment section
                    SharedPreferences prefs = getEncryptedPrefs();
                    newComment.setAuthorName(prefs.getString("user_name", "Me"));
                    //newComment.setAuthorAvatar(...); //for later
                    newComment.setPostedOn("Just now");
                    newComment.setPostedAt("");

                    commentList.add(newComment);
                    adapter.notifyItemInserted(commentList.size() - 1);
                    recyclerView.scrollToPosition(commentList.size() - 1);
                    tvNoComments.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "You must be signed in to comment on a Frame", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                btnSend.setEnabled(true);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private SharedPreferences getEncryptedPrefs() {
        try {
            MasterKey masterKey = new MasterKey.Builder(getContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return EncryptedSharedPreferences.create(
                    getContext(),
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) { return null; }
    }

    private String getToken() {
        try {
            MasterKey masterKey = new MasterKey.Builder(getContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    getContext(),
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
}