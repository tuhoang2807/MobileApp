package com.example.moblie_app.ViewModel;

public class Product {
    private int id;
    private String name;
    private String price;
    private String description;
    private int category_id;
    private int quantity;
    private String image;

    public Product(String name, String price, String description, int category_id, int quantity, String image, int id) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.category_id = category_id;
        this.quantity = quantity;
        this.image = image;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public int getCategoryId() {
        return category_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getId() {
        return id;
    }
}
