package com.example.finalmp;

public class CartItem {
    private Menu menu;
    private int quantity;
    private String cartId;
    private String userId;
    private long timestamp;
    private String orderStatus; // "pending", "confirmed", "processing", "delivered"
    private String deliveryAddress;

    public CartItem() {}

    public CartItem(Menu menu, int quantity) {
        this.menu = menu;
        this.quantity = quantity;
        this.timestamp = System.currentTimeMillis();
        this.orderStatus = "pending";
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    // Method untuk validasi quantity
    public void setQuantity(int quantity) {
        this.quantity = Math.max(1, quantity); // Minimal 1
    }
}