package com.project.pointofsaleproject.screen;

import static com.project.pointofsaleproject.api.UtilsApi.BASE_URL_API_IMAGE_KATEGORI;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class KategoriEditActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CAMERA = 1;
    int GET_FROM_GALLERY = 141;
    EditText etNama;
    ImageView btnBack, imgvKategori;
    TextView tvPilihGambar;
    Button btnSimpan;
    ProgressDialog loading;
    Context context;
    Kategori kategori;
    int position;
    private Uri mPhotoUri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori_edit);

        kategori = getIntent().getParcelableExtra("data");
        position = getIntent().getIntExtra("pos", 0);

        context = KategoriEditActivity.this;
        etNama = findViewById(R.id.et_nama);
        tvPilihGambar = findViewById(R.id.tv_pilih_gambar);
        imgvKategori = findViewById(R.id.imgv_kategori);
        btnBack = findViewById(R.id.btn_back);
        btnSimpan = findViewById(R.id.btn_simpan);

        btnBack.setOnClickListener(this);
        btnSimpan.setOnClickListener(this);

        etNama.setText(kategori.getNama());
        Glide.with(context).load(BASE_URL_API_IMAGE_KATEGORI +kategori.getImage()).into(imgvKategori);

        tvPilihGambar.setOnClickListener(this);

        isPermissionGranted();
    }

    public void isPermissionGranted(){
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(KategoriEditActivity.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    CAMERA);
        }
    }

    private void saveData() {
        loading = ProgressDialog.show(context,  null, "Harap Tunggu...", true, false);
        MultipartBody.Part image = null;
        if(mPhotoUri != null){
            String filePath = getRealPathFromURIPath(mPhotoUri, KategoriEditActivity.this);
            File file = new File(filePath);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
            image = MultipartBody.Part.createFormData("image", file.getName(), mFile);
        }

        RequestBody nama = RequestBody.create(MediaType.parse("text/plain"), etNama.getText().toString());
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), kategori.getId());

        ApiRest mApiRest = UtilsApi.getAPIService();
        mApiRest.editKategori(id, nama, image).enqueue(new Callback<JsonObject>() {
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
                        Toast.makeText(context, "Berhasil Edit Data", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                imgvKategori.setImageBitmap(bitmap);
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

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_back){
            finish();
        }else if(view.getId() == R.id.btn_simpan){
            if(etNama.getText().toString().trim().length() == 0){
                showToast("Nama tidak boleh kosong");
            }else if(view.getId() == R.id.tv_pilih_gambar){
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }else {
                saveData();
            }
        }
    }

    public void showToast(String p){
        Toast.makeText(context, p, Toast.LENGTH_SHORT).show();
    }
}