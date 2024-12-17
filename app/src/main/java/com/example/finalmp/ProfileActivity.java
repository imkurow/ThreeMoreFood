package com.example.finalmp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private CircleImageView imageViewProfile;
    private EditText editTextName, editTextPhone, editTextAddress;
    private DatabaseReference userRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        userId = user.getUid();

        setupReferences();
        setupToolbar();
        initViews();
        loadUserData();
    }

    private void setupReferences() {
        userRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        imageViewProfile = findViewById(R.id.imageViewProfile);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);

        findViewById(R.id.buttonSave).setOnClickListener(v -> saveProfile());
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserData userData = snapshot.getValue(UserData.class);
                    if (userData != null) {
                        TextView textViewProfileName = findViewById(R.id.textViewProfileName);
                        textViewProfileName.setText(userData.getFullname());

                        editTextName.setText(userData.getFullname());
                        editTextPhone.setText(userData.getPhone());
                        editTextAddress.setText(userData.getAddress());

                        if (userData.getProfilePicUrl() != null && !userData.getProfilePicUrl().isEmpty()) {
                            Glide.with(ProfileActivity.this)
                                    .load(userData.getProfilePicUrl())
                                    .placeholder(R.drawable.default_profile)
                                    .into(imageViewProfile);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this,
                        "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Nama harus diisi");
            return;
        }

        AlertDialog loadingDialog = new AlertDialog.Builder(this)
                .setMessage("Menyimpan...")
                .setCancelable(false)
                .create();
        loadingDialog.show();

        updateProfile(name, phone, address, loadingDialog);
    }

    private void updateProfile(String name, String phone,
                               String address, AlertDialog loadingDialog) {
        // Mengambil data email dan gender yang sudah ada
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData existingData = snapshot.getValue(UserData.class);
                String email = (existingData != null) ? existingData.getEmail() : "";
                String gender = (existingData != null) ? existingData.getGender() : "";
                String profilePicUrl = (existingData != null) ? existingData.getProfilePicUrl() : "";

                UserData userData = new UserData(name, email, phone, address, gender);
                userData.setProfilePicUrl(profilePicUrl);

                userRef.setValue(userData)
                        .addOnSuccessListener(aVoid -> {
                            loadingDialog.dismiss();
                            Toast.makeText(ProfileActivity.this,
                                    "Profil berhasil diperbarui",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            loadingDialog.dismiss();
                            Toast.makeText(ProfileActivity.this,
                                    "Gagal memperbarui profil: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(ProfileActivity.this,
                        "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}