package com.example.finalmp;

public class CartItem {
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
}