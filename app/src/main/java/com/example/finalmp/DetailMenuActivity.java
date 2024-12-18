package com.example.finalmp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailMenuActivity extends AppCompatActivity {
    private ImageView imageViewMenu;
    private TextView textViewName, textViewDescription, textViewPrice;
    private RatingBar ratingBar;
    private Button buttonAddToCart;
    private ProgressBar progressBar;
    private DatabaseReference menuRef;
    private String menuId;
    private Menu currentMenu;
    private Button buttonFavorite;
    private RatingBar userRatingBar;
    private EditText reviewEditText;
    private RecyclerView reviewsRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviews = new ArrayList<>();
    private Button buttonSubmitReview;
    private ValueEventListener menuValueEventListener;
    private boolean isActivityDestroyed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_menu);

        menuId = getIntent().getStringExtra("menuId");
        if (menuId == null) {
            Toast.makeText(this, "Menu tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        initViews();
        loadMenuData();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Menu");
        }
    }

    private void initViews() {
        imageViewMenu = findViewById(R.id.imageViewMenu);
        textViewName = findViewById(R.id.textViewName);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewPrice = findViewById(R.id.textViewPrice);
        ratingBar = findViewById(R.id.ratingBar);
        buttonAddToCart = findViewById(R.id.buttonAddToCart);
        progressBar = findViewById(R.id.progressBar);
        buttonFavorite = findViewById(R.id.buttonFavorite);
        userRatingBar = findViewById(R.id.userRatingBar);
        reviewEditText = findViewById(R.id.reviewEditText);
        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView);

        menuRef = FirebaseDatabase.getInstance().getReference().child("menus");

        buttonAddToCart.setOnClickListener(v -> addToCart());
        buttonFavorite.setOnClickListener(v -> toggleFavorite());
        buttonSubmitReview = findViewById(R.id.buttonSubmitReview);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(reviews);
        reviewsRecyclerView.setAdapter(reviewAdapter);

        buttonSubmitReview.setOnClickListener(v -> submitReview());

        // Load existing reviews
        loadReviews();

    }

    private void loadMenuData() {
        showLoading(true);
        menuValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isActivityDestroyed) {  // Check if activity is still active
                    currentMenu = snapshot.getValue(Menu.class);
                    if (currentMenu != null) {
                        updateUI(currentMenu);
                    }
                    showLoading(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isActivityDestroyed) {  // Check if activity is still active
                    showLoading(false);
                    Toast.makeText(DetailMenuActivity.this,
                            "Error: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        menuRef.child(menuId).addValueEventListener(menuValueEventListener);
    }

    private void updateUI(Menu menu) {
        if (!isActivityDestroyed) {  // Check if activity is still active
            textViewName.setText(menu.getName());
            textViewDescription.setText(menu.getDescription());
            textViewPrice.setText(String.format(Locale.getDefault(),
                    "Rp %,d", (int) menu.getPrice()));
            ratingBar.setRating(menu.getRating());

            // Check if activity is not destroyed before loading image
            if (!isDestroyed() && !isFinishing()) {
                Glide.with(this)
                        .load(menu.getImageUrl())
                        .placeholder(R.drawable.placeholder_food)
                        .into(imageViewMenu);
            }
        }
    }

    private void addToCart() {
        if (currentMenu == null) return;

        // Cek apakah user sudah login
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            // Optional: redirect ke halaman login
             startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("carts")
                .child(userId)
                .child(menuId);

        CartItem cartItem = new CartItem(currentMenu, 1);

        cartRef.setValue(cartItem)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Berhasil ditambahkan ke keranjang",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal menambahkan ke keranjang: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleFavorite() {
        // Cek apakah user sudah login
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference favRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("favorites")
                .child(userId)
                .child(menuId);

        // Toggle favorite status
        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Jika sudah favorite, hapus dari favorite
                    favRef.removeValue()
                            .addOnSuccessListener(aVoid -> {
                                buttonFavorite.setSelected(false);
                                Toast.makeText(DetailMenuActivity.this,
                                        "Dihapus dari favorit",
                                        Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // Jika belum favorite, tambahkan ke favorite
                    favRef.setValue(true)
                            .addOnSuccessListener(aVoid -> {
                                buttonFavorite.setSelected(true);
                                Toast.makeText(DetailMenuActivity.this,
                                        "Ditambahkan ke favorit",
                                        Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailMenuActivity.this,
                        "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReviews() {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("reviews")
                .child(menuId);

        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviews.clear();
                float totalRating = 0;
                int ratingCount = 0;

                for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    Review review = reviewSnapshot.getValue(Review.class);
                    if (review != null) {
                        reviews.add(review);
                        totalRating += review.getRating();
                        ratingCount++;
                    }
                }

                // Update menu's average rating
                if (ratingCount > 0) {
                    float averageRating = totalRating / ratingCount;
                    updateMenuRating(averageRating);
                }

                reviewAdapter.updateData(reviews);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailMenuActivity.this,
                        "Error loading reviews: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitReview() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Silakan login terlebih dahulu",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        float rating = userRatingBar.getRating();
        String comment = reviewEditText.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Berikan rating terlebih dahulu",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (comment.isEmpty()) {
            Toast.makeText(this, "Tulis ulasan Anda", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get user's name from Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(currentUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userData = snapshot.getValue(UserData.class);
                String userName = userData != null ? userData.getFullname() : "Anonymous";

                Review review = new Review(
                        currentUser.getUid(),
                        userName,
                        menuId,
                        rating,
                        comment
                );

                DatabaseReference reviewRef = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("reviews")
                        .child(menuId)
                        .child(currentUser.getUid());

                reviewRef.setValue(review)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(DetailMenuActivity.this,
                                    "Ulasan berhasil ditambahkan",
                                    Toast.LENGTH_SHORT).show();
                            userRatingBar.setRating(0);
                            reviewEditText.setText("");
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(DetailMenuActivity.this,
                                        "Gagal menambahkan ulasan: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show()
                        );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailMenuActivity.this,
                        "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateMenuRating(float newRating) {
        DatabaseReference menuRatingRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("menus")
                .child(menuId)
                .child("rating");

        menuRatingRef.setValue(newRating);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityDestroyed = true;
        // Remove the ValueEventListener
        if (menuValueEventListener != null && menuRef != null) {
            menuRef.child(menuId).removeEventListener(menuValueEventListener);
        }
    }
}