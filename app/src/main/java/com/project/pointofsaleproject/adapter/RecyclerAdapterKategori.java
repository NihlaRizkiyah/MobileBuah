package com.project.pointofsaleproject.adapter;

import static com.project.pointofsaleproject.api.UtilsApi.BASE_URL_API_IMAGE_KATEGORI;

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
import com.project.pointofsaleproject.model.Kategori;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterKategori extends RecyclerView.Adapter<RecyclerAdapterKategori.ViewHolder> implements Filterable {
    public List<Kategori> dataList;
    public List<Kategori> dataListAll;
    private RecyclerAdapterKategori.OnItemClickCallback onItemClickCallback;
    Context ctx;

    public void setOnclickCallback(RecyclerAdapterKategori.OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public RecyclerAdapterKategori(List<Kategori> list, Context context) {
        this.dataList = list;
        dataListAll = new ArrayList<>(list);
        this.ctx = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item_kategori, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ArrayList<Integer> dImage = new ArrayList<>();
        dImage.add(R.drawable.img_apel);
        dImage.add(R.drawable.jeruk);
        dImage.add(R.drawable.mangga);
        holder.nama.setText(dataList.get(position).getNama());
        Glide.with(ctx).load(BASE_URL_API_IMAGE_KATEGORI + dataList.get(position).getImage()).placeholder(dImage.get(position)).into(holder.image);
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
            List<Kategori> filteredList = new ArrayList<>();
            if(charSequence.length() == 0 || charSequence == null){
                filteredList.addAll(dataListAll);
            }else {
                for (Kategori item: dataListAll) {
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
        ImageView btnDelete, image;
        TextView nama;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.tv_nama);
            image = itemView.findViewById(R.id.imgv_img);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    public interface OnItemClickCallback {
        void onItemClick(Kategori item, int position);
        void onItemDelete(Kategori item, int position);
    }
}
