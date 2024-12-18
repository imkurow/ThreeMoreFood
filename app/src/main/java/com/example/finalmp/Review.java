package com.example.finalmp;

public class Review {
    private String userId;
    private String userName;
    private String menuId;
    private float rating;
    private String comment;
    private long timestamp;

    // Required empty constructor for Firebase
    public Review() {}

    public Review(String userId, String userName, String menuId, float rating, String comment) {
        this.userId = userId;
        this.userName = userName;
        this.menuId = menuId;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getMenuId() { return menuId; }
    public void setMenuId(String menuId) { this.menuId = menuId; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}