package com.example.phongphung.appshopping.model;

public class User {
    private String name, password, phone, isStaff, secureCode;
    private int balance;

    public User() {
    }


    public User(String phone, String name, String password, String secureCode) {
        this.phone = phone;
        this.name = name;
        this.password = password;
        isStaff = "false";
        this.secureCode = secureCode;
        balance = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
