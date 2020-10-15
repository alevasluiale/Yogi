package com.licenta.yogi.models;


import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private String name,symbol,restrictedCountries,id,contractAddress;
    private long totalSupply,length,width,height,sold,left;
    private long productionDate,expirationDate;

    protected Product(Parcel in) {
        name = in.readString();
        symbol = in.readString();
        restrictedCountries = in.readString();
        id = in.readString();
        totalSupply = in.readLong();
        length = in.readLong();
        width = in.readLong();
        height = in.readLong();
        sold = in.readLong();
        left = in.readLong();
        productionDate = in.readLong();
        expirationDate = in.readLong();
        contractAddress = in.readString();
    }
    public Product() {
        name = "";
        symbol = "";
        restrictedCountries = "";
        id = "";
        totalSupply = 0;
        length = 0;
        width = 0;
        height = 0;
        sold = 0;
        left = 0;
        productionDate = System.currentTimeMillis();
        expirationDate = System.currentTimeMillis();
        contractAddress = "";
    }
    public Product(String name, String symbol, String restrictedCountries, long totalSupply, long length, long width, long height, long productionDate, long expirationDate) {
        this.name = name;
        this.symbol = symbol;
        this.restrictedCountries = restrictedCountries;
        this.totalSupply = totalSupply;
        this.length = length;
        this.width = width;
        this.height = height;
        this.productionDate = productionDate;
        this.expirationDate = expirationDate;
        this.left = totalSupply;
        this.sold = 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public void setSold(long sold) {
        this.sold = sold;
    }

    public void setLeft(long left) {
        this.left = left;
    }

    public long getSold() {
        return sold;
    }


    public long getLeft() {
        return left;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getRestrictedCountries() {
        return restrictedCountries;
    }

    public void setRestrictedCountries(String restrictedCountries) {
        this.restrictedCountries = restrictedCountries;
    }

    public long getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(long totalSupply) {
        this.totalSupply = totalSupply;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public long getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(long productionDate) {
        this.productionDate = productionDate;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(long expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static Creator<Product> getCREATOR() {
        return CREATOR;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(name);
        parcel.writeString(symbol);
        parcel.writeString(restrictedCountries);
        parcel.writeString(id);

        parcel.writeLong(totalSupply);
        parcel.writeLong(length);
        parcel.writeLong(height);
        parcel.writeLong(width);
        parcel.writeLong(sold);
        parcel.writeLong(left);
        parcel.writeLong(productionDate);
        parcel.writeLong(expirationDate);
    }
}
