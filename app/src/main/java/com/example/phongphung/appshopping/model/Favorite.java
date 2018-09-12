package com.example.phongphung.appshopping.model;


public class Favorite {
    private String productID, productName, productPrice, productCategoryID, productImage, productDiscount, productDescription, userPhone;

    public Favorite() {
    }

    public Favorite(String productID, String productName, String productPrice, String productCategoryID, String productImage, String productDiscount, String productDescription, String userPhone) {
        this.productID = productID;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productCategoryID = productCategoryID;
        this.productImage = productImage;
        this.productDiscount = productDiscount;
        this.productDescription = productDescription;
        this.userPhone = userPhone;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductCategoryID() {
        return productCategoryID;
    }

    public void setProductCategoryID(String productCategoryID) {
        this.productCategoryID = productCategoryID;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
