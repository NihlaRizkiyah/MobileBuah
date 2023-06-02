package com.project.pointofsaleproject.api;

public class UtilsApi {
    //public static final String BASE_URL_API = "http://10.0.2.2:8000/";
    public static final String BASE_URL_API = "https://pointofsalesappskripsi.000webhostapp.com/";

    // Mendeklarasikan Interface BaseApiServic e
    public static ApiRest getAPIService(){
        return ApiClient.getClient(BASE_URL_API).create(ApiRest.class);
    }

    public static final String BASE_URL_API_IMAGE_KATEGORI = BASE_URL_API + "img/kategori/";
    public static final String BASE_URL_API_IMAGE_PRODUK = BASE_URL_API + "img/produk/";
}

//Class ini berfungsi untuk menggabungkan
// class RetrofitClient dan juga meng-deklarasikan class interface ApiRest . Didalam class ini pun kita harus meng-set BASE URL API kita
