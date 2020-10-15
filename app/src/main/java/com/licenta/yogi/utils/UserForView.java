package com.licenta.yogi.utils;

public class UserForView {
    private String name,country,city,address;
    private long sold;

    public UserForView(String name, String country, String city, String address,long sold) {
        this.name = name;
        this.country = country;
        this.city = city;
        this.address = address;
        this.sold = sold;
    }

    public void setSold(long sold) {
        this.sold = sold;
    }
    public long getSold() {
        return sold;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
