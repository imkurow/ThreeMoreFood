package com.example.finalmp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuAdapter;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuListActivity extends AppCompatActivity {
    private RecyclerView recyclerViewMenu;
    private MenuAdapter menuAdapter;
    private ProgressBar progressBar;
    private DatabaseReference menuRef;
    private String category;
    private SearchView searchView;
    private List<Menu> fullMenuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        // Dapatkan kategori dari intent
        category = getIntent().getStringExtra("category");
        if (category == null) {
            Toast.makeText(this, "Kategori tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        initViews();
        setupSearchView();
        loadMenuData();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(category);
        }
    }

    private void initViews() {
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu);
        progressBar = findViewById(R.id.progressBar);
        searchView = findViewById(R.id.searchView);

        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(this));
        menuAdapter = new MenuAdapter(new ArrayList<>());
        recyclerViewMenu.setAdapter(menuAdapter);

        menuRef = FirebaseDatabase.getInstance().getReference().child("menus");
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterMenu(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMenu(newText);
                return true;
            }
        });
    }

    private void filterMenu(String query) {
        if (fullMenuList == null) return;

        List<Menu> filteredList = new ArrayList<>();
        for (Menu menu : fullMenuList) {
            if (menu.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(menu);
            }
        }
        menuAdapter.updateData(filteredList);
    }

    private void loadMenuData() {
        showLoading(true);
        Query query = menuRef.orderByChild("category").equalTo(category);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Menu> menuList = new ArrayList<>();
                for (DataSnapshot menuSnapshot : snapshot.getChildren()) {
                    Menu menu = menuSnapshot.getValue(Menu.class);
                    if (menu != null) {
                        menuList.add(menu);
                    }
                }
                fullMenuList = new ArrayList<>(menuList); // Simpan salinan lengkap
                menuAdapter.updateData(menuList);
                showLoading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
                Toast.makeText(MenuListActivity.this,
                        "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewMenu.setVisibility(show ? View.GONE : View.VISIBLE);
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
