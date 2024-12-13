package com.example.finalmp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.SignInButton;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private SignInButton buttonGoogleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoogleSignIn = findViewById(R.id.buttonGoogleSignIn);
        textViewRegister = findViewById(R.id.textViewRegister);

        // Login button click listener
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if (validateInput(email, password)) {
                // Untuk sementara langsung ke MainActivity
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });

        // Google Sign In click listener
        buttonGoogleSignIn.setOnClickListener(v -> {
            Toast.makeText(this, "Google Sign In clicked", Toast.LENGTH_SHORT).show();
        });

        // Register text click listener
        textViewRegister.setOnClickListener(v -> {
            Toast.makeText(this, "Register clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            editTextEmail.setError("Email tidak boleh kosong");
            return false;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Password tidak boleh kosong");
            return false;
        }
        return true;
    }
}