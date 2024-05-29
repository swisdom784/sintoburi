package com.example.sintoburi;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private String price;
    private String imageUrl;
    private String description;
    private String category; // 카테고리 필드 추가

    public Product() {
        // 기본 생성자
    }

    public Product(String name, String price, String imageUrl, String description, String category) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
}
