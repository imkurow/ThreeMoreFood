package com.example.finalmp;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItemParcelable implements Parcelable {
    private String menuId;
    private String menuName;
    private double menuPrice;
    private String menuImage;
    private int quantity;

    public CartItemParcelable(CartItem cartItem) {
        this.menuId = cartItem.getMenu().getId();
        this.menuName = cartItem.getMenu().getName();
        this.menuPrice = cartItem.getMenu().getPrice();
        this.menuImage = cartItem.getMenu().getImageUrl();
        this.quantity = cartItem.getQuantity();
    }

    protected CartItemParcelable(Parcel in) {
        menuId = in.readString();
        menuName = in.readString();
        menuPrice = in.readDouble();
        menuImage = in.readString();
        quantity = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(menuId);
        dest.writeString(menuName);
        dest.writeDouble(menuPrice);
        dest.writeString(menuImage);
        dest.writeInt(quantity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CartItemParcelable> CREATOR = new Creator<CartItemParcelable>() {
        @Override
        public CartItemParcelable createFromParcel(Parcel in) {
            return new CartItemParcelable(in);
        }

        @Override
        public CartItemParcelable[] newArray(int size) {
            return new CartItemParcelable[size];
        }
    };

    // Getter methods
    public String getMenuId() { return menuId; }
    public String getMenuName() { return menuName; }
    public double getMenuPrice() { return menuPrice; }
    public String getMenuImage() { return menuImage; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return menuPrice * quantity; }
}