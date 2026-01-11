package dev.mb_labs.travelshare.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.mb_labs.travelshare.FeedAdapter;
import dev.mb_labs.travelshare.R;
import dev.mb_labs.travelshare.api.APIClient;
import dev.mb_labs.travelshare.model.Frame;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvNoResults;
    private FeedAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerViewSearch);
        progressBar = view.findViewById(R.id.searchProgressBar);
        tvNoResults = view.findViewById(R.id.tvNoResults);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //initialize adapter with empty list
        adapter = new FeedAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        setupSearchListener();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchView searchView = view.findViewById(R.id.searchView);

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        searchEditText.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.dark_blue)
        );

        searchEditText.setHintTextColor(
                ContextCompat.getColor(requireContext(), R.color.light_blue)
        );
    }


    private void setupSearchListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    performSearch(query);
                }
                searchView.clearFocus();
                //hide keyboard
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //...
                return false;
            }
        });
    }

    private void performSearch(String query) {
        progressBar.setVisibility(View.VISIBLE);
        tvNoResults.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        //call the search endpoint of the API
        APIClient.getInstance().searchFrames(query).enqueue(new Callback<List<Frame>>() {
            @Override
            public void onResponse(Call<List<Frame>> call, Response<List<Frame>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Frame> results = response.body();

                    if (results.isEmpty()) {
                        tvNoResults.setVisibility(View.VISIBLE);
                    } else {
                        //re-use the FeedAdapter to display results
                        adapter = new FeedAdapter(getContext(), results);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Search failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Frame>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}