package com.project.pointofsaleproject.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Produk implements Parcelable {
    String id;
    String nama;
    String id_kategori;
    String kategori;
    String kode;
    String harga;
    String stok;
    String image;
    String keterangan;
    int qty = 0;
    int onCart = 0;

    public Produk(){

    }

    protected Produk(Parcel in) {
        id = in.readString();
        nama = in.readString();
        id_kategori = in.readString();
        kategori = in.readString();
        kode = in.readString();
        harga = in.readString();
        stok = in.readString();
        image = in.readString();
        keterangan = in.readString();
        qty = in.readInt();
        onCart = in.readInt();
    }

    public static final Creator<Produk> CREATOR = new Creator<Produk>() {
        @Override
        public Produk createFromParcel(Parcel in) {
            return new Produk(in);
        }

        @Override
        public Produk[] newArray(int size) {
            return new Produk[size];
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

    public String getId_kategori() {
        return id_kategori;
    }

    public void setId_kategori(String id_kategori) {
        this.id_kategori = id_kategori;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getStok() {
        return stok;
    }

    public void setStok(String stok) {
        this.stok = stok;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getOnCart() {
        return onCart;
    }

    public void setOnCart(int onCart) {
        this.onCart = onCart;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(nama);
        parcel.writeString(id_kategori);
        parcel.writeString(kategori);
        parcel.writeString(kode);
        parcel.writeString(harga);
        parcel.writeString(stok);
        parcel.writeString(image);
        parcel.writeString(keterangan);
        parcel.writeInt(qty);
        parcel.writeInt(onCart);
    }
}
