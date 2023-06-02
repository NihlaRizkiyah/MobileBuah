package com.project.pointofsaleproject.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiRest {

    @FormUrlEncoded
    @POST("api/login")
    Call<JsonObject> login(@Field("username") String username,
                           @Field("password") String password);

    @FormUrlEncoded
    @POST("api/updateprofil")
    Call<JsonObject> updateProfil(@Field("id") String id,
                                  @Field("username") String username,
                                  @Field("password") String password,
                                  @Field("nomor") String nomor);

    @GET("api/customer")
    Call<JsonArray> getCustomer();

    @FormUrlEncoded
    @POST("api/addcustomer")
    Call<JsonObject> saveCustomer(@Field("nama") String nama, @Field("nomor") String nomor,
                                  @Field("email") String email, @Field("alamat") String alamat);

    @FormUrlEncoded
    @POST("api/deletecustomer")
    Call<JsonObject> deleteCustomer(@Field("id") String id);

    @FormUrlEncoded
    @POST("api/editcustomer")
    Call<JsonObject> editCustomer(@Field("id") String id, @Field("nama") String nama,
                                  @Field("nomor") String nomor, @Field("email") String email,
                                  @Field("alamat") String alamat);

    @GET("api/kategori")
    Call<JsonArray> getKategori();

    @Multipart
    @POST("api/addkategori")
    Call<JsonObject> saveKategori(@Part("nama") RequestBody nama, @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST("api/deletekategori")
    Call<JsonObject> deleteKategori(@Field("id") String id);

    @Multipart
    @POST("api/editkategori")
    Call<JsonObject> editKategori(@Part("id") RequestBody id, @Part("nama") RequestBody nama, @Part MultipartBody.Part image);

    @GET("api/produk")
    Call<JsonArray> getProduk();

    @Multipart
    @POST("api/addproduk")
    Call<JsonObject> saveProduk(@Part("nama") RequestBody nama,
                                @Part("id_kategori") RequestBody id_kategori,
                                @Part("kode") RequestBody kode,
                                @Part("harga") RequestBody harga,
                                @Part("stok") RequestBody stok,
                                @Part("keterangan") RequestBody keterangan,
                                @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST("api/deleteproduk")
    Call<JsonObject> deleteProduk(@Field("id") String id);

    @Multipart
    @POST("api/editproduk")
    Call<JsonObject> editProduk(@Part("id") RequestBody id,
                                @Part("nama") RequestBody nama,
                                @Part("id_kategori") RequestBody id_kategori,
                                @Part("kode") RequestBody kode,
                                @Part("harga") RequestBody harga,
                                @Part("stok") RequestBody stok,
                                @Part("keterangan") RequestBody keterangan,
                                @Part MultipartBody.Part image);


    @GET("api/order")
    Call<JsonArray> getOrder();

    @FormUrlEncoded
    @POST("api/addorder")
    Call<JsonObject> saveOrder(@Field("id_customer") String id_customer, @Field("metode_bayar") String metode_bayar,
                               @Field("metode_kirim") String metode_kirim, @Field("sub_total") String sub_total,
                               @Field("dataproduk") String dataproduk);

    @GET("api/orderdetail/{id}")
    Call<JsonArray> getOrderDetail(@Path("id") String id);



}
