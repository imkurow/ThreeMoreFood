package com.example.finalmp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemChangeListener {
    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private TextView textViewTotal;
    private Button buttonCheckout;
    private DatabaseReference cartRef;
    private String userId;
    private List<CartItem> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Silakan login terlebih dahulu",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        userId = user.getUid();
        cartRef = FirebaseDatabase.getInstance().getReference()
                .child("carts").child(userId);

        setupToolbar();
        initViews();
        loadCartItems();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        textViewTotal = findViewById(R.id.textViewTotal);
        buttonCheckout = findViewById(R.id.buttonCheckout);

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartItems, this);
        recyclerViewCart.setAdapter(cartAdapter);

        buttonCheckout.setOnClickListener(v -> processCheckout());
    }

    private void loadCartItems() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear();
                double total = 0;

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    CartItem item = itemSnapshot.getValue(CartItem.class);
                    if (item != null) {
                        cartItems.add(item);
                        total += item.getTotalPrice();
                    }
                }

                cartAdapter.updateData(cartItems);
                updateTotal(total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this,
                        "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotal(double total) {
        textViewTotal.setText(String.format(Locale.getDefault(),
                "Total: Rp %,d", (int) total));
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        cartRef.child(item.getMenu().getId())
                .child("quantity")
                .setValue(newQuantity);
    }

    @Override
    public void onItemDeleted(CartItem item) {
        cartRef.child(item.getMenu().getId()).removeValue();
    }

    private void processCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Keranjang kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<CartItemParcelable> parcelableItems = new ArrayList<>();
        double total = 0;

        for (CartItem item : cartItems) {
            CartItemParcelable parcelableItem = new CartItemParcelable(item);
            parcelableItems.add(parcelableItem);
            total += item.getTotalPrice();
        }

        Intent intent = new Intent(this, CheckoutActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("cartItems", parcelableItems);
        bundle.putDouble("totalAmount", total);
        intent.putExtras(bundle);

        Log.d("CartActivity", "Sending " + parcelableItems.size() + " items");
        Log.d("CartActivity", "Total amount: " + total);

        startActivity(intent);
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