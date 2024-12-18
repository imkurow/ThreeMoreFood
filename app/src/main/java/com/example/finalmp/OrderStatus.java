package com.example.finalmp;

public enum OrderStatus {
    PENDING("Menunggu Konfirmasi"),
    PROCESSING("Sedang Diproses"),
    ON_DELIVERY("Dalam Pengiriman"),
    COMPLETED("Selesai"),
    CANCELLED("Dibatalkan");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}