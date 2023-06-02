package com.project.pointofsaleproject.screen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.project.pointofsaleproject.R;
import com.project.pointofsaleproject.SharedPrefManager;
import com.project.pointofsaleproject.api.ApiRest;
import com.project.pointofsaleproject.api.UtilsApi;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText username, password;
    ImageView imgvLogin;
    Button btnLogin, btnRegister;
    ApiRest mApiClient;
    ProgressDialog loading;
    SharedPrefManager mSharedPrefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        isLogin();

        username = findViewById(R.id.et_username);
        password = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        imgvLogin = findViewById(R.id.imgv_login);

        //Glide.with(LoginActivity.this).load("https://kemkm.unnes.ac.id/api/public/img/logo.png").into(imgvLogin);

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
                return true;
            }
        });

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        mSharedPrefManager = new SharedPrefManager(this);
        mApiClient = UtilsApi.getAPIService();
    }

    public void isLogin(){
        if (SharedPrefManager.getInstance(this).isLogin()){
            finish();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_login){
            Intent intents = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intents);
            finish();
//            if (TextUtils.isEmpty(username.getText().toString())) {
//                username.setError("Nama pengguna harus diisi");
//                if(TextUtils.isEmpty(password.getText().toString())) {
//                    password.setError("Kata sandi harus diisi");
//                }
//            } else if(TextUtils.isEmpty(password.getText().toString())) {
//                password.setError("Kata sandi harus diisi");
//            }
//            else{
//                loading = ProgressDialog.show(this, null, "Harap Tunggu...", true, false);
//                //requestLogin(username.getText().toString(), password.getText().toString());
//
//            }
        }else  if(view.getId() == R.id.btn_register){
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    private void requestLogin(String username, String password){
        mApiClient.login(username,password)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.isSuccessful()){
                            JSONObject json = null;
                            String id = "", nama= "", nomor = "", username = "", password = "";
                            try {
                                json = new JSONObject(response.body().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            assert json != null;
                            if(json.has("error")){
                                Toast.makeText(LoginActivity.this, "Usernama Atau Password Salah", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                            }else {
                                try {
                                    id = json.getString("userid");
                                    username = json.getString("username");
                                    nomor = json.getString("nomor_hp");
                                    password = json.getString("password");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(LoginActivity.this, "BERHASIL LOGIN", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                                SharedPrefManager.getInstance(LoginActivity.this).login(
                                        id, nomor, nama, username, password
                                );
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                finish();
                                startActivity(intent);
                            }

                        }
                        else {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.i("debug", "onFailure: ERROR > " + t.toString());
                        Toast.makeText(LoginActivity.this, "Koneksi Error", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });

    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}