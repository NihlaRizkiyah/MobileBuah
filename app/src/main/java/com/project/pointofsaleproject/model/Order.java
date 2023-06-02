package com.project.pointofsaleproject.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Order implements Parcelable {
    String id;
    String id_customer;
    String customer;
    String kode;
    String metode_kirim;
    String metode_bayar;
    String jam;
    String tanggal;
    String harga_total;

    protected Order(Parcel in) {
        id = in.readString();
        id_customer = in.readString();
        customer = in.readString();
        kode = in.readString();
        metode_kirim = in.readString();
        metode_bayar = in.readString();
        jam = in.readString();
        tanggal = in.readString();
        harga_total = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_customer() {
        return id_customer;
    }

    public void setId_customer(String id_customer) {
        this.id_customer = id_customer;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getMetode_kirim() {
        return metode_kirim;
    }

    public void setMetode_kirim(String metode_kirim) {
        this.metode_kirim = metode_kirim;
    }

    public String getMetode_bayar() {
        return metode_bayar;
    }

    public void setMetode_bayar(String metode_bayar) {
        this.metode_bayar = metode_bayar;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getHarga_total() {
        return harga_total;
    }

    public void setHarga_total(String harga_total) {
        this.harga_total = harga_total;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(id_customer);
        parcel.writeString(customer);
        parcel.writeString(kode);
        parcel.writeString(metode_kirim);
        parcel.writeString(metode_bayar);
        parcel.writeString(jam);
        parcel.writeString(tanggal);
        parcel.writeString(harga_total);
    }
}
