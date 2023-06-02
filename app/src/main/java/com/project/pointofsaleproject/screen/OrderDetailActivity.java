package com.project.pointofsaleproject.screen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.project.pointofsaleproject.R;
import com.project.pointofsaleproject.adapter.RecyclerAdapterCheckout;
import com.project.pointofsaleproject.api.ApiRest;
import com.project.pointofsaleproject.api.UtilsApi;
import com.project.pointofsaleproject.model.Order;
import com.project.pointofsaleproject.model.Produk;
import com.project.pointofsaleproject.pdf.PdfCreatorExampleActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    ProgressDialog loading;
    RecyclerView recyclerView;
    RecyclerAdapterCheckout recyclerAdapter;
    TextView tvKode, tvCustomer, tvTanggal, tvBayar, tvTotal;
    LinearLayout btnPrint;
    ImageView btnBack;
    List<Produk> dataList;
    ApiRest mApiClient;
    Order data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        data = getIntent().getParcelableExtra("data");

        context = OrderDetailActivity.this;
        btnBack = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.rv_data);
        tvKode= findViewById(R.id.tv_kode);
        tvTanggal = findViewById(R.id.tv_tanggal);
        tvCustomer = findViewById(R.id.tv_customer);
        tvBayar = findViewById(R.id.tv_bayar);
        tvTotal = findViewById(R.id.tv_total);
        btnPrint = findViewById(R.id.btn_print);

        dataList = new ArrayList<>();
        initData();

        getData();
        btnBack.setOnClickListener(this);
        btnPrint.setOnClickListener(this);
    }

    public void initData(){
        tvCustomer.setText(data.getCustomer());
        tvBayar.setText(data.getMetode_bayar() + "-" + data.getMetode_kirim());
        tvTanggal.setText(data.getTanggal());
        tvKode.setText(data.getKode());
        tvTotal.setText(formatMoney(Integer.parseInt(data.getHarga_total())));
    }

    public void init(){
        recyclerAdapter = new RecyclerAdapterCheckout(dataList, context);
        recyclerView.setAdapter(recyclerAdapter);
    }

    public String formatMoney(long s){
        NumberFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(s);
    }

    public void getData(){
        loading = ProgressDialog.show(context, null, "Harap Tunggu...", true, false);
        dataList.clear();
        mApiClient = UtilsApi.getAPIService();
        mApiClient.getOrderDetail(data.getId())
                .enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                        if(response.isSuccessful()){
                            if(response.body() == null){
                                Toast.makeText(context, "Data Tidak Tersedia", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            } else {
                                for(int i=0;i<response.body().size();i++) {
                                    String mJsonString = response.body().get(i).toString();
                                    JsonParser parser = new JsonParser();
                                    JsonElement mJson =  parser.parse(mJsonString);
                                    Gson gson = new Gson();
                                    Produk object = gson.fromJson(mJson, Produk.class);
                                    dataList.add(object);
                                }
                                init();
                                loading.dismiss();
                            }
                        }
                        else {
                            loading.dismiss();
                            Toast.makeText(context, "Gagal Mengambil Data", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_print){
            Intent intent = new Intent(context, PdfCreatorExampleActivity.class);
            intent.putExtra("data_order", data);
            intent.putParcelableArrayListExtra("order_detail", (ArrayList<? extends Parcelable>) dataList);
            startActivity(intent);
        }else if(view.getId() == R.id.btn_back){
            finish();
        }
    }

    public void showToast(String p){
        Toast.makeText(context, p, Toast.LENGTH_SHORT).show();
    }
}