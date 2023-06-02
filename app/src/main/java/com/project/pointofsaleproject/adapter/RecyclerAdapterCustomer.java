package com.project.pointofsaleproject.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.project.pointofsaleproject.R;
import com.project.pointofsaleproject.model.Customer;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterCustomer extends RecyclerView.Adapter<RecyclerAdapterCustomer.ViewHolder> implements Filterable {
    public List<Customer> dataList;
    public List<Customer> dataListAll;
    private RecyclerAdapterCustomer.OnItemClickCallback onItemClickCallback;
    Context ctx;

    public void setOnclickCallback(RecyclerAdapterCustomer.OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public RecyclerAdapterCustomer(List<Customer> list, Context context) {
        this.dataList = list;
        dataListAll = new ArrayList<>(list);
        this.ctx = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item_customer, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.nama.setText(dataList.get(position).getNama());
        holder.nomor.setText(dataList.get(position).getNomor_hp());
        holder.email.setText(dataList.get(position).getEmail());
        holder.alamat.setText(dataList.get(position).getAlamat());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.onItemClick(dataList.get(position), position);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.onItemDelete(dataList.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Customer> filteredList = new ArrayList<>();
            if(charSequence.length() == 0 || charSequence == null){
                filteredList.addAll(dataListAll);
            }else {
                for (Customer item: dataListAll) {
                    if(item.getNama().toLowerCase().contains(charSequence.toString().toLowerCase())
                        || item.getNomor_hp().toLowerCase().contains(charSequence.toString().toLowerCase())
                        || item.getEmail().toLowerCase().contains(charSequence.toString().toLowerCase())
                        || item.getAlamat().toLowerCase().contains(charSequence.toString().toLowerCase())
                    ){
                        filteredList.add(item);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values  = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataList.clear();
            dataList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView btnDelete;
        TextView nama, nomor, email, alamat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.tv_nama);
            nomor = itemView.findViewById(R.id.tv_nomor);
            email = itemView.findViewById(R.id.tv_email);
            alamat = itemView.findViewById(R.id.tv_alamat);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    public interface OnItemClickCallback {
        void onItemClick(Customer item, int position);
        void onItemDelete(Customer item, int position);
    }
}
