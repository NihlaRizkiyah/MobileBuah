package com.project.pointofsaleproject.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.project.pointofsaleproject.R;
import com.project.pointofsaleproject.adapter.RecyclerAdapterDialogCustomer;
import com.project.pointofsaleproject.model.Customer;

import java.util.List;

public class CustomerDialogFragment extends DialogFragment {
    private EditText mEditText;
    private TextView tvTitle;
    RecyclerView recyclerView;
    RecyclerAdapterDialogCustomer recyclerAdapter;
    Context context;
    TextView tvNoExist;
    List<Customer> data;
    private  getDataDialogListener getDialogListener;

    public CustomerDialogFragment(Context context, List<Customer> dataCustomer) {
        data = dataCustomer;
        this.context = context;
    }

    public void setOnclickCallback(getDataDialogListener getDialogListener) {
        this.getDialogListener = getDialogListener;
    }

    public interface getDataDialogListener {
        void onFinishEditDialog(Customer customer);
    }

    public static CustomerDialogFragment newInstance(String title, List<Customer> dataCustomer, Context context) {
        CustomerDialogFragment frag = new CustomerDialogFragment(context, dataCustomer);
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dialog_layout, container);
        tvNoExist = root.findViewById(R.id.tv_not_exist);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tvTitle = view.findViewById(R.id.lbl_barang);
        mEditText = view.findViewById(R.id.txt_your_name);
        recyclerView = view.findViewById(R.id.rv_data);

        String title = getArguments().getString("title", "Customer");
        getDialog().setTitle(title);
        tvTitle.setText(title);

        initData();

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void initData(){
        recyclerAdapter = new RecyclerAdapterDialogCustomer(data, context);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnclickCallback(new RecyclerAdapterDialogCustomer.OnItemClickCallback() {
            @Override
            public void onItemClick(Customer item, int position) {
                getDialogListener.onFinishEditDialog(data.get(position));
                dismiss();
            }

        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text

                recyclerAdapter.getFilter().filter(cs, new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int i) {
                        if(recyclerAdapter.getItemCount() == 0){
                            tvNoExist.setVisibility(View.VISIBLE);
                        }else {
                            tvNoExist.setVisibility(View.GONE);
                        }
                    }
                });

            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) {}
        });

    }

}
