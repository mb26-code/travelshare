package dev.mb_labs.travelshare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dev.mb_labs.travelshare.fragments.FeedWallFragment;
import dev.mb_labs.travelshare.fragments.HangFrameFragment;
import dev.mb_labs.travelshare.fragments.ProfileFragment;
import dev.mb_labs.travelshare.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private boolean inSignedOutMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if we are in signed out mode (passed from SignInActivity)
        inSignedOutMode = getIntent().getBooleanExtra("SIGNED_OUT_MODE", false);

        if (!inSignedOutMode && !isUserLoggedIn()) {
            redirectToLogin();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //if in signed out mode, hide specific menu items
        if (inSignedOutMode) {
            bottomNavigationView.getMenu().findItem(R.id.nav_hang_frame).setVisible(false);
            bottomNavigationView.getMenu().findItem(R.id.nav_profile).setVisible(false);

            Toast.makeText(this, "Signed out Mode: read only", Toast.LENGTH_LONG).show();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (itemId == R.id.nav_hang_frame) {
                selectedFragment = new HangFrameFragment();
            } else if (itemId == R.id.nav_feed_wall) {
                selectedFragment = new FeedWallFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        //default fragment = feed wall
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_feed_wall);
        }
    }

    private boolean isUserLoggedIn() {
        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    this,
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

    private void redirectToLogin() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}