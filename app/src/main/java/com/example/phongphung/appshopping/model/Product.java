package com.example.phongphung.appshopping.model;

public class Product {

    private int id ;
    private String name, image, description,idCategory;
    private double discount;
    private float price;

    public Product() {
    }

    public Product(int id, String name, String image, String description, String idCategory, double discount, float price) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
        this.idCategory = idCategory;
        this.discount = discount;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
