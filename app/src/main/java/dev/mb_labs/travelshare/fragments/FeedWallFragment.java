package dev.mb_labs.travelshare.fragments;

import android.content.SharedPreferences;
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
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.util.List;

import dev.mb_labs.travelshare.R;
import dev.mb_labs.travelshare.adapters.FeedAdapter;
import dev.mb_labs.travelshare.api.APIClient;
import dev.mb_labs.travelshare.database.AppDatabase;
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

        //load from cache immediately (local database, if offline)
        List<Frame> cachedFrames = AppDatabase.getDatabase(getContext()).frameDao().getAllFrames();
        if (cachedFrames != null && !cachedFrames.isEmpty()) {
            boolean isGuest = !isUserLoggedIn();
            FeedAdapter adapter = new FeedAdapter(getContext(), cachedFrames, isGuest);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }

        //fetch from network (sync)
        String token = null;
        if (isUserLoggedIn()) {
            token = "Bearer " + getToken();
        }

        APIClient.getInstance().getFrames(token).enqueue(new Callback<List<Frame>>() {
            @Override
            public void onResponse(Call<List<Frame>> call, Response<List<Frame>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Frame> frames = response.body();

                    //save to database (cache)
                    new Thread(() -> {
                        AppDatabase db = AppDatabase.getDatabase(getContext());
                        db.frameDao().clearAll();
                        db.frameDao().insertAll(frames);
                    }).start();

                    //update UI
                    boolean isGuest = !isUserLoggedIn();
                    FeedAdapter adapter = new FeedAdapter(getContext(), frames, isGuest);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Frame>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                //if network fails, we already showed cached frames above
                Toast.makeText(getContext(), "Offline Mode: Showing cached data.", Toast.LENGTH_SHORT).show();
            }
        });
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
            e.printStackTrace();
            return "";
        }
    }

    private boolean isUserLoggedIn() {
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
            return sharedPreferences.contains("auth_token");
        } catch (Exception e) {
            return false;
        }
    }
}