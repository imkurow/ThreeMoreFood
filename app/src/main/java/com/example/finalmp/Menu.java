package com.example.finalmp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Menu implements Parcelable {
    private String id;
    private String name;
    private String description;
    private String category;
    private double price;
    private String imageUrl;
    private float rating;
    private int orderCount;
    private boolean isFavorite;

    public Menu() {
    }


    public Menu(String id, String name, String description, String category,
                double price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.imageUrl = imageUrl;
        this.rating = 0;
        this.orderCount = 0;
    }

    protected Menu(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        category = in.readString();
        price = in.readDouble();
        imageUrl = in.readString();
        rating = in.readFloat();
        orderCount = in.readInt();
        isFavorite = in.readByte() != 0;
    }

    public static final Creator<Menu> CREATOR = new Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(category);
        dest.writeDouble(price);
        dest.writeString(imageUrl);
        dest.writeFloat(rating);
        dest.writeInt(orderCount);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }
}
