package com.project.pointofsaleproject.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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

import com.project.pointofsaleproject.R;
import com.project.pointofsaleproject.adapter.RecyclerAdapterKategori;
import com.project.pointofsaleproject.adapter.RecyclerAdapterTrasaction;
import com.project.pointofsaleproject.api.ApiRest;
import com.project.pointofsaleproject.model.Transaction;
import com.project.pointofsaleproject.screen.KategoriEditActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeTransactionFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerAdapterTrasaction recyclerAdapter;
    ProgressDialog loading;
    Context context;
    EditText etSearch;
    ImageView imgvNotFound;
    List<Transaction> dataList;
    ApiRest mApiClient;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home_transaction, container, false);
        context = getContext();
        recyclerView = root.findViewById(R.id.rv_data);
        imgvNotFound = root.findViewById(R.id.imgv_not_found);
        dataList = new ArrayList<>();

        getData();
        return root;
    }

    public void initData(){
        recyclerAdapter = new RecyclerAdapterTrasaction(dataList, context);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnclickCallback(new RecyclerAdapterTrasaction.OnItemClickCallback() {
            @Override
            public void onItemClick(Transaction item, int position) {
                Intent intent = new Intent(context, KategoriEditActivity.class);
                intent.putExtra("data", item);
                intent.putExtra("pos", position);
                startActivityForResult(intent, 11);
            }
        });
    }

    public void getData(){
        Transaction object = new Transaction();
        object.setNama("Apel");
        object.setPrice("50.000");
        dataList.add(object);
        object = new Transaction();
        object.setNama("Mangga");
        object.setPrice("57.000");
        dataList.add(object);
        object = new Transaction();
        object.setNama("Jeruk");
        object.setPrice("45.000");
        dataList.add(object);
//        if(dataList.size() == 0){
//            imgvNotFound.setVisibility(View.VISIBLE);
//        }else {
//            imgvNotFound.setVisibility(View.GONE);
//        }
        initData();
        //loading.dismiss();
    }

}