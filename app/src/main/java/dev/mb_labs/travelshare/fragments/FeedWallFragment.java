package dev.mb_labs.travelshare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import dev.mb_labs.travelshare.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.mb_labs.travelshare.FeedAdapter;
import dev.mb_labs.travelshare.R;
import dev.mb_labs.travelshare.api.APIClient;
import dev.mb_labs.travelshare.model.Frame;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedWallFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_wall, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFeed);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFrames();

        return view;
    }

    private void loadFrames() {
        progressBar.setVisibility(View.VISIBLE);

        APIClient.getInstance().getFrames().enqueue(new Callback<List<Frame>>() {
            @Override
            public void onResponse(Call<List<Frame>> call, Response<List<Frame>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Frame> frames = response.body();
                    FeedAdapter adapter = new FeedAdapter(getContext(), frames);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "Failed to load feed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Frame>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}