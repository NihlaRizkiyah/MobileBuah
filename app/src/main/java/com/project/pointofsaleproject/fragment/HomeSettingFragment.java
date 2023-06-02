package com.project.pointofsaleproject.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.project.pointofsaleproject.R;
import com.project.pointofsaleproject.SharedPrefManager;
import com.project.pointofsaleproject.api.ApiRest;
import com.project.pointofsaleproject.api.UtilsApi;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeSettingFragment extends Fragment implements View.OnClickListener {
    ImageView btnEdit, btnVisibility;
    EditText etUsername, etPassword, etNomor;
    Boolean editStatus = false;
    Boolean showPassword = false;
    Button btnUpdate;
    Context context;
    ProgressDialog loading;
    String id_user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home_setting, container, false);

        context = getContext();
        btnEdit = root.findViewById(R.id.btn_edit);
        btnVisibility = root.findViewById(R.id.btn_visibility);
        etUsername = root.findViewById(R.id.et_username);
        etPassword = root.findViewById(R.id.et_password);
        etNomor = root.findViewById(R.id.et_nomor);
        btnUpdate = root.findViewById(R.id.btn_update);

        btnEdit.setOnClickListener(this);
        btnVisibility.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        id_user = SharedPrefManager.getInstance(context).getAkun(SharedPrefManager.TAG_ID);
        etUsername.setText(SharedPrefManager.getInstance(context).getAkun(SharedPrefManager.TAG_USERNAME));
        etNomor.setText(SharedPrefManager.getInstance(context).getAkun(SharedPrefManager.TAG_NOMOR));

        return root;
    }

    private void saveData() {
        loading = ProgressDialog.show(context,  null, "Harap Tunggu...", true, false);

        String username = etUsername.getText().toString();
        String nomor = etNomor.getText().toString();
        String password = etPassword.getText().toString();

        ApiRest mApiRest = UtilsApi.getAPIService();
        mApiRest.updateProfil(id_user, username, password, nomor).enqueue(new Callback<JsonObject>() {
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
                        Toast.makeText(context, "Berhasil Update Profil", Toast.LENGTH_SHORT).show();
                        SharedPrefManager.getInstance(context).setAkun(SharedPrefManager.TAG_USERNAME, username);
                        SharedPrefManager.getInstance(context).setAkun(SharedPrefManager.TAG_NOMOR, nomor);
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
        if(view.getId() == R.id.btn_edit){
            if(!editStatus){
                btnEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_clear));
                editStatus = true;
                btnUpdate.setVisibility(View.VISIBLE);
                etUsername.setEnabled(true);
                etPassword.setEnabled(true);
                etNomor.setEnabled(true);
            }else {
                btnEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit));
                editStatus = false;
                btnUpdate.setVisibility(View.GONE);
                etUsername.setEnabled(false);
                etPassword.setEnabled(false);
                etNomor.setEnabled(false);
            }
        }else if(view.getId() == R.id.btn_visibility){
            if(showPassword){
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnVisibility.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility_off));
                showPassword = false;
            }else {
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnVisibility.setImageDrawable(getResources().getDrawable(R.drawable.ic_visibility));
                showPassword = true;
            }
        }

        else if(view.getId() == R.id.btn_update){
            saveData();
        }
    }
}