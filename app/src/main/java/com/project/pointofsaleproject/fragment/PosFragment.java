package com.project.pointofsaleproject.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
import com.google.gson.JsonParser;
import com.project.pointofsaleproject.R;
import com.project.pointofsaleproject.adapter.GridSpacingItemDecoration;
import com.project.pointofsaleproject.adapter.RecyclerAdapterPos;
import com.project.pointofsaleproject.api.ApiRest;
import com.project.pointofsaleproject.api.UtilsApi;
import com.project.pointofsaleproject.database.DatabaseHelper;
import com.project.pointofsaleproject.model.Produk;
import com.project.pointofsaleproject.screen.CartActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PosFragment extends Fragment implements View.OnClickListener {
    RecyclerView recyclerView;
    RecyclerAdapterPos recyclerAdapter;
    ProgressDialog loading;
    TextView tvCartNum;
    Context context;
    DatabaseHelper db;
    EditText etSearch;
    ImageView imgvNotFound;
    List<Produk> dataList;
    ApiRest mApiClient;
    CardView btnCart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_pos, container, false);

        context = getContext();
        db = new DatabaseHelper(context);
        etSearch = root.findViewById(R.id.et_search);
        recyclerView = root.findViewById(R.id.rv_data);
        tvCartNum = root.findViewById(R.id.tv_cart_num);
        imgvNotFound = root.findViewById(R.id.imgv_not_found);
        btnCart = root.findViewById(R.id.btn_cart);

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

        btnCart.setOnClickListener(this);

        getData();

        int spacingInPixels = 50;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, false, 0));

        generateCart();
        return root;
    }

    public void initData(){
        recyclerAdapter = new RecyclerAdapterPos(dataList, context);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerAdapter.setOnclickCallback(new RecyclerAdapterPos.OnItemClickCallback() {
            @Override
            public void onItemClick(Produk item, int pos) {
                dataList.get(pos).setOnCart(1);
                recyclerAdapter.notifyItemChanged(pos);
                db.insertCart(item.getId(), item.getNama(), item.getKategori(), item.getKode(), item.getHarga(), item.getImage(), 1);
                generateCart();
                Toast.makeText(context, "Item Berhasil Ditambahkan", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemPlus(Produk produk, int pos) {
                int nilai = dataList.get(pos).getOnCart() + 1;
                if(nilai > Integer.parseInt(dataList.get(pos).getStok())){
                    Toast.makeText(context, "Stok Tidak Tersedia", Toast.LENGTH_SHORT).show();
                }else {
                    dataList.get(pos).setOnCart(nilai);
                    recyclerAdapter.notifyItemChanged(pos);
                    db.updateCart(produk.getId(), "plus");
                    Toast.makeText(context, "Item Berhasil Ditambahkan", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onItemMinus(Produk produk, int pos) {
                int nilai = dataList.get(pos).getOnCart() - 1;
                dataList.get(pos).setOnCart(nilai);
                recyclerAdapter.notifyItemChanged(pos);
                if(nilai != 0){
                    db.updateCart(produk.getId(), "minus");
                } else {
                    db.deleteCart(produk.getId());
                }
                generateCart();
                Toast.makeText(context, "Item Berhasil Dikurangi", Toast.LENGTH_SHORT).show();
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
                                    int qty = db.getQtyCartById(object.getId());
                                    object.setOnCart(qty);
                                    dataList.add(object);
                                }
                                initData();
                                if(dataList.size() == 0){
                                    imgvNotFound.setVisibility(View.VISIBLE);
                                } else {
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

    public void generateCart(){
        long totalDiCart = db.getCountCart();
        if(totalDiCart > 0){
            tvCartNum.setText("" + totalDiCart);
            tvCartNum.setVisibility(View.VISIBLE);
        }else {
            tvCartNum.setText("" + totalDiCart);
            tvCartNum.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_cart){
            Intent intent = new Intent(context, CartActivity.class);
            startActivityForResult(intent, 10);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10 && resultCode == Activity.RESULT_OK){
            getData();
            generateCart();
        }
    }
}