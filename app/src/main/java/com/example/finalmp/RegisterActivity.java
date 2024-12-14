package com.example.finalmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG, "Database Reference: " + mDatabase.toString());

        initViews();
        setupClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Cek koneksi database
        mDatabase.child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                Log.d(TAG, "Database connected: " + connected);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
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

    private void handleRegister() {
        String fullname = editTextFullname.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String gender = getSelectedGender();

        if (validateInput(fullname, email, phone, address, password, gender)) {
            showLoading(true);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            UserData userData = new UserData(fullname, email, phone, address, gender);

                            mDatabase.child("users").child(userId).setValue(userData)
                                    .addOnCompleteListener(dbTask -> {
                                        showLoading(false);
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this,
                                                    "Registrasi berhasil",
                                                    Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "Data tersimpan untuk user: " + userId);
                                            navigateToMain();
                                        } else {
                                            Toast.makeText(RegisterActivity.this,
                                                    "Gagal menyimpan data: " + dbTask.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "Error: " + dbTask.getException().getMessage());
                                        }
                                    });
                        } else {
                            showLoading(false);
                            Toast.makeText(RegisterActivity.this,
                                    "Registrasi gagal: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Registrasi gagal: " + task.getException().getMessage());
                        }
                    });
        }
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
        textViewLogin.setEnabled(!isLoading);
        editTextFullname.setEnabled(!isLoading);
        editTextEmail.setEnabled(!isLoading);
        editTextPhone.setEnabled(!isLoading);
        editTextAddress.setEnabled(!isLoading);
        editTextPassword.setEnabled(!isLoading);
    }

    private void navigateToLogin() {
        finish();
    }

    private void navigateToMain() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finishAffinity();
    }

    
    

}

