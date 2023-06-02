package com.project.pointofsaleproject.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.project.pointofsaleproject.R;
import com.project.pointofsaleproject.adapter.GridSpacingItemDecoration;
import com.project.pointofsaleproject.adapter.RecyclerAdapterProduk;
import com.project.pointofsaleproject.api.ApiRest;
import com.project.pointofsaleproject.api.UtilsApi;
import com.project.pointofsaleproject.model.Produk;
import com.project.pointofsaleproject.screen.ProdukAddActivity;
import com.project.pointofsaleproject.screen.ProdukEditActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdukFragment extends Fragment implements View.OnClickListener {
    RecyclerView recyclerView;
    RecyclerAdapterProduk recyclerAdapter;
    ProgressDialog loading;
    Context context;
    EditText etSearch;
    ImageView imgvNotFound;
    List<Produk> dataList;
    ApiRest mApiClient;
    CardView btnAdd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_produk, container, false);

        context = getContext();
        etSearch = root.findViewById(R.id.et_search);
        recyclerView = root.findViewById(R.id.rv_data);
        imgvNotFound = root.findViewById(R.id.imgv_not_found);
        btnAdd = root.findViewById(R.id.btn_add);

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    recyclerAdapter.getFilter().filter(v.getText(),new Filter.FilterListener() {
                        @Override
                        public void onFilterComplete(int i) {
                            if(recyclerAdapter.getItemCount() == 0){
                                imgvNotFound.setVisibility(View.VISIBLE);
                            }else {
                                imgvNotFound.setVisibility(View.GONE);
                            }
                        }
                    });
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        dataList = new ArrayList<>();

        btnAdd.setOnClickListener(this);

        getData();

        int spacingInPixels = 50;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, false, 0));

        return root;
    }

    public void initData(){
        recyclerAdapter = new RecyclerAdapterProduk(dataList, context);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerAdapter.setOnclickCallback(new RecyclerAdapterProduk.OnItemClickCallback() {
            @Override
            public void onItemClick(Produk item, int position) {
                Intent intent = new Intent(context, ProdukEditActivity.class);
                intent.putExtra("data", item);
                intent.putExtra("pos", position);
                startActivityForResult(intent, 11);
            }

            @Override
            public void onItemDelete(Produk item, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Konfirmasi");
                builder.setMessage("Anda Yakin Ingin Menghapus Produk Ini?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteItem(item.getId(), position);
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
        });
    }

    public void deleteItem(String id, int position){
        loading = ProgressDialog.show(context, null, "Harap Tunggu...", true, false);
        ApiRest mApiRest = UtilsApi.getAPIService();
        mApiRest.deleteProduk(id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                JSONObject json = null;
                String status = "", message = "";
                try {
                    json = new JSONObject(response.body().toString());
                    status = json.getString("status");
                    message = json.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(response.body() == null){
                    Toast.makeText(context, "Gagal Upload Data", Toast.LENGTH_SHORT).show();
                }else if(status.equals("error")){
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } else if(status.equals("success")){
                    Toast.makeText(context, "Produk Berhasil Di Hapus", Toast.LENGTH_SHORT).show();
                    dataList.remove(position);
                    recyclerAdapter.notifyItemRemoved(position);
                    recyclerAdapter.notifyItemRangeRemoved(position, recyclerAdapter.getItemCount());
                    if(recyclerAdapter.getItemCount() == 0){
                        imgvNotFound.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(context, "Silahkan Periksa Data Input", Toast.LENGTH_SHORT).show();
                }
                loading.dismiss();
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, Throwable t) {
                loading.dismiss();
                Log.d("messageerror", t.getMessage());
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getData(){
        loading = ProgressDialog.show(context, null, "Harap Tunggu...", true, false);
        dataList.clear();
        mApiClient = UtilsApi.getAPIService();
        mApiClient.getProduk()
                .enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                        if(response.isSuccessful()){
                            if(response.body() == null){
                                Toast.makeText(getContext(), "Data Tidak Tersedia", Toast.LENGTH_SHORT).show();
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
                                initData();
                                if(dataList.size() == 0){
                                    imgvNotFound.setVisibility(View.VISIBLE);
                                }else {
                                    imgvNotFound.setVisibility(View.GONE);
                                }
                                loading.dismiss();
                            }
                        }
                        else {
                            loading.dismiss();
                            Toast.makeText(getContext(), "Gagal Mengambil Data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonArray> call, Throwable t) {
                        Log.i("debug", "onFailure: ERROR > " + t.toString());
                        Toast.makeText(getContext(), "Keneksi Error", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_add){
            Intent intent = new Intent(context, ProdukAddActivity.class);
            startActivityForResult(intent, 10);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10 && resultCode == Activity.RESULT_OK){
            getData();
        }else if(requestCode == 11 && resultCode == Activity.RESULT_OK){
            getData();
        }
    }
}