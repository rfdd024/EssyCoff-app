package com.example.essycoff_cashier.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essycoff_cashier.R;
import com.example.essycoff_cashier.models.Product;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

/**
 * Adapter untuk manajemen produk (khusus Manager)
 * Menampilkan produk dengan kontrol untuk edit, hapus, dan update stock
 */
public class ProductManagementAdapter extends RecyclerView.Adapter<ProductManagementAdapter.ProductManagementViewHolder> {
    
    private List<Product> productList;
    private OnProductManagementListener listener;
    
    // Interface untuk handle management actions
    public interface OnProductManagementListener {
        void onEditProduct(Product product);
        void onDeleteProduct(Product product);
        void onUpdateStock(Product product, int newStock);
        void onToggleAvailability(Product product);
    }
    
    public ProductManagementAdapter(List<Product> productList, OnProductManagementListener listener) {
        this.productList = productList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ProductManagementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_management, parent, false);
        return new ProductManagementViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProductManagementViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, listener);
    }
    
    @Override
    public int getItemCount() {
        return productList.size();
    }
    
    /**
     * ViewHolder untuk item product management
     */
    static class ProductManagementViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardProduct;
        private TextView tvProductName, tvProductPrice, tvProductCategory, tvProductDescription, 
                        tvCurrentStock, tvStockStatus;
        private Switch switchAvailable;
        private ImageButton btnEdit, btnDelete, btnStockMinus, btnStockPlus;
        
        public ProductManagementViewHolder(@NonNull View itemView) {
            super(itemView);
            cardProduct = itemView.findViewById(R.id.card_product);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductCategory = itemView.findViewById(R.id.tv_product_category);
            tvProductDescription = itemView.findViewById(R.id.tv_product_description);
            tvCurrentStock = itemView.findViewById(R.id.tv_current_stock);
            tvStockStatus = itemView.findViewById(R.id.tv_stock_status);
            switchAvailable = itemView.findViewById(R.id.switch_available);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnStockMinus = itemView.findViewById(R.id.btn_stock_minus);
            btnStockPlus = itemView.findViewById(R.id.btn_stock_plus);
        }
        
        public void bind(Product product, OnProductManagementListener listener) {
            // Set product data
            tvProductName.setText(product.getName());
            tvProductPrice.setText(product.getFormattedPrice());
            tvProductCategory.setText(product.getCategory());
            tvProductDescription.setText(product.getDescription());
            tvCurrentStock.setText(String.valueOf(product.getStock()));
            
            // Set stock status
            updateStockStatus(product);
            
            // Set availability switch
            switchAvailable.setChecked(product.isAvailable());
            
            // Set click listeners
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditProduct(product);
                }
            });
            
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteProduct(product);
                }
            });
            
            btnStockMinus.setOnClickListener(v -> {
                int newStock = Math.max(0, product.getStock() - 1);
                if (listener != null) {
                    listener.onUpdateStock(product, newStock);
                }
            });
            
            btnStockPlus.setOnClickListener(v -> {
                int newStock = product.getStock() + 1;
                if (listener != null) {
                    listener.onUpdateStock(product, newStock);
                }
            });
            
            switchAvailable.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null && buttonView.isPressed()) {
                    listener.onToggleAvailability(product);
                }
            });
            
            // Disable stock minus button jika stock = 0
            btnStockMinus.setEnabled(product.getStock() > 0);
        }
        
        /**
         * Update stock status display
         */
        private void updateStockStatus(Product product) {
            int stock = product.getStock();
            String status;
            int colorRes;
            
            if (stock == 0) {
                status = "Habis";
                colorRes = R.color.error_color;
            } else if (stock <= 5) {
                status = "Stock Rendah";
                colorRes = R.color.warning_color;
            } else {
                status = "Stock Aman";
                colorRes = R.color.success_color;
            }
            
            tvStockStatus.setText(status);
            tvStockStatus.setTextColor(itemView.getContext().getColor(colorRes));
        }
    }
}
