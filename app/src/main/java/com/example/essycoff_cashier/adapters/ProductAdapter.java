package com.example.essycoff_cashier.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essycoff_cashier.R;
import com.example.essycoff_cashier.models.Product;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

/**
 * Adapter untuk menampilkan daftar produk dalam RecyclerView
 * Digunakan di POSFragment untuk menampilkan menu produk
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    
    private List<Product> productList;
    private OnProductClickListener listener;
    
    // Interface untuk handle click event
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }
    
    public ProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, listener);
    }
    
    @Override
    public int getItemCount() {
        return productList.size();
    }
    
    /**
     * ViewHolder untuk item produk
     */
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardProduct;
        private ImageView ivProductImage;
        private TextView tvProductName, tvProductPrice, tvProductCategory, tvProductStock;
        
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            cardProduct = itemView.findViewById(R.id.card_product);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductCategory = itemView.findViewById(R.id.tv_product_category);
            tvProductStock = itemView.findViewById(R.id.tv_product_stock);
        }
        
        public void bind(Product product, OnProductClickListener listener) {
            // Set product data
            tvProductName.setText(product.getName());
            tvProductPrice.setText(product.getFormattedPrice());
            // Set category with Indonesian translation
            String categoryText = product.getCategory().equalsIgnoreCase("COFFEE") ? "KOPI" : 
                                product.getCategory().equalsIgnoreCase("FOOD") ? "MAKANAN" : 
                                product.getCategory().toUpperCase();
            tvProductCategory.setText(categoryText);
            
            // Set stock info
            if (product.getStock() > 0) {
                tvProductStock.setText(String.valueOf(product.getStock()));
                tvProductStock.setTextColor(itemView.getContext().getColor(R.color.success_color));
            } else {
                tvProductStock.setText("0");
                tvProductStock.setTextColor(itemView.getContext().getColor(R.color.error_color));
            }
            
            // Set product image berdasarkan nama produk
            setProductImage(product.getName());
            
            // Set card enabled/disabled berdasarkan availability
            cardProduct.setEnabled(product.canBeSold());
            cardProduct.setAlpha(product.canBeSold() ? 1.0f : 0.5f);
            
            // Set click listener
            cardProduct.setOnClickListener(v -> {
                if (product.canBeSold() && listener != null) {
                    listener.onProductClick(product);
                }
            });
        }
        
        /**
         * Set gambar produk berdasarkan nama produk
         */
        private void setProductImage(String productName) {
            int imageResource;
            
            // Map product names to specific images
            switch (productName.toLowerCase()) {
                case "espresso":
                    imageResource = R.drawable.product_espresso;
                    break;
                case "cappuccino":
                    imageResource = R.drawable.product_cappuccino;
                    break;
                case "latte":
                case "caffe latte":
                    imageResource = R.drawable.product_latte;
                    break;
                case "americano":
                    imageResource = R.drawable.product_americano;
                    break;
                case "mocha":
                case "caffe mocha":
                    imageResource = R.drawable.product_mocha;
                    break;
                case "croissant":
                case "roti croissant":
                    imageResource = R.drawable.product_croissant;
                    break;
                default:
                    // Default based on category
                    imageResource = getDefaultImageByCategory(itemView.getContext());
                    break;
            }
            
            ivProductImage.setImageResource(imageResource);
        }
        
        private int getDefaultImageByCategory(android.content.Context context) {
            return R.drawable.product_espresso;
        }
    }
}
