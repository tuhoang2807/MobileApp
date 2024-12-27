package com.example.moblie_app.ViewModel;

public class CartItem {
    private String productName;
    private int quantity;
    private String price;
    private String imageUrl;
    private int id;
    private int user_id;
    private int product_id;
    private String total_money;
    private String message;
    private UpdatedFields updatedFields;

    public CartItem(String productName, int quantity, String price, String imageUrl, int id, int user_id, int product_id, String total_money, String message, UpdatedFields updatedFields) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
        this.id = id;
        this.user_id = user_id;
        this.product_id = product_id;
        this.total_money = total_money;
        this.message = message;
        this.updatedFields = updatedFields;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UpdatedFields getUpdatedFields() {
        return updatedFields;
    }

    public void setUpdatedFields(UpdatedFields updatedFields) {
        this.updatedFields = updatedFields;
    }

    public static class UpdatedFields {
        private int quantity;
        private int total_money;

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getTotalMoney() {
            return total_money;
        }

        public void setTotalMoney(int total_money) {
            this.total_money = total_money;
        }
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int userId) {
        this.user_id = userId;
    }

    public int getProductId() {
        return product_id;
    }

    public void setProductId(int productId) {
        this.product_id = productId;
    }

    public String getTotalMoney() {
        return total_money;
    }

    public void setTotalMoney(String totalMoney) {
        this.total_money = totalMoney;
    }
}
