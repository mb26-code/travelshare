package dev.mb_labs.travelshare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import dev.mb_labs.travelshare.R;
import dev.mb_labs.travelshare.api.APIClient;
import dev.mb_labs.travelshare.model.VerifyRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity {

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verification);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_verification), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = getIntent().getStringExtra("EMAIL");
        TextView tvInfo = findViewById(R.id.tv_verification_info);
        if (email != null) {
            tvInfo.setText("Enter the code sent to \"" + email + "\"");
        }

        EditText etCode = findViewById(R.id.et_verification_code);
        Button btnVerify = findViewById(R.id.btn_verify);

        btnVerify.setOnClickListener(v -> {
            String code = etCode.getText().toString().trim();
            if (code.length() < 8) {
                Toast.makeText(this, "Invalid code format", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyEmail(code);
        });
    }

    private void verifyEmail(String code) {
        VerifyRequest request = new VerifyRequest(email, code);
        APIClient.getInstance().verifyEmail(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(VerificationActivity.this, "Verified! You can now login.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(VerificationActivity.this, SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(VerificationActivity.this, "Verification failed. Check your code.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(VerificationActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}