package com.example.e_shopper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {
    private List<CategoryModel> categories;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryModel category);
    }

    // Updated constructor to accept a listener
    public CategoriesAdapter(List<CategoryModel> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryModel category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateData(List<CategoryModel> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView categoryImage;
        private TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryName = itemView.findViewById(R.id.categoryName);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCategoryClick(categories.get(position));
                }
            });
        }

        public void bind(CategoryModel category) {
            categoryName.setText(category.getName());

            if (category.getIconUrl() != null && !category.getIconUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(category.getIconUrl())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder))
                        .into(categoryImage);
            } else {
                Log.e("CategoriesAdapter", "Image URL is null or empty for category: " + category.getName());
                categoryImage.setImageResource(R.drawable.placeholder);
            }
        }
    }
}


//package com.example.e_shopper;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
//
//import java.util.List;
//
//public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {
//    private List<CategoryModel> categories;
//    private OnCategoryClickListener listener;
//
//    public interface OnCategoryClickListener {
//        void onCategoryClick(CategoryModel category);
//    }
//
//    public CategoriesAdapter(List<CategoryModel> categories) {
//        this.categories = categories;
//    }
//
//    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
//        this.listener = listener; // Assign listener
//    }
//
//    @NonNull
//    @Override
//    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_category_full, parent, false);
//        return new CategoryViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
//        CategoryModel category = categories.get(position);
//        holder.bind(category); // Bind data to the view holder
//    }
//
//    @Override
//    public int getItemCount() {
//        return categories.size();
//    }
//
//    public void updateData(List<CategoryModel> newCategories) {
//        this.categories = newCategories;
//        notifyDataSetChanged();
//    }
//
//    class CategoryViewHolder extends RecyclerView.ViewHolder {
//        private ImageView categoryImage;
//        private TextView categoryName;
//
//        public CategoryViewHolder(@NonNull View itemView) {
//            super(itemView);
//            categoryImage = itemView.findViewById(R.id.categoryImage);
//            categoryName = itemView.findViewById(R.id.categoryName);
//
//            // Handle click event for the entire item
//            itemView.setOnClickListener(v -> {
//                int position = getAdapterPosition();
//                if (position != RecyclerView.NO_POSITION && listener != null) {
//                    listener.onCategoryClick(categories.get(position));
//                }
//            });
//        }
//
//        public void bind(CategoryModel category) {
//            categoryName.setText(category.getName());
//
//            if (category.getIconUrl() != null && !category.getIconUrl().isEmpty()) {
//                Glide.with(itemView.getContext())
//                        .load(category.getIconUrl())
//                        .apply(new RequestOptions()
//                                .placeholder(R.drawable.placeholder) // Placeholder image
//                                .error(R.drawable.placeholder) // Error image
//                        )
//                        .into(categoryImage);
//            } else {
//                Log.e("CategoriesAdapter", "Image URL is null or empty for category: " + category.getName());
//                categoryImage.setImageResource(R.drawable.placeholder); // Default error image
//            }
//        }
//    }
//}
