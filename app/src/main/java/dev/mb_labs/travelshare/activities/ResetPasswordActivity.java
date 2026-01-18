package dev.mb_labs.travelshare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import dev.mb_labs.travelshare.R;
import dev.mb_labs.travelshare.api.APIClient;
import dev.mb_labs.travelshare.model.ConfirmResetRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_reset_password), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = getIntent().getStringExtra("EMAIL");

        EditText etCode = findViewById(R.id.et_reset_code);
        EditText etNewPass = findViewById(R.id.et_new_password);
        Button btnConfirm = findViewById(R.id.btn_confirm_reset);

        btnConfirm.setOnClickListener(v -> {
            String code = etCode.getText().toString().trim();
            String newPass = etNewPass.getText().toString().trim();

            if (code.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            confirmReset(code, newPass);
        });
    }

    private void confirmReset(String code, String newPassword) {
        ConfirmResetRequest request = new ConfirmResetRequest(email, code, newPassword);
        APIClient.getInstance().confirmPasswordReset(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Password updated! Please login.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Failed. Check code or try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}