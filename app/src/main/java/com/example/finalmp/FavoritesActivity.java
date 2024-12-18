package com.example.finalmp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private List<Menu> favoriteMenus = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        setupToolbar();
        initViews();
        loadFavorites();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Menu Favorit");
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewFavorites);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MenuAdapter(favoriteMenus);
        recyclerView.setAdapter(adapter);
    }

    private void loadFavorites() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        showLoading(true);
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("favorites")
                .child(user.getUid());

        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteMenus.clear();
                List<String> favoriteIds = new ArrayList<>();

                for (DataSnapshot favoriteSnapshot : snapshot.getChildren()) {
                    favoriteIds.add(favoriteSnapshot.getKey());
                }

                if (favoriteIds.isEmpty()) {
                    showEmptyState(true);
                    showLoading(false);
                    return;
                }

                loadMenuDetails(favoriteIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
                showEmptyState(true);
            }
        });
    }

    private void loadMenuDetails(List<String> menuIds) {
        DatabaseReference menuRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("menus");

        menuRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteMenus.clear();
                for (String menuId : menuIds) {
                    DataSnapshot menuSnapshot = snapshot.child(menuId);
                    Menu menu = menuSnapshot.getValue(Menu.class);
                    if (menu != null) {
                        menu.setFavorite(true);
                        favoriteMenus.add(menu);
                    }
                }

                adapter.updateData(favoriteMenus);
                showLoading(false);
                showEmptyState(favoriteMenus.isEmpty());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
                showEmptyState(true);
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showEmptyState(boolean show) {
        emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
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