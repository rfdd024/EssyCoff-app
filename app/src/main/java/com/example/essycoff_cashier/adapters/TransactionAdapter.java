package com.example.essycoff_cashier.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essycoff_cashier.R;
import com.example.essycoff_cashier.models.Transaction;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

/**
 * Adapter untuk menampilkan daftar transaksi dalam RecyclerView
 * Digunakan di TransactionHistoryFragment
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    
    private List<Transaction> transactionList;
    private OnTransactionClickListener listener;
    
    // Interface untuk handle click event
    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }
    
    public TransactionAdapter(List<Transaction> transactionList, OnTransactionClickListener listener) {
        this.transactionList = transactionList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.bind(transaction, listener);
    }
    
    @Override
    public int getItemCount() {
        return transactionList.size();
    }
    
    /**
     * ViewHolder untuk item transaksi
     */
    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardTransaction;
        private TextView tvTransactionNumber, tvDateTime, tvCashierName, tvTotalAmount, 
                        tvPaymentMethod, tvStatus, tvItemCount;
        
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTransaction = itemView.findViewById(R.id.card_transaction);
            tvTransactionNumber = itemView.findViewById(R.id.tv_transaction_number);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            tvCashierName = itemView.findViewById(R.id.tv_cashier_name);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvItemCount = itemView.findViewById(R.id.tv_item_count);
        }
        
        public void bind(Transaction transaction, OnTransactionClickListener listener) {
            // Set transaction data
            tvTransactionNumber.setText(transaction.getTransactionNumber());
            tvDateTime.setText(formatDateTime(transaction.getCreatedAt()));
            tvCashierName.setText(transaction.getUserFullName());
            tvTotalAmount.setText(transaction.getFormattedTotalAmount());
            tvPaymentMethod.setText(getPaymentMethodName(transaction.getPaymentMethod()));
            tvStatus.setText(transaction.getStatus());
            tvItemCount.setText(transaction.getTotalItems() + " items");
            
            // Set status color
            setStatusColor(transaction.getStatus());
            
            // Set payment method background
            setPaymentMethodBackground(transaction.getPaymentMethod());
            
            // Set click listener
            cardTransaction.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTransactionClick(transaction);
                }
            });
        }
        
        /**
         * Format date time untuk display
         */
        private String formatDateTime(String dateTime) {
            if (dateTime == null) return "Unknown";
            
            try {
                // Simple format untuk demo - format: YYYY-MM-DD HH:MM
                return dateTime.replace("T", " ").substring(0, 16);
            } catch (Exception e) {
                return dateTime;
            }
        }
        
        /**
         * Get payment method display name
         */
        private String getPaymentMethodName(String paymentMethod) {
            if (paymentMethod == null) return "Unknown";
            
            switch (paymentMethod.toUpperCase()) {
                case "CASH":
                    return "Tunai";
                case "CARD":
                    return "Kartu";
                case "DIGITAL_WALLET":
                    return "Digital";
                default:
                    return paymentMethod;
            }
        }
        
        /**
         * Set status color berdasarkan status transaksi
         */
        private void setStatusColor(String status) {
            int colorRes;
            
            switch (status.toUpperCase()) {
                case "COMPLETED":
                    colorRes = R.color.success_color;
                    break;
                case "CANCELLED":
                    colorRes = R.color.error_color;
                    break;
                case "REFUNDED":
                    colorRes = R.color.warning_color;
                    break;
                default:
                    colorRes = R.color.text_secondary;
                    break;
            }
            
            tvStatus.setTextColor(itemView.getContext().getColor(colorRes));
        }
        
        /**
         * Set payment method background color
         */
        private void setPaymentMethodBackground(String paymentMethod) {
            int backgroundRes;
            int textColorRes = R.color.text_white;
            
            switch (paymentMethod.toUpperCase()) {
                case "CASH":
                    backgroundRes = R.color.success_color;
                    break;
                case "CARD":
                    backgroundRes = R.color.info_color;
                    break;
                case "DIGITAL_WALLET":
                    backgroundRes = R.color.accent_color;
                    break;
                default:
                    backgroundRes = R.color.text_secondary;
                    break;
            }
            
            tvPaymentMethod.setBackgroundResource(R.drawable.bg_payment_method);
            tvPaymentMethod.getBackground().setTint(itemView.getContext().getColor(backgroundRes));
            tvPaymentMethod.setTextColor(itemView.getContext().getColor(textColorRes));
        }
    }
}
