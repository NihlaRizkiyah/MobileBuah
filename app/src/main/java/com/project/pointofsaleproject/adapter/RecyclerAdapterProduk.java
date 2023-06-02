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

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterProduk extends RecyclerView.Adapter<RecyclerAdapterProduk.GridViewHolder> implements Filterable {
    private List<Produk> dataList;
    public List<Produk> dataListAll;
    private RecyclerAdapterProduk.OnItemClickCallback onItemClickCallback;
    Context ctx;

    public void setOnclickCallback(RecyclerAdapterProduk.OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public RecyclerAdapterProduk(List<Produk> list, Context context) {
        this.dataList = list;
        dataListAll = new ArrayList<>(list);
        ctx = context;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item_produk, parent, false);
        GridViewHolder viewHolder = new GridViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(ctx).load(BASE_URL_API_IMAGE_PRODUK + dataList.get(position).getImage()).into(holder.imageView);
        holder.nama.setText(dataList.get(position).getNama());
        holder.kategori.setText(dataList.get(position).getKategori());
        holder.harga.setText(dataList.get(position).getHarga() + "/kg");
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.onItemDelete(dataList.get(position), position);
            }
        });
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

    class GridViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, btnDelete;
        TextView nama,kategori, harga;
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgv_produk);
            nama = itemView.findViewById(R.id.tv_nama);
            harga = itemView.findViewById(R.id.tv_harga);
            kategori = itemView.findViewById(R.id.tv_kategori);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    public interface OnItemClickCallback {
        void onItemClick(Produk produk, int pos);
        void onItemDelete(Produk produk, int pos);
    }
}
