package com.example.phongphung.appshopping.model;

public class Banner {

    private String name, image;
    private String productID;

    public Banner() {
    }

    public Banner(String productID, String name, String image) {
        this.productID = productID;
        this.name = name;
        this.image = image;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
