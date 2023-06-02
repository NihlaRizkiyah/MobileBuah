package com.project.pointofsaleproject.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.project.pointofsaleproject.adapter.RecyclerAdapterCheckout;
import com.project.pointofsaleproject.api.ApiRest;
import com.project.pointofsaleproject.api.UtilsApi;
import com.project.pointofsaleproject.database.DatabaseHelper;
import com.project.pointofsaleproject.fragment.CustomerDialogFragment;
import com.project.pointofsaleproject.model.Customer;
import com.project.pointofsaleproject.model.Produk;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    ProgressDialog loading;
    EditText etCustomer;
    RecyclerView recyclerView;
    RecyclerAdapterCheckout recyclerAdapter;
    Spinner spBayar, spKirim;
    TextView tvTotalQty, tvTotal;
    ImageView btnBack;
    DatabaseHelper db;
    Button btnCheckout;
    List<Customer> dataListCustomer;
    List<Produk> dataList;
    ApiRest mApiClient;
    String customerId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        context = CartActivity.this;
        etCustomer = findViewById(R.id.et_customer);
        btnBack = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.rv_data);
        spBayar = findViewById(R.id.sp_bayar);
        spKirim = findViewById(R.id.sp_kirim);
        tvTotalQty = findViewById(R.id.tv_total_qty);
        tvTotal = findViewById(R.id.tv_total);
        btnCheckout = findViewById(R.id.btn_checkout);

        db = new DatabaseHelper(context);
        dataListCustomer = new ArrayList<>();
        dataList = db.getDataProduk();
        tvTotalQty.setText(dataList.size() + " item");
        tvTotal.setText(formatMoney(db.getTotal()));
        init();

        getDataCustomer();
        setOnFokus(etCustomer);
        btnCheckout.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    public void init(){
        recyclerAdapter = new RecyclerAdapterCheckout(dataList, context);
        recyclerView.setAdapter(recyclerAdapter);
    }

    public void setOnFokus(EditText editText){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showDialog("Customer");
                    editText.clearFocus();
                }

            }
        });
    }

    private void showDialog(String title) {
        FragmentManager fm = getSupportFragmentManager();
        dismissAllDialogs(fm);
        CustomerDialogFragment getCustomerDialogFragment = CustomerDialogFragment.newInstance(title, dataListCustomer, context);

        getCustomerDialogFragment.show(fm, title);
        getCustomerDialogFragment.setOnclickCallback(new CustomerDialogFragment.getDataDialogListener() {
            @Override
            public void onFinishEditDialog(Customer customer) {
                etCustomer.setText(customer.getNama());
                customerId = customer.getId();
            }
        });
    }

    public String formatMoney(long s){
        NumberFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(s);
    }

    public String formatJson(){
        Gson gson = new Gson();
        return gson.toJson(dataList);
    }

    private void saveData() {
        loading = ProgressDialog.show(context,  null, "Harap Tunggu...", true, false);

        String kirim = spKirim.getSelectedItem().toString();
        String bayar = spBayar.getSelectedItem().toString();
        String subTotal  = db.getTotal() + "";

        ApiRest mApiRest = UtilsApi.getAPIService();
        mApiRest.saveOrder(customerId, bayar, kirim, subTotal, formatJson()).enqueue(new Callback<JsonObject>() {
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
                        Toast.makeText(context, "Berhasil Ditambahkan Ke Order", Toast.LENGTH_LONG).show();
                        db.hapusDataAll();
                        setResult(Activity.RESULT_OK, null);
                        finish();
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

    public static void dismissAllDialogs(FragmentManager manager) {
        List<Fragment> fragments = manager.getFragments();

        if (fragments == null)
            return;

        for (Fragment fragment : fragments) {
            if (fragment instanceof DialogFragment) {
                DialogFragment dialogFragment = (DialogFragment) fragment;
                dialogFragment.dismissAllowingStateLoss();
            }

            FragmentManager childFragmentManager = fragment.getChildFragmentManager();
            if (childFragmentManager != null)
                dismissAllDialogs(childFragmentManager);
        }
    }

    public void getDataCustomer(){
        loading = ProgressDialog.show(context, null, "Harap Tunggu...", true, false);
        dataListCustomer.clear();
        mApiClient = UtilsApi.getAPIService();
        mApiClient.getCustomer()
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
                                    Customer object = gson.fromJson(mJson, Customer.class);
                                    dataListCustomer.add(object);
                                }
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
        if(view.getId() == R.id.btn_checkout){
            if(db.getTotal() == 0){
                Toast.makeText(context, "Tidak ada produk di keranjang", Toast.LENGTH_LONG).show();
            } else if(customerId.equalsIgnoreCase("")){
                showToast("Silahkan Pilih Customer");
            } else if(spBayar.getSelectedItemPosition() == 0){
                showToast("Silahkan Pilih Metode Pembayaran");
            } else if(spBayar.getSelectedItemPosition() == 0){
                showToast("Silahkan Pilih Metode Pengiriman");
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Konfirmasi");
                builder.setMessage("Anda Yakin Untuk Menambahkan Order?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveData();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }else if(view.getId() == R.id.btn_back){
            finish();
        }
    }

    public void showToast(String p){
        Toast.makeText(context, p, Toast.LENGTH_SHORT).show();
    }
}