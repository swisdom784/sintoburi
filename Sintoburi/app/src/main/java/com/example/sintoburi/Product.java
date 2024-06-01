package com.example.sintoburi;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private String price;
    private String imageUrl;
    private String description;
    private String category;
    private String uploaderEmail;
    private long uploadTime;
    private int favoriteCount;

    public Product() {
        // 기본 생성자
    }

    public Product(String name, String price, String imageUrl, String description, String category,
                   String uploaderEmail, long uploadTime, int favoriteCount) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.category = category;
        this.uploaderEmail = uploaderEmail;
        this.uploadTime = uploadTime;
        this.favoriteCount = favoriteCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getUploaderEmail() {
        return uploaderEmail;
    }

    public void setUploaderEmail(String uploaderEmail) {
        this.uploaderEmail = uploaderEmail;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }
}
