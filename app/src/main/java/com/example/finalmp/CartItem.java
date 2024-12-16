package com.example.finalmp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CartItem implements Parcelable {
    private Menu menu;
    private int quantity;
    private String cartId;
    private String userId;
    private long timestamp;
    private String orderStatus;
    private String deliveryAddress;

    // Constructor
    public CartItem() {}

    public CartItem(Menu menu, int quantity) {
        this.menu = menu;
        this.quantity = quantity;
        this.timestamp = System.currentTimeMillis();
        this.orderStatus = "pending";
    }

    protected CartItem(Parcel in) {
        quantity = in.readInt();
        cartId = in.readString();
        userId = in.readString();
        timestamp = in.readLong();
        orderStatus = in.readString();
        deliveryAddress = in.readString();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    // Getter dan Setter yang hilang
    public Menu getMenu() { return menu; }
    public void setMenu(Menu menu) { this.menu = menu; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = Math.max(1, quantity);
    }

    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    // Method untuk kalkulasi total
    public double getTotalPrice() {
        return menu != null ? menu.getPrice() * quantity : 0;
    }

    // Method untuk update quantity
    public void incrementQuantity() {
        this.quantity++;
    }

    public boolean decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
            return true;
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable((Parcelable) menu, flags);
        dest.writeInt(quantity);
        dest.writeString(cartId);
        dest.writeString(userId);
        dest.writeLong(timestamp);
        dest.writeString(orderStatus);
        dest.writeString(deliveryAddress);
    }
}