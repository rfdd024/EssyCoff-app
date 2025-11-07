package com.example.essycoff_cashier.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.essycoff_cashier.R;

/**
 * Dialog untuk proses pembayaran
 * Menampilkan total, pilihan metode pembayaran, dan input jumlah bayar
 */
public class PaymentDialog extends DialogFragment {
    
    private double totalAmount;
    private OnPaymentCompleteListener listener;
    
    // UI Components
    private TextView tvTotalAmount, tvTaxAmount, tvGrandTotal, tvChangeAmount;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbCash, rbCard, rbDigitalWallet;
    private EditText etPaidAmount;
    private Button btnCalculateChange, btnProcessPayment;
    
    // Interface untuk callback
    public interface OnPaymentCompleteListener {
        void onPaymentComplete(String paymentMethod, double paidAmount);
    }
    
    public PaymentDialog(double totalAmount, OnPaymentCompleteListener listener) {
        this.totalAmount = totalAmount;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_payment, null);
        
        initViews(view);
        setupData();
        setupClickListeners();
        
        builder.setView(view)
                .setTitle("Pembayaran")
                .setNegativeButton("Batal", (dialog, which) -> dismiss());
        
        return builder.create();
    }
    
    /**
     * Inisialisasi semua view components
     */
    private void initViews(View view) {
        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        tvTaxAmount = view.findViewById(R.id.tv_tax_amount);
        tvGrandTotal = view.findViewById(R.id.tv_grand_total);
        tvChangeAmount = view.findViewById(R.id.tv_change_amount);
        
        rgPaymentMethod = view.findViewById(R.id.rg_payment_method);
        rbCash = view.findViewById(R.id.rb_cash);
        rbCard = view.findViewById(R.id.rb_card);
        rbDigitalWallet = view.findViewById(R.id.rb_digital_wallet);
        
        etPaidAmount = view.findViewById(R.id.et_paid_amount);
        btnCalculateChange = view.findViewById(R.id.btn_calculate_change);
        btnProcessPayment = view.findViewById(R.id.btn_process_payment);
    }
    
    /**
     * Setup data awal
     */
    private void setupData() {
        // Hitung subtotal, pajak, dan grand total
        double subtotal = totalAmount;
        double tax = subtotal * 0.10; // PPN 10%
        double grandTotal = subtotal + tax;
        
        // Set text
        tvTotalAmount.setText(String.format("Rp %.0f", subtotal));
        tvTaxAmount.setText(String.format("Rp %.0f", tax));
        tvGrandTotal.setText(String.format("Rp %.0f", grandTotal));
        
        // Update totalAmount dengan grand total
        this.totalAmount = grandTotal;
        
        // Set default payment method
        rbCash.setChecked(true);
        
        // Set default paid amount untuk non-cash
        etPaidAmount.setText(String.format("%.0f", grandTotal));
    }
    
    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        // Payment method change listener
        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_cash) {
                etPaidAmount.setVisibility(View.VISIBLE);
                btnCalculateChange.setVisibility(View.VISIBLE);
                tvChangeAmount.setVisibility(View.VISIBLE);
            } else {
                etPaidAmount.setVisibility(View.GONE);
                btnCalculateChange.setVisibility(View.GONE);
                tvChangeAmount.setVisibility(View.GONE);
            }
        });
        
        // Calculate change button
        btnCalculateChange.setOnClickListener(v -> calculateChange());
        
        // Process payment button
        btnProcessPayment.setOnClickListener(v -> processPayment());
    }
    
    /**
     * Hitung kembalian
     */
    private void calculateChange() {
        String paidAmountStr = etPaidAmount.getText().toString().trim();
        
        if (TextUtils.isEmpty(paidAmountStr)) {
            etPaidAmount.setError("Masukkan jumlah bayar");
            return;
        }
        
        try {
            double paidAmount = Double.parseDouble(paidAmountStr);
            
            if (paidAmount < totalAmount) {
                etPaidAmount.setError("Jumlah bayar kurang");
                tvChangeAmount.setText("Kembalian: Kurang bayar");
                tvChangeAmount.setTextColor(getResources().getColor(R.color.error_color));
                btnProcessPayment.setEnabled(false);
                return;
            }
            
            double change = paidAmount - totalAmount;
            tvChangeAmount.setText(String.format("Kembalian: Rp %.0f", change));
            tvChangeAmount.setTextColor(getResources().getColor(R.color.success_color));
            btnProcessPayment.setEnabled(true);
            
        } catch (NumberFormatException e) {
            etPaidAmount.setError("Format angka tidak valid");
            btnProcessPayment.setEnabled(false);
        }
    }
    
    /**
     * Proses pembayaran
     */
    private void processPayment() {
        // Get selected payment method
        String paymentMethod = getSelectedPaymentMethod();
        
        // Get paid amount
        String paidAmountStr = etPaidAmount.getText().toString().trim();
        if (TextUtils.isEmpty(paidAmountStr)) {
            Toast.makeText(getContext(), "Masukkan jumlah bayar", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            double paidAmount = Double.parseDouble(paidAmountStr);
            
            // Validasi untuk cash
            if ("CASH".equals(paymentMethod) && paidAmount < totalAmount) {
                Toast.makeText(getContext(), "Jumlah bayar kurang", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Callback ke parent
            if (listener != null) {
                listener.onPaymentComplete(paymentMethod, paidAmount);
            }
            
            dismiss();
            
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Format angka tidak valid", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Get selected payment method
     */
    private String getSelectedPaymentMethod() {
        int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
        
        if (selectedId == R.id.rb_cash) {
            return "CASH";
        } else if (selectedId == R.id.rb_card) {
            return "CARD";
        } else if (selectedId == R.id.rb_digital_wallet) {
            return "DIGITAL_WALLET";
        }
        
        return "CASH"; // default
    }
}
