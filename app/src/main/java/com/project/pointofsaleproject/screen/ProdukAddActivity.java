package com.project.pointofsaleproject.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.project.pointofsaleproject.R;
import com.project.pointofsaleproject.api.ApiRest;
import com.project.pointofsaleproject.api.UtilsApi;
import com.project.pointofsaleproject.model.Kategori;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdukAddActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CAMERA = 1;
    int GET_FROM_GALLERY = 141;
    EditText etNama, etKode, etHarga, etStok, etKeterangan;
    Spinner spKategori;
    ImageView btnBack, imgvProduk;
    TextView tvPilihGambar;
    List<Kategori> dataKategori;
    List<String> dataIdKategori;
    Button btnSimpan;
    ProgressDialog loading;
    Context context;
    private Uri mPhotoUri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk_add);

        context = ProdukAddActivity.this;
        etNama = findViewById(R.id.et_nama);
        etHarga = findViewById(R.id.et_harga);
        etKode = findViewById(R.id.et_kode);
        etStok = findViewById(R.id.et_stok);
        etKeterangan = findViewById(R.id.et_keterangan);
        spKategori = findViewById(R.id.sp_kategori);
        tvPilihGambar = findViewById(R.id.tv_pilih_gambar);
        imgvProduk = findViewById(R.id.imgv_produk);
        btnBack = findViewById(R.id.btn_back);
        btnSimpan = findViewById(R.id.btn_simpan);

        btnBack.setOnClickListener(this);
        btnSimpan.setOnClickListener(this);

        tvPilihGambar.setOnClickListener(this);

        dataKategori = new ArrayList<>();
        dataIdKategori = new ArrayList<>();

        getData();

        isPermissionGranted();
    }

    public void isPermissionGranted(){
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ProdukAddActivity.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    CAMERA);
        }
    }

    private void saveData() {
        loading = ProgressDialog.show(context,  null, "Harap Tunggu...", true, false);

        MultipartBody.Part image = null;
        if(mPhotoUri != null){
            String filePath = getRealPathFromURIPath(mPhotoUri, ProdukAddActivity.this);
            File file = new File(filePath);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
            image = MultipartBody.Part.createFormData("image", file.getName(), mFile);
        }

        RequestBody nama = RequestBody.create(MediaType.parse("text/plain"), etNama.getText().toString());
        RequestBody harga = RequestBody.create(MediaType.parse("text/plain"), etHarga.getText().toString());
        RequestBody stok = RequestBody.create(MediaType.parse("text/plain"), etStok.getText().toString());
        RequestBody kode = RequestBody.create(MediaType.parse("text/plain"), etKode.getText().toString());
        RequestBody keterangan = RequestBody.create(MediaType.parse("text/plain"), etKeterangan.getText().toString());
        RequestBody id_kategori = RequestBody.create(MediaType.parse("text/plain"), dataIdKategori.get(spKategori.getSelectedItemPosition() - 1));

        ApiRest mApiRest = UtilsApi.getAPIService();
        mApiRest.saveProduk(nama, id_kategori, kode, harga, stok, keterangan , image).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if(response.isSuccessful()) {
                    JSONObject json = null;
                    String status = "", message = "", data = "";
                    try {
                        json = new JSONObject(response.body().toString());
                        status = json.getString("status");
                        message = json.getString("message");
                        data = json.getString("data");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (response.body() == null) {
                        Toast.makeText(context, "Gagal Upload Data", Toast.LENGTH_SHORT).show();
                    } else if (status.equals("error")) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } else if (status.equals("success")) {
                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                        Toast.makeText(context, "Berhasil Upload Data", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Silahkan Periksa Data Input", Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();
                }else {
                    loading.dismiss();
                    Toast.makeText(context, "Gagal Upload Data", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, Throwable t) {
                loading.dismiss();
                Log.d("messageerror", t.getMessage());
                Toast.makeText(context, "Koneksi Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getData(){
        loading = ProgressDialog.show(context, null, "Harap Tunggu...", true, false);
        ApiRest mApiRest = UtilsApi.getAPIService();
        mApiRest.getKategori()
                .enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                        if(response.isSuccessful()){
                            if(response.body() == null){
                                Toast.makeText(context, "Kategori Tidak Tersedia", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            } else {
                                for(int i=0;i<response.body().size();i++) {
                                    String mJsonString = response.body().get(i).toString();
                                    JsonParser parser = new JsonParser();
                                    JsonElement mJson =  parser.parse(mJsonString);
                                    Gson gson = new Gson();
                                    Kategori object = gson.fromJson(mJson, Kategori.class);
                                    dataKategori.add(object);
                                    dataIdKategori.add(object.getId());
                                }
                                initData();
                                loading.dismiss();
                            }
                        }
                        else {
                            loading.dismiss();
                            Toast.makeText(context, "Gagal Mengambil Data Kategori", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonArray> call, Throwable t) {
                        Log.i("debug", "onFailure: ERROR > " + t.toString());
                        Toast.makeText(context, "Keneksi Error", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });
    }

    private void initData() {
        String[] arrData = new String[dataKategori.size() + 1];
        arrData[0] = "Pilih Kategori";
        for(int i=0 ; i< dataKategori.size();i++){
            arrData[i+1] = dataKategori.get(i).getNama();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arrData);
        spKategori.setAdapter(adapter);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String imageName = randomAlphaNumeric(20);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, imageName , null);
        return Uri.parse(path);
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public static String randomAlphaNumeric(int count) {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmopqrstuvwxyz0123456789";
        StringBuilder builder = new StringBuilder();

        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public void showToast(String p){
        Toast.makeText(context, p, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_back){
            finish();
        }else if(view.getId() == R.id.tv_pilih_gambar){
            startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
        }
        else if(view.getId() == R.id.btn_simpan){
            if(etNama.getText().toString().trim().length() == 0){
                showToast("Nama tidak boleh kosong");
            }else if(spKategori.getSelectedItemPosition() == 0){
                showToast("Kategori Belum Dipilih");
            }else if(etKode.getText().toString().trim().length() == 0){
                showToast("Kode tidak boleh kosong");
            }else if(etHarga.getText().toString().trim().length() == 0){
                showToast("Harga tidak boleh kosong");
            }else if(etStok.getText().toString().trim().length() == 0){
                showToast("Stok tidak boleh kosong");
            }else {
                saveData();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                imgvProduk.setImageBitmap(bitmap);
                mPhotoUri = getImageUri(context, bitmap);
                tvPilihGambar.setText("Ganti Gambar");
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}