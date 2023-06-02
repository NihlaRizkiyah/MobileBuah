package com.project.pointofsaleproject.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.Locale;

public class TotalTextWatcher implements TextWatcher {
    private final WeakReference<EditText> priceReference;
    private final WeakReference<EditText> priceHiddenReference;
    private final WeakReference<EditText> qtyReference;
    private final WeakReference<EditText> stokReference;

    public TotalTextWatcher( EditText price, EditText priceHidden, EditText qty, EditText stok) {
        priceReference = new WeakReference<EditText>(price);
        priceHiddenReference= new WeakReference<EditText>(priceHidden);
        qtyReference = new WeakReference<EditText>(qty);
        stokReference = new WeakReference<EditText>(stok);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        EditText price = priceReference.get();
        EditText priceHidden = priceHiddenReference.get();
        EditText qty = qtyReference.get();
        EditText stok = stokReference.get();

        try {
            if(Float.parseFloat(stok.getText().toString()) < Integer.parseInt(qty.getText().toString())){
                qty.setError("Melebihi Stok");
            }
        }catch (Exception x){}

        int q = 0; long harga = 0;
        try{
            harga = Long.parseLong(priceHidden.getText().toString());
            q = Integer.parseInt(qty.getText().toString());
        }catch (Exception x){
        }
        price.setText(String.valueOf(harga * q));
        //price.setText(formatInt(String.valueOf(harga * q)));
    }

    public String formatInt(String price){
        double priceD = Double.parseDouble(price);
        Locale indo = new Locale("id", "ID");
        NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(indo);
        return rupiahFormat.format(priceD);
    }
}