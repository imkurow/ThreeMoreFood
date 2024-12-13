package com.example.finalmp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.SignInButton;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private SignInButton buttonGoogleSignIn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi views
        initViews();
        setupClickListeners();
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoogleSignIn = findViewById(R.id.buttonGoogleSignIn);
        textViewRegister = findViewById(R.id.textViewRegister);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        buttonLogin.setOnClickListener(v -> handleLogin());
        buttonGoogleSignIn.setOnClickListener(v -> handleGoogleSignIn());
        textViewRegister.setOnClickListener(v -> navigateToRegister());
    }

    private void handleLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (validateInput(email, password)) {
            showLoading(true);
            // Simulasi delay login
            new Handler().postDelayed(() -> {
                showLoading(false);
                navigateToMain();
            }, 1500);
        }
    }

    private void handleGoogleSignIn() {
        showLoading(true);
        Toast.makeText(this, "Google Sign In akan diimplementasikan", Toast.LENGTH_SHORT).show();
        showLoading(false);
    }

    private void navigateToRegister() {
        // Akan diimplementasikan nanti
        Toast.makeText(this, "Menuju halaman Register", Toast.LENGTH_SHORT).show();
    }

    private void navigateToMain() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        buttonLogin.setEnabled(!isLoading);
        buttonGoogleSignIn.setEnabled(!isLoading);
        textViewRegister.setEnabled(!isLoading);
        editTextEmail.setEnabled(!isLoading);
        editTextPassword.setEnabled(!isLoading);
    }

    private boolean validateInput(String email, String password) {
        boolean isValid = true;

        if (email.isEmpty()) {
            editTextEmail.setError("Email tidak boleh kosong");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Format email tidak valid");
            isValid = false;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password tidak boleh kosong");
            isValid = false;
        } else if (password.length() < 6) {
            editTextPassword.setError("Password minimal 6 karakter");
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setTitle("Keluar Aplikasi")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", (dialog, which) -> finish())
                .setNegativeButton("Tidak", null)
                .show();
    }
}