package com.example.finalmp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextFullname, editTextEmail, editTextPhone, editTextAddress, editTextPassword;
    private RadioGroup radioGroupGender;
    private Button buttonRegister;
    private TextView textViewLogin;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String TAG = "RegisterActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Cek koneksi internet saat activity dibuat
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
        }

        // Inisialisasi Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initViews();
        setupClickListeners();
    }


    private void checkDatabaseConnection() {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                if (connected) {
                    Log.d(TAG, "Terhubung ke database");
                    Toast.makeText(RegisterActivity.this,
                            "Terhubung ke database",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Tidak dapat terhubung ke database");
                    Toast.makeText(RegisterActivity.this,
                            "Tidak dapat terhubung ke database",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Listener cancelled", error.toException());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Cek koneksi database
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                if (!connected) {
                    Toast.makeText(RegisterActivity.this,
                            "Tidak dapat terhubung ke database",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Listener cancelled", error.toException());
            }
        });
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

    private String getSelectedGender() {
        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedId == R.id.radioButtonMale) {
            return "Laki-laki";
        } else if (selectedId == R.id.radioButtonFemale) {
            return "Perempuan";
        }
        return "";
    }

    private void handleRegister() {
        String fullname = editTextFullname.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String gender = getSelectedGender();

        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
            return;
        }

        if (validateInput(fullname, email, phone, address, password, gender)) {
            showLoading(true);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                UserData userData = new UserData(fullname, email, phone, address, gender);

                                mDatabase.child("users").child(userId).setValue(userData)
                                        .addOnSuccessListener(aVoid -> {
                                            showLoading(false);
                                            Toast.makeText(RegisterActivity.this,
                                                    "Registrasi berhasil! Silakan login",
                                                    Toast.LENGTH_SHORT).show();

                                            // Sign out dulu agar user harus login ulang
                                            mAuth.signOut();

                                            // Kirim data email dan password ke LoginActivity
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            intent.putExtra("email", email);
                                            intent.putExtra("password", password);
                                            startActivity(intent);
                                            finish(); // Tutup RegisterActivity
                                        })
                                        .addOnFailureListener(e -> {
                                            showLoading(false);
                                            Toast.makeText(RegisterActivity.this,
                                                    "Gagal menyimpan data: " + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            showLoading(false);
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Registrasi gagal";
                            Toast.makeText(RegisterActivity.this,
                                    "Registrasi gagal: " + errorMessage,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean validateInput(String fullname, String email, String phone,
                                String address, String password, String gender) {
        boolean isValid = true;

        if (fullname.isEmpty()) {
            editTextFullname.setError("Nama lengkap tidak boleh kosong");
            isValid = false;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email tidak boleh kosong");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Format email tidak valid");
            isValid = false;
        }

        if (phone.isEmpty()) {
            editTextPhone.setError("Nomor telepon tidak boleh kosong");
            isValid = false;
        }

        if (address.isEmpty()) {
            editTextAddress.setError("Alamat tidak boleh kosong");
            isValid = false;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password tidak boleh kosong");
            isValid = false;
        } else if (password.length() < 6) {
            editTextPassword.setError("Password minimal 6 karakter");
            isValid = false;
        }

        if (gender.isEmpty()) {
            Toast.makeText(this, "Pilih jenis kelamin", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        buttonRegister.setEnabled(!isLoading);
        editTextFullname.setEnabled(!isLoading);
        editTextEmail.setEnabled(!isLoading);
        editTextPhone.setEnabled(!isLoading);
        editTextAddress.setEnabled(!isLoading);
        editTextPassword.setEnabled(!isLoading);
        radioGroupGender.setEnabled(!isLoading);
    }

    private void navigateToLogin() {
        finish();
    }

    private void navigateToMain() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finishAffinity();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }


    

}

