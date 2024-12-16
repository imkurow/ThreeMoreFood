package com.example.finalmp;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {
    private EditText editTextAddress;
    private RadioGroup radioGroupPayment;
    private TextView textViewTotal;
    private Button buttonConfirmOrder;
//    private List<CartItem> cartItems;
    private double totalAmount;
    private static final String TAG = "CheckoutActivity";
    private List<CartItemParcelable> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        ArrayList<CartItemParcelable> items = getIntent()
                .getParcelableArrayListExtra("cartItems");

        if (items == null) {
            Log.e(TAG, "Cart items is null");
            Toast.makeText(this, "Error: Keranjang kosong",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cartItems = items;
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0);

        setupToolbar();
        initViews();
        setupViews();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        editTextAddress = findViewById(R.id.editTextAddress);
        radioGroupPayment = findViewById(R.id.radioGroupPayment);
        textViewTotal = findViewById(R.id.textViewTotal);
        buttonConfirmOrder = findViewById(R.id.buttonConfirmOrder);
    }

    private void setupViews() {
        textViewTotal.setText(String.format(Locale.getDefault(),
                "Total: Rp %,d", (int) totalAmount));

        buttonConfirmOrder.setOnClickListener(v -> processOrder());
    }

    private void processOrder() {
        String address = editTextAddress.getText().toString().trim();
        if (address.isEmpty()) {
            editTextAddress.setError("Alamat harus diisi");
            return;
        }

        int selectedPaymentId = radioGroupPayment.getCheckedRadioButtonId();
        if (selectedPaymentId == -1) {
            Toast.makeText(this, "Pilih metode pembayaran",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentMethod = selectedPaymentId == R.id.radioButtonCash ?
                "Tunai" : "Transfer Bank";

        createOrder(address, paymentMethod);
    }

    private void createOrder(String address, String paymentMethod) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference ordersRef = FirebaseDatabase.getInstance()
                .getReference().child("orders");
        String orderId = ordersRef.push().getKey();
        if (orderId == null) return;

        // Convert CartItemParcelable back to CartItem for Firebase
        List<CartItem> orderItems = new ArrayList<>();
        for (CartItemParcelable parcelableItem : cartItems) {
            Menu menu = new Menu();
            menu.setId(parcelableItem.getMenuId());
            menu.setName(parcelableItem.getMenuName());
            menu.setPrice(parcelableItem.getMenuPrice());
            menu.setImageUrl(parcelableItem.getMenuImage());

            CartItem item = new CartItem(menu, parcelableItem.getQuantity());
            orderItems.add(item);
        }

        Order order = new Order(
                orderId,
                user.getUid(),
                orderItems,
                totalAmount,
                "pending",
                System.currentTimeMillis(),
                address,
                paymentMethod
        );

        ordersRef.child(orderId).setValue(order)
                .addOnSuccessListener(aVoid -> {
                    clearCart(user.getUid());
                    showSuccessDialog();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating order: " + e.getMessage());
                    Toast.makeText(CheckoutActivity.this,
                            "Gagal membuat pesanan: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void clearCart(String userId) {
        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                .getReference().child("carts").child(userId);
        cartRef.removeValue()
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error clearing cart: " + e.getMessage()));
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Pesanan Berhasil")
                .setMessage("Pesanan Anda telah berhasil dibuat")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
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