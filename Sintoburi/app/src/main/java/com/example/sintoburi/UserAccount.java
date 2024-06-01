package com.example.sintoburi;

import java.util.Map;

public class UserAccount {
    private String nickname;
    private String email;
    private String password;
    private Map<String, Boolean> uploadedProducts; // 유저가 등록한 상품 ID 맵
    private Map<String, Boolean> wishlistProducts; // 유저가 찜한 상품 ID 맵

    public UserAccount() {
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Map<String, Boolean> getUploadedProducts() {
        return uploadedProducts;
    }
    public void setUploadedProducts(Map<String, Boolean> uploadedProducts) {
        this.uploadedProducts = uploadedProducts;
    }
    public Map<String, Boolean> getWishlistProducts() {
        return wishlistProducts;
    }
    public void setWishlistProducts(Map<String, Boolean> wishlistProducts) {
        this.wishlistProducts = wishlistProducts;
    }
    public void addUploadedProduct(String productId) {
        this.uploadedProducts.put(productId, true);
    }
    public void removeUploadedProduct(String productId) {
        this.uploadedProducts.remove(productId);
    }
    public void addWishlistProduct(String productId) {
        this.wishlistProducts.put(productId, true);
    }
    public void removeWishlistProduct(String productId) {
        this.wishlistProducts.remove(productId);
    }
}
