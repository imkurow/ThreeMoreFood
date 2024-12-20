package com.example.finalmp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private SignInButton buttonGoogleSignIn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String TAG = "LoginActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi Firebase - Tambahkan ini
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Inisialisasi views
        initViews();
        setupClickListeners();

        // Cek apakah ada data dari RegisterActivity
        Intent intent = getIntent();
        if (intent != null) {
            String email = intent.getStringExtra("email");
            String password = intent.getStringExtra("password");

            if (email != null && password != null) {
                editTextEmail.setText(email);
                editTextPassword.setText(password);
            }
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1078030588592-9f59mc2daeu2nf52sasvjhismqg2vgq0.apps.googleusercontent.com") // Ganti dengan Web Client ID dari Firebase Console
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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

            // Menambahkan timeout handler
            new Handler().postDelayed(() -> {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    showLoading(false);
                    Toast.makeText(LoginActivity.this,
                            "Koneksi timeout, silakan coba lagi",
                            Toast.LENGTH_SHORT).show();
                }
            }, 15000); // 15 detik timeout

            // Login dengan Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Login berhasil
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Mengambil data user dari database
                                mDatabase.child("users").child(user.getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                showLoading(false);
                                                UserData userData = snapshot.getValue(UserData.class);
                                                if (userData != null) {
                                                    // Login berhasil dan data user ditemukan
                                                    Toast.makeText(LoginActivity.this,
                                                            "Selamat datang, " + userData.getFullname(),
                                                            Toast.LENGTH_SHORT).show();
                                                    navigateToMain();
                                                } else {
                                                    // Data user tidak ditemukan
                                                    Toast.makeText(LoginActivity.this,
                                                            "Error: Data pengguna tidak ditemukan",
                                                            Toast.LENGTH_SHORT).show();
                                                    mAuth.signOut();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                showLoading(false);
                                                Toast.makeText(LoginActivity.this,
                                                        "Error: " + error.getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                                Log.e(TAG, "Database error: ", error.toException());
                                                mAuth.signOut();
                                            }
                                        });
                            }
                        } else {
                            // Login gagal
                            showLoading(false);
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() :
                                    "Authentication failed";
                            Toast.makeText(LoginActivity.this,
                                    "Login gagal: " + errorMessage,
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Login error: ", task.getException());
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Cek jika user sudah login
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToMain();
        }
    }

    private void handleGoogleSignIn() {
        showLoading(true);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void navigateToRegister() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                showLoading(false);
                Toast.makeText(this, "Google sign in gagal: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Simpan data user ke Firebase Database
                            UserData userData = new UserData(
                                    user.getDisplayName(),
                                    user.getEmail(),
                                    "",  // phone
                                    "",  // address
                                    ""   // gender
                            );
                            if (user.getPhotoUrl() != null) {
                                userData.setProfilePicUrl(user.getPhotoUrl().toString());
                            }

                            mDatabase.child("users").child(user.getUid())
                                    .setValue(userData)
                                    .addOnCompleteListener(dbTask -> {
                                        showLoading(false);
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this,
                                                    "Selamat datang, " + userData.getFullname(),
                                                    Toast.LENGTH_SHORT).show();
                                            navigateToMain();
                                        } else {
                                            Toast.makeText(LoginActivity.this,
                                                    "Gagal menyimpan data user",
                                                    Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                        }
                                    });
                        }
                    } else {
                        showLoading(false);
                        Toast.makeText(LoginActivity.this, "Authentication Failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}