package com.example.moblie_app.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moblie_app.R;
import com.example.moblie_app.ViewModel.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categoryList;
    private OnCategoryClickListener onCategoryClickListener;

    // Sửa constructor
    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener onCategoryClickListener) {
        this.categoryList = categoryList;
        this.onCategoryClickListener = onCategoryClickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.titleCat.setText(category.getName());
        holder.picCat.setImageResource(getImageResId(category.getId()));

        // Sử dụng listener khi nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            if (onCategoryClickListener != null) {
                onCategoryClickListener.onCategoryClick(category.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    private int getImageResId(int categoryId) {
        switch (categoryId) {
            case 1: return R.drawable.chao;
            case 2: return R.drawable.noi;
            case 3: return R.drawable.may_hut_bui;
            case 4: return R.drawable.bep;
            case 5: return R.drawable.may_xay;
            default: return R.drawable.intro_logo;
        }
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView picCat;
        TextView titleCat;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            picCat = itemView.findViewById(R.id.picCat);
            titleCat = itemView.findViewById(R.id.titleCat);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(int id);
    }

    public void addCategories(List<Category> categories) {
        categoryList.addAll(categories);
        notifyDataSetChanged();
    }
}
