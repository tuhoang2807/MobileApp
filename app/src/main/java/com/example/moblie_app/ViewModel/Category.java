package com.example.moblie_app.ViewModel;

public class Category {
    private final int id;
    private final String name;
    private final int imageResId;

    public Category(int id, String name, int imageResId) {
        this.id = id;
        this.name = name;
        this.imageResId = imageResId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}
