package com.project.pointofsaleproject.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Fruit implements Parcelable {
    String id;
    String image;
    String nama;
    String price;

    public Fruit(){

    }

    protected Fruit(Parcel in) {
        id = in.readString();
        image = in.readString();
        nama = in.readString();
        price = in.readString();
    }

    public static final Creator<Fruit> CREATOR = new Creator<Fruit>() {
        @Override
        public Fruit createFromParcel(Parcel in) {
            return new Fruit(in);
        }

        @Override
        public Fruit[] newArray(int size) {
            return new Fruit[size];
        }
    };

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(image);
        parcel.writeString(nama);
        parcel.writeString(price);
    }
}
