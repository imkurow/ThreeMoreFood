package com.example.finalmp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView recyclerViewCategories;
    private RecyclerView recyclerViewPopular;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private CategoryAdapter categoryAdapter;
    private MenuAdapter popularMenuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Inisialisasi menu data
        MenuDataInitializer initializer = new MenuDataInitializer(this);
        initializer.initializeMenuData();

        // Setup UI
        setupToolbar();
        setupNavigationDrawer();
        setupRecyclerViews();
        loadUserData();
        loadMenuData();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle navigation menu items
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            } else if (id == R.id.nav_orders) {
                startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
            } else if (id == R.id.nav_logout) {
                logout();
            } else if (id == R.id.nav_favorites) {
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupRecyclerViews() {
        // Setup Categories RecyclerView
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        recyclerViewCategories.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(this);
        recyclerViewCategories.setAdapter(categoryAdapter);

        // Setup Popular Menu RecyclerView
        recyclerViewPopular = findViewById(R.id.recyclerViewPopular);
        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(this));
        popularMenuAdapter = new MenuAdapter(new ArrayList<>());
        recyclerViewPopular.setAdapter(popularMenuAdapter);
    }

    private void loadMenuData() {
        DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference().child("menus");
        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Menu> popularMenus = new ArrayList<>();
                for (DataSnapshot menuSnapshot : snapshot.getChildren()) {
                    Menu menu = menuSnapshot.getValue(Menu.class);
                    if (menu != null) {
                        popularMenus.add(menu);
                    }
                }

                // Sort berdasarkan rating atau orderCount untuk menu populer
                Collections.sort(popularMenus, (m1, m2) ->
                        Float.compare(m2.getRating(), m1.getRating()));

                // Ambil 5 menu teratas
                List<Menu> topMenus = popularMenus.size() > 5 ?
                        popularMenus.subList(0, 5) : popularMenus;

                popularMenuAdapter.updateData(topMenus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,
                        "Error loading menu: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateNavigationHeader(UserData userData) {
        View headerView = navigationView.getHeaderView(0);
        TextView textViewName = headerView.findViewById(R.id.textViewName);
        TextView textViewEmail = headerView.findViewById(R.id.textViewEmail);
        ImageView imageViewProfile = headerView.findViewById(R.id.imageViewProfile);

        textViewName.setText(userData.getFullname());
        textViewEmail.setText(userData.getEmail());

        if (userData.getProfilePicUrl() != null && !userData.getProfilePicUrl().isEmpty()) {
            Glide.with(this)
                    .load(userData.getProfilePicUrl())
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .circleCrop()
                    .into(imageViewProfile);
        }
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = mDatabase.child("users").child(userId);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserData userData = snapshot.getValue(UserData.class);
                    if (userData != null) {
                        updateNavigationHeader(userData);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this,
                            "Error loading user data: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}