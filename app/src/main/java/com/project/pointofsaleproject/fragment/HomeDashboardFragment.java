package com.project.pointofsaleproject.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
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
import com.project.pointofsaleproject.adapter.RecyclerAdapterKategori;
import com.project.pointofsaleproject.api.ApiRest;
import com.project.pointofsaleproject.api.UtilsApi;
import com.project.pointofsaleproject.model.Kategori;
import com.project.pointofsaleproject.screen.KategoriEditActivity;
import com.project.pointofsaleproject.screen.MainActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeDashboardFragment extends Fragment implements View.OnClickListener {
    RecyclerView recyclerView;
    RecyclerAdapterKategori recyclerAdapter;
    ProgressDialog loading;
    Context context;
    EditText etSearch;
    ImageView imgvNotFound;
    List<Kategori> dataList;
    ApiRest mApiClient;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home_dashboard, container, false);
        context = getContext();
        etSearch = root.findViewById(R.id.et_search);
        recyclerView = root.findViewById(R.id.rv_data);
        imgvNotFound = root.findViewById(R.id.imgv_not_found);

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

        getData();
        return root;
    }

    public void initData(){
        recyclerAdapter = new RecyclerAdapterKategori(dataList, context);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnclickCallback(new RecyclerAdapterKategori.OnItemClickCallback() {
            @Override
            public void onItemClick(Kategori item, int position) {
                Intent intent = new Intent(context, KategoriEditActivity.class);
                intent.putExtra("data", item);
                intent.putExtra("pos", position);
                startActivityForResult(intent, 11);
            }

            @Override
            public void onItemDelete(Kategori item, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Konfirmasi");
                builder.setMessage("Anda Yakin Ingin Menghapus Kategori Ini?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //deleteItem(item.getId(), position);
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

    public void getData(){
        Kategori object = new Kategori();
        object.setNama("Apel");
        dataList.add(object);
        object = new Kategori();
        object.setNama("Mangga");
        dataList.add(object);
        object = new Kategori();
        object.setNama("Jeruk");
        dataList.add(object);
//        if(dataList.size() == 0){
//            imgvNotFound.setVisibility(View.VISIBLE);
//        }else {
//            imgvNotFound.setVisibility(View.GONE);
//        }
        initData();
        //loading.dismiss();
    }

    @Override
    public void onClick(View view) {

    }
}