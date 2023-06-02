package com.project.pointofsaleproject.adapter;

import static com.project.pointofsaleproject.api.UtilsApi.BASE_URL_API_IMAGE_PRODUK;

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

import com.bumptech.glide.Glide;
import com.project.pointofsaleproject.R;
import com.project.pointofsaleproject.model.Produk;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterCheckout extends RecyclerView.Adapter<RecyclerAdapterCheckout.ViewHolder> implements Filterable {
    public List<Produk> dataList;
    public List<Produk> dataListAll;
    private RecyclerAdapterCheckout.OnItemClickCallback onItemClickCallback;
    Context ctx;

    public void setOnclickCallback(RecyclerAdapterCheckout.OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public RecyclerAdapterCheckout(List<Produk> list, Context context) {
        this.dataList = list;
        dataListAll = new ArrayList<>(list);
        this.ctx = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item_checkout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int harga = Integer.parseInt(dataList.get(position).getHarga());
        int qty = dataList.get(position).getQty();
        int total = harga * qty;
        holder.nama.setText(dataList.get(position).getNama());
        holder.id.setText(dataList.get(position).getKode());
        holder.harga.setText("Rp. " + formatMoney(harga) +"/kg x" + qty);
        holder.total.setText("Rp. " + formatMoney(total));
        holder.kategori.setText(dataList.get(position).getKategori());
        Glide.with(ctx).load(BASE_URL_API_IMAGE_PRODUK + dataList.get(position).getImage()).into(holder.image);
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
            List<Produk> filteredList = new ArrayList<>();
            if(charSequence.length() == 0 || charSequence == null){
                filteredList.addAll(dataListAll);
            }else {
                for (Produk item: dataListAll) {
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
        ImageView image;
        TextView nama, id, kategori, harga, total;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.tv_nama);
            id = itemView.findViewById(R.id.tv_id);
            kategori = itemView.findViewById(R.id.tv_kategori);
            harga = itemView.findViewById(R.id.tv_harga);
            total = itemView.findViewById(R.id.tv_total);
            image = itemView.findViewById(R.id.imgv_img);
        }
    }

    public interface OnItemClickCallback {
        void onItemClick(Produk item, int position);
    }

    public String formatMoney(long s){
        NumberFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(s);
    }
}
