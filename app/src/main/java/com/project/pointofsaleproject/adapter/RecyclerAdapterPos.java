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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.pointofsaleproject.R;
import com.project.pointofsaleproject.model.Produk;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterPos extends RecyclerView.Adapter<RecyclerAdapterPos.GridViewHolder> implements Filterable {
    private List<Produk> dataList;
    public List<Produk> dataListAll;
    private RecyclerAdapterPos.OnItemClickCallback onItemClickCallback;
    Context ctx;

    public void setOnclickCallback(RecyclerAdapterPos.OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public RecyclerAdapterPos(List<Produk> list, Context context) {
        this.dataList = list;
        dataListAll = new ArrayList<>(list);
        ctx = context;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item_pos, parent, false);
        GridViewHolder viewHolder = new GridViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(ctx).load(BASE_URL_API_IMAGE_PRODUK + dataList.get(position).getImage()).into(holder.imageView);
        int stok = Integer.parseInt(dataList.get(position).getStok());
        int onCart = dataList.get(position).getOnCart();
        int stokAkhir = stok - onCart;

        holder.nama.setText(dataList.get(position).getNama());
        holder.stok.setText(stokAkhir + " unit");
        holder.kategori.setText(dataList.get(position).getKategori());
        holder.harga.setText(dataList.get(position).getHarga() + "/kg");
        holder.nilai.setText(dataList.get(position).getOnCart() + "");
        if(onCart == 0){
            holder.btnAdd.setVisibility(View.VISIBLE);
            holder.wrapPlusMinus.setVisibility(View.GONE);
        }else {
            holder.btnAdd.setVisibility(View.GONE);
            holder.wrapPlusMinus.setVisibility(View.VISIBLE);
            if(onCart == 1){
                holder.btnMinus.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_delete));
                holder.btnMinus.setImageTintList(ctx.getResources().getColorStateList(R.color.red_500));
            }else {
                holder.btnMinus.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_remove_circle));
                holder.btnMinus.setImageTintList(ctx.getResources().getColorStateList(R.color.blue_500));
            }
        }

        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.onItemClick(dataList.get(position), position);
            }
        });
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.onItemPlus(dataList.get(position), position);
            }
        });
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.onItemMinus(dataList.get(position), position);
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
        ImageView imageView, btnPlus, btnMinus;
        TextView nama,kategori, harga, stok, nilai;
        CardView btnAdd, wrapPlusMinus;
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgv_produk);
            nama = itemView.findViewById(R.id.tv_nama);
            stok = itemView.findViewById(R.id.tv_stok);
            nama = itemView.findViewById(R.id.tv_nama);
            harga = itemView.findViewById(R.id.tv_harga);
            nilai = itemView.findViewById(R.id.tv_nilai);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            wrapPlusMinus = itemView.findViewById(R.id.wrap_plus_minus);
            kategori = itemView.findViewById(R.id.tv_kategori);
            btnAdd= itemView.findViewById(R.id.btn_add);
        }
    }

    public interface OnItemClickCallback {
        void onItemClick(Produk produk, int pos);
        void onItemPlus(Produk produk, int pos);
        void onItemMinus(Produk produk, int pos);

    }
}
