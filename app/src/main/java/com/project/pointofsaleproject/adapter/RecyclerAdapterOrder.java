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
import com.project.pointofsaleproject.model.Order;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterOrder extends RecyclerView.Adapter<RecyclerAdapterOrder.ViewHolder> implements Filterable {
    public List<Order> dataList;
    public List<Order> dataListAll;
    private RecyclerAdapterOrder.OnItemClickCallback onItemClickCallback;
    Context ctx;

    public void setOnclickCallback(RecyclerAdapterOrder.OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public RecyclerAdapterOrder(List<Order> list, Context context) {
        this.dataList = new ArrayList<>(list);
        dataListAll = new ArrayList<>(list);
        this.ctx = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item_order, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int total = Integer.parseInt(dataList.get(position).getHarga_total());
        holder.nama.setText(dataList.get(position).getCustomer());
        holder.total.setText("Rp. " + formatMoney(total));
        holder.kode.setText(dataList.get(position).getKode());
        holder.bayar.setText(dataList.get(position).getMetode_bayar() + "-" + dataList.get(position).getMetode_kirim());
        holder.tanggal.setText(dataList.get(position).getJam() + " " + dataList.get(position).getTanggal());
        holder.total.setText("Rp. " + formatMoney(total));
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
            List<Order> filteredList = new ArrayList<>();
            if(charSequence.length() == 0 || charSequence == null){
                filteredList.addAll(dataListAll);
            }else {
                for (Order item: dataListAll) {
                    if(item.getKode().toLowerCase().contains(charSequence.toString().toLowerCase())
                            || item.getCustomer().toLowerCase().contains(charSequence.toString().toLowerCase())
                            || item.getTanggal().toLowerCase().contains(charSequence.toString().toLowerCase())
                            || item.getJam().toLowerCase().contains(charSequence.toString().toLowerCase())
                            || item.getMetode_bayar().toLowerCase().contains(charSequence.toString().toLowerCase())
                            || item.getMetode_kirim().toLowerCase().contains(charSequence.toString().toLowerCase())
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
        TextView nama, tanggal, kode, bayar, total;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.tv_nama);
            tanggal = itemView.findViewById(R.id.tv_tanggal);
            kode = itemView.findViewById(R.id.tv_kode);
            bayar = itemView.findViewById(R.id.tv_bayar);
            total = itemView.findViewById(R.id.tv_total);
        }
    }

    public interface OnItemClickCallback {
        void onItemClick(Order item, int position);
    }

    public String formatMoney(long s){
        NumberFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(s);
    }
}
