package dev.mb_labs.travelshare;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewFrames);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Frame> posts = generateDummyFrames();

        FeedAdapter adapter = new FeedAdapter(posts);
        recyclerView.setAdapter(adapter);

    }

    private List<Frame> generateDummyFrames() {
        List<Frame> list = new ArrayList<>();

        list.add(new Frame(
                "Delphine Chevalier",
                "Montpellier, France",
                "Superbe coucher de soleil...",
                Arrays.asList(android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery)
        ));

        list.add(new Frame(
                "Hugo Prévert",
                "Paris, France",
                "L'Arc de Triomphe.",
                Arrays.asList(android.R.drawable.ic_menu_gallery)
        ));

        list.add(new Frame(
                "Alice Boulanger",
                "Lyon, France",
                "Food tour incroyable! 🍔",
                Arrays.asList(android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_gallery)
        ));

        return list;
    }
}