

package com.example.e_shopper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ProductModel> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(ProductModel product);
    }

    public ProductAdapter(List<ProductModel> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel product = productList.get(position);
        holder.bind(product); // Pass the ProductModel to bind
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateData(List<ProductModel> newProducts) {
        this.productList = newProducts;
        notifyDataSetChanged();
    }

    // Add this method to return the current list of products
    public List<ProductModel> getProducts() {
        return productList;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onProductClick(productList.get(position));
                }
            });
        }

        public void bind(ProductModel product) { // Accept ProductModel
            productName.setText(product.getName());
            productPrice.setText(String.format("₹%.2f", product.getPrice()));
            if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImageUrls().get(0))
                        .placeholder(R.drawable.placeholder)
                        .into(productImage);
            } else {
                Glide.with(itemView.getContext())
                        .load(R.drawable.placeholder)
                        .into(productImage);
            }
        }
    }
}
