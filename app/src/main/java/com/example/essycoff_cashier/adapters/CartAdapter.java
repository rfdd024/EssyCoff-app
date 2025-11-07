package com.example.essycoff_cashier.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essycoff_cashier.R;
import com.example.essycoff_cashier.models.CartItem;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * Adapter untuk menampilkan item-item dalam keranjang belanja
 * Digunakan di POSFragment untuk menampilkan cart items
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    
    private List<CartItem> cartItems;
    private OnCartItemChangeListener listener;
    
    // Interface untuk handle perubahan cart item
    public interface OnCartItemChangeListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onItemRemoved(CartItem item);
    }
    
    public CartAdapter(List<CartItem> cartItems, OnCartItemChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem, listener);
    }
    
    @Override
    public int getItemCount() {
        return cartItems.size();
    }
    
    /**
     * ViewHolder untuk item cart
     */
    static class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCartProductImage;
        private TextView tvItemName, tvItemPrice, tvQuantity, tvSubtotal;
        private MaterialButton btnDecrease, btnIncrease, btnRemove;
        
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCartProductImage = itemView.findViewById(R.id.iv_cart_product_image);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemPrice = itemView.findViewById(R.id.tv_item_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
        
        public void bind(CartItem cartItem, OnCartItemChangeListener listener) {
            // Set item data
            tvItemName.setText(cartItem.getProduct().getName());
            tvItemPrice.setText(cartItem.getProduct().getFormattedPrice());
            tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
            tvSubtotal.setText(cartItem.getFormattedSubtotal());
            
            // Set product image
            setProductImage(cartItem.getProduct().getName());
            
            // Set click listeners
            if (btnDecrease != null) {
                btnDecrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int newQuantity = cartItem.getQuantity() - 1;
                        if (listener != null) {
                            listener.onQuantityChanged(cartItem, newQuantity);
                        }
                    }
                });
            }
            
            if (btnIncrease != null) {
                btnIncrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int newQuantity = cartItem.getQuantity() + 1;
                        if (listener != null) {
                            listener.onQuantityChanged(cartItem, newQuantity);
                        }
                    }
                });
            }
            
            if (btnRemove != null) {
                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onItemRemoved(cartItem);
                        }
                    }
                });
            }
            
            // Disable decrease button jika quantity = 1
            btnDecrease.setEnabled(cartItem.getQuantity() > 1);
        }
        
        private void setProductImage(String productName) {
            int imageResource;
            
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
                    imageResource = R.drawable.product_espresso;
                    break;
            }
            
            ivCartProductImage.setImageResource(imageResource);
        }
    }
}
