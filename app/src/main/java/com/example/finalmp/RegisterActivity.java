package com.example.finalmp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextFullname, editTextEmail, editTextPhone, editTextAddress, editTextPassword;
    private RadioGroup radioGroupGender;
    private Button buttonRegister;
    private TextView textViewLogin;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    private void initViews() {
        editTextFullname = findViewById(R.id.editTextFullname);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPassword = findViewById(R.id.editTextPassword);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        buttonRegister.setOnClickListener(v -> handleRegister());
        textViewLogin.setOnClickListener(v -> navigateToLogin());
    }

    private void handleRegister() {
        String fullname = editTextFullname.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String gender = getSelectedGender();

        if (validateInput(fullname, email, phone, address, password, gender)) {
            showLoading(true);
            // Simulasi delay register
            new Handler().postDelayed(() -> {
                showLoading(false);
                // Untuk sementara langsung ke MainActivity
                navigateToMain();
            }, 1500);
        }
    }
    

}

