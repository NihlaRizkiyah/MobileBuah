package com.project.pointofsaleproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.project.pointofsaleproject.model.Produk;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "produkSQLite.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE cart(id integer PRIMARY KEY AUTOINCREMENT, idProduk text, nama text, kode text, kategori text, harga integer, qty integer, image text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS cart");
        onCreate(db);
    }

    public long getCountCart(){
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, "cart");
        db.close();
        return count;
    }

    //insert Cart
    public Boolean insertCart(String id, String nama, String kategori, String kode, String harga, String image, int qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("idProduk", id);
        contentValues.put("image", image);
        contentValues.put("nama", nama);
        contentValues.put("kategori", kategori);
        contentValues.put("kode", kode);
        contentValues.put("harga", Integer.parseInt(harga));
        contentValues.put("qty", qty);
        long insert = db.insert("cart", null, contentValues);
        if (insert == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    public Boolean updateCart(String id, String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM cart WHERE idProduk = ?", new String[]{id});
        int total = 0;
        if(cursor.moveToFirst()) {
            if (type.equalsIgnoreCase("plus")){
                total = cursor.getInt(cursor.getColumnIndexOrThrow("qty")) + 1;
            }else {
                total = cursor.getInt(cursor.getColumnIndexOrThrow("qty")) - 1;
            }
        }
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("qty",total);
        long update = db.update("cart",  contentValues, "idProduk = ?", new String[]{id});
        db.close();
        if (update == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean deleteCart(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("cart", "idProduk" + "='" + id +"'", null) > 0;
    }

    public int getQtyCartById(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM cart WHERE idProduk = ?", new String[]{id});
        if(cursor.moveToFirst()) {
            return cursor.getInt(cursor.getColumnIndexOrThrow("qty"));
        }
        return 0;
    }

    public boolean getCartById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM cart WHERE idFurniture = ?", new String[]{id});
        if (cursor.getCount() > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public ArrayList<Produk> getDataProduk() {
        ArrayList<Produk> listProduk = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM cart", null);
        String id, nama = "", harga = "", image = "", kategori = "", kode = "kode";
        int qty = 0;
        if(cursor.moveToFirst()) {
            do{
                id = cursor.getString(cursor.getColumnIndexOrThrow("idProduk"));
                nama = cursor.getString(cursor.getColumnIndexOrThrow("nama"));
                image = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                kategori = cursor.getString(cursor.getColumnIndexOrThrow("kategori"));
                kode = cursor.getString(cursor.getColumnIndexOrThrow("kode"));
                harga = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("harga")));
                qty = cursor.getInt(cursor.getColumnIndexOrThrow("qty"));
                Produk item = new Produk();
                item.setId(id);
                item.setKode(kode);
                item.setImage(image);
                item.setNama(nama);
                item.setHarga(harga);
                item.setQty(qty);
                item.setKategori(kategori);
                listProduk.add(item);
            }while (cursor.moveToNext());
        }
        return listProduk;
    }

    public long getTotal() {
        long total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(harga*qty) as total FROM cart", null);
        if(cursor.moveToFirst()) {
            do{
                total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
            }while (cursor.moveToNext());
        }
        return total;
    }

    public boolean hapusDataCart(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("cart", "idFurniture" + "='" + id +"'", null) > 0;
    }

    public boolean hapusDataAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("cart", "", null) > 0;
    }
}
