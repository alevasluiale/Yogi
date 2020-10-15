package com.licenta.yogi.models;

public class User {
    private String firstName, lastName, email,country,city, walletFileName,walletAddress,id;
    private int userType;
    public User() {
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.country = "";
        this.city = "";
        this.walletAddress = "";
        this.walletFileName = "";
        this.id = "";
        this.userType = -1;
    }
    public User(String email,String firstName,String lastName,String country,String city,int userType,String walletFileName,String walletAddress,String id){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.city = city;
        this.userType = userType;
        this.walletFileName = walletFileName;
        this.walletAddress = walletAddress;
        this.id = id;
    }
    public User(String email,String firstName,String lastName,String country,String city,int userType,String walletFileName,String walletAddress){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.city = city;
        this.userType = userType;
        this.walletFileName = walletFileName;
        this.walletAddress = walletAddress;
    }
    public User(String email,String firstName,String lastName,String country,String city,int userType) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.city = city;
        this.userType = userType;
    }

    @Override
    public String toString() {
        return firstName+" "+lastName + "  -  " + country + ", " + city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getWalletFileName() {
        return walletFileName;
    }

    public void setWalletFileName(String walletFileName) {
        this.walletFileName = walletFileName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
