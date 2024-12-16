package com.example.finalmp;

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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    }

    private void loadMenuData() {
        showLoading(true);
        menuRef.child(menuId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentMenu = snapshot.getValue(Menu.class);
                if (currentMenu != null) {
                    updateUI(currentMenu);
                }
                showLoading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
                Toast.makeText(DetailMenuActivity.this,
                        "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Menu menu) {
        textViewName.setText(menu.getName());
        textViewDescription.setText(menu.getDescription());
        textViewPrice.setText(String.format(Locale.getDefault(),
                "Rp %,d", (int) menu.getPrice()));
        ratingBar.setRating(menu.getRating());

        Glide.with(this)
                .load(menu.getImageUrl())
                .placeholder(R.drawable.placeholder_food)
                .into(imageViewMenu);
    }

    private void addToCart() {
        if (currentMenu == null) return;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference favRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("favorites")
                .child(userId)
                .child(menuId);

        // Toggle favorite status
    }
}