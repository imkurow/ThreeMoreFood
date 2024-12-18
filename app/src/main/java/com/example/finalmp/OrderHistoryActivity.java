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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;
    private ProgressBar progressBar;
    private TextView textViewEmpty;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }

        setupToolbar();
        initViews();
        loadOrderHistory();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewOrders);
        progressBar = findViewById(R.id.progressBar);
        textViewEmpty = findViewById(R.id.textViewEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderHistoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void loadOrderHistory() {
        showLoading(true);
        DatabaseReference ordersRef = FirebaseDatabase.getInstance()
                .getReference().child("orders");
        Query query = ordersRef.orderByChild("userId")
                .equalTo(currentUser.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> orders = new ArrayList<>();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        orders.add(order);
                    }
                }

                // Sort orders by time (newest first)
                Collections.sort(orders, (o1, o2) ->
                        Long.compare(o2.getOrderTime(), o1.getOrderTime()));

                adapter.updateData(orders);
                showLoading(false);
                updateEmptyState(orders.isEmpty());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
                updateEmptyState(true);
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateEmptyState(boolean isEmpty) {
        textViewEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
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