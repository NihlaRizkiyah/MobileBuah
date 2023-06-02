package com.project.pointofsaleproject.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Customer implements Parcelable {
    String id;
    String nama;
    String nomor_hp;
    String email;
    String alamat;

    public Customer(){

    }

    protected Customer(Parcel in) {
        id = in.readString();
        nama = in.readString();
        nomor_hp = in.readString();
        email = in.readString();
        alamat = in.readString();
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNomor_hp() {
        return nomor_hp;
    }

    public void setNomor_hp(String nomor_hp) {
        this.nomor_hp = nomor_hp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(nama);
        parcel.writeString(nomor_hp);
        parcel.writeString(email);
        parcel.writeString(alamat);
    }
}
