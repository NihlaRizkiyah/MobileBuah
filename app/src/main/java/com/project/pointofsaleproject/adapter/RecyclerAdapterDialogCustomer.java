package com.project.pointofsaleproject.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.pointofsaleproject.R;
import com.project.pointofsaleproject.model.Customer;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterDialogCustomer extends RecyclerView.Adapter<RecyclerAdapterDialogCustomer.ViewHolder> implements Filterable {
    private List<Customer> dataList;
    private List<Customer> dataListAll;
    private RecyclerAdapterDialogCustomer.OnItemClickCallback onItemClickCallback;
    Context ctx;

    public void setOnclickCallback(RecyclerAdapterDialogCustomer.OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public RecyclerAdapterDialogCustomer(List<Customer> list, Context context) {
        this.dataList = new ArrayList<>(list);
        this.dataListAll = new ArrayList<>(list);
        this.ctx = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.nama.setText(dataList.get(position).getNama());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.onItemClick(dataList.get(position), position);
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
        TextView nama;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.tv_nama);
        }
    }

    public interface OnItemClickCallback {
        void onItemClick(Customer item, int position);
    }
}
