package com.project.pointofsaleproject.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.project.pointofsaleproject.R;
import com.project.pointofsaleproject.api.ApiRest;
import com.project.pointofsaleproject.api.UtilsApi;
import com.project.pointofsaleproject.model.Customer;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;

public class CustomerEditActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etNama, etEmail, etNomor, etAlamat;
    ImageView btnBack;
    Button btnSimpan;
    ProgressDialog loading;
    Context context;
    Customer customer;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_edit);

        customer = getIntent().getParcelableExtra("data");
        position = getIntent().getIntExtra("pos", 0);

        context = CustomerEditActivity.this;
        etNama = findViewById(R.id.et_nama);
        etEmail = findViewById(R.id.et_email);
        etNomor = findViewById(R.id.et_nomor);
        etAlamat = findViewById(R.id.et_alamat);
        btnBack = findViewById(R.id.btn_back);
        btnSimpan = findViewById(R.id.btn_simpan);

        btnBack.setOnClickListener(this);
        btnSimpan.setOnClickListener(this);

        etNama.setText(customer.getNama());
        etEmail.setText(customer.getEmail());
        etNomor.setText(customer.getNomor_hp());
        etAlamat.setText(customer.getAlamat());
    }

    private void saveData() {
        loading = ProgressDialog.show(context,  null, "Harap Tunggu...", true, false);
        String nama = etNama.getText().toString();
        String nomor = etNomor.getText().toString();
        String email = etEmail.getText().toString();
        String alamat  = etAlamat.getText().toString();

        ApiRest mApiRest = UtilsApi.getAPIService();
        mApiRest.editCustomer(customer.getId(), nama, nomor, email, alamat).enqueue(new Callback<JsonObject>() {
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
                        Customer customer = new Customer();
                        customer.setId(data);
                        customer.setNama(nama);
                        customer.setNomor_hp(nomor);
                        customer.setEmail(email);
                        customer.setAlamat(alamat);
                        Intent intent = new Intent();
                        intent.putExtra("data",customer);
                        intent.putExtra("pos", position);
                        setResult(Activity.RESULT_OK, intent);
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

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_back){
            finish();
        }else if(view.getId() == R.id.btn_simpan){
            if(etNama.getText().toString().trim().length() == 0){
                showToast("Nama tidak boleh kosong");
            }else if(etNomor.getText().toString().trim().length() == 0){
                showToast("Nomor HP tidak boleh kosong");
            }else if(etEmail.getText().toString().trim().length() == 0){
                showToast("Email tidak boleh kosong");
            }else if(etAlamat.getText().toString().trim().length() == 0){
                showToast("Alamat tidak boleh kosong");
            }else {
                saveData();
            }
        }
    }

    public void showToast(String p){
        Toast.makeText(context, p, Toast.LENGTH_SHORT).show();
    }
}