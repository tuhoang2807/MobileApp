package com.example.moblie_app.ViewModel;

public class OrderItem {

    private String address;
    private String created_at;
    private int id;
    private String phone_number;
    private String status;
    private String total_amount;
    private int userId;
    private String username;


    public OrderItem(String address, String created_at, int id, String phone_number, String status, String total_amount, int userId, String username) {
        this.address = address;
        this.created_at = created_at;
        this.id = id;
        this.phone_number = phone_number;
        this.status = status;
        this.total_amount = total_amount;
        this.userId = userId;
        this.username = username;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getcreated_at() {
        return created_at;
    }

    public void setcreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getphone_number() {
        return phone_number;
    }

    public void setphone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String gettotal_amount() {
        return total_amount;
    }

    public void settotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
