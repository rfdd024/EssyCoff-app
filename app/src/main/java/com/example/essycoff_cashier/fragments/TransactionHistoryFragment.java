package com.example.essycoff_cashier.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.essycoff_cashier.R;
import com.example.essycoff_cashier.adapters.TransactionAdapter;
import com.example.essycoff_cashier.database.SupabaseClient;
import com.example.essycoff_cashier.models.Transaction;
import com.example.essycoff_cashier.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment untuk menampilkan riwayat transaksi
 * Menampilkan daftar transaksi yang telah selesai
 */
public class TransactionHistoryFragment extends Fragment implements TransactionAdapter.OnTransactionClickListener {
    
    private static final String TAG = "TransactionHistoryFragment";
    
    // UI Components
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvTransactions;
    private TextView tvEmptyState, tvTotalTransactions, tvTotalAmount;
    
    // Adapter
    private TransactionAdapter transactionAdapter;
    
    // Data
    private List<Transaction> transactionList;
    
    // Utils
    private SupabaseClient supabaseClient;
    private SessionManager sessionManager;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_history, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        initData();
        setupRecyclerView();
        setupSwipeRefresh();
        loadTransactions();
    }
    
    /**
     * Inisialisasi semua view components
     */
    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        rvTransactions = view.findViewById(R.id.rv_transactions);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        tvTotalTransactions = view.findViewById(R.id.tv_total_transactions);
        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
    }
    
    /**
     * Inisialisasi data dan utils
     */
    private void initData() {
        transactionList = new ArrayList<>();
        supabaseClient = SupabaseClient.getInstance();
        sessionManager = new SessionManager(requireContext());
    }
    
    /**
     * Setup RecyclerView untuk transaksi
     */
    private void setupRecyclerView() {
        transactionAdapter = new TransactionAdapter(transactionList, this);
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTransactions.setAdapter(transactionAdapter);
    }
    
    /**
     * Setup SwipeRefreshLayout
     */
    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTransactions();
            }
        });
        
        swipeRefreshLayout.setColorSchemeResources(
            R.color.primary_color,
            R.color.accent_color
        );
    }
    
    /**
     * Load transaksi dari database
     */
    private void loadTransactions() {
        swipeRefreshLayout.setRefreshing(true);
        
        // Filter berdasarkan role user
        String filter;
        if (sessionManager.isCurrentUserManager()) {
            // Manager bisa lihat semua transaksi
            filter = "order=created_at.desc&limit=50";
        } else {
            // Staff hanya bisa lihat transaksi sendiri
            filter = "user_id=eq." + sessionManager.getCurrentUserId() + "&order=created_at.desc&limit=50";
        }
        
        supabaseClient.select("transactions", filter, Transaction.class,
            new SupabaseClient.SupabaseCallback<List<Transaction>>() {
                @Override
                public void onSuccess(List<Transaction> transactions) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            swipeRefreshLayout.setRefreshing(false);
                            
                            transactionList.clear();
                            if (transactions != null) {
                                transactionList.addAll(transactions);
                            }
                            
                            transactionAdapter.notifyDataSetChanged();
                            updateSummary();
                            updateEmptyState();
                        });
                    }
                }
                
                @Override
                public void onError(String error) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getContext(), "Error loading transactions: " + error,
                                Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
    }
    
    /**
     * Load sample transactions untuk testing
     */
    private void loadSampleTransactions() {
        transactionList.clear();
        
        // Sample transactions
        Transaction tx1 = new Transaction("TRX-20241001-0001", 
            sessionManager.getCurrentUserId(),
            sessionManager.getCurrentUserName(),
            new ArrayList<>(), "CASH");
        tx1.setTotalAmount(45000);
        tx1.setPaidAmount(50000);
        tx1.setStatus("COMPLETED");
        tx1.setCreatedAt("2024-10-01T10:30:00");
        
        Transaction tx2 = new Transaction("TRX-20241001-0002",
            sessionManager.getCurrentUserId(),
            sessionManager.getCurrentUserName(),
            new ArrayList<>(), "CARD");
        tx2.setTotalAmount(32000);
        tx2.setPaidAmount(32000);
        tx2.setStatus("COMPLETED");
        tx2.setCreatedAt("2024-10-01T09:15:00");
        
        Transaction tx3 = new Transaction("TRX-20241001-0003",
            sessionManager.getCurrentUserId(),
            sessionManager.getCurrentUserName(),
            new ArrayList<>(), "DIGITAL_WALLET");
        tx3.setTotalAmount(28000);
        tx3.setPaidAmount(28000);
        tx3.setStatus("COMPLETED");
        tx3.setCreatedAt("2024-10-01T08:45:00");
        
        transactionList.add(tx1);
        transactionList.add(tx2);
        transactionList.add(tx3);
        
        transactionAdapter.notifyDataSetChanged();
    }
    
    /**
     * Update summary information
     */
    private void updateSummary() {
        int totalTransactions = transactionList.size();
        double totalAmount = 0.0;
        
        for (Transaction transaction : transactionList) {
            if ("COMPLETED".equals(transaction.getStatus())) {
                totalAmount += transaction.getTotalAmount();
            }
        }
        
        tvTotalTransactions.setText(totalTransactions + " transaksi");
        tvTotalAmount.setText(String.format("Rp %.0f", totalAmount));
    }
    
    /**
     * Update empty state visibility
     */
    private void updateEmptyState() {
        if (transactionList.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvTransactions.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvTransactions.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * Callback ketika transaksi diklik
     */
    @Override
    public void onTransactionClick(Transaction transaction) {
        // TODO: Tampilkan detail transaksi atau receipt
        showTransactionDetail(transaction);
    }
    
    /**
     * Tampilkan detail transaksi
     */
    private void showTransactionDetail(Transaction transaction) {
        String detail = "Detail Transaksi\n\n" +
                       "No. Transaksi: " + transaction.getTransactionNumber() + "\n" +
                       "Kasir: " + transaction.getUserFullName() + "\n" +
                       "Total: " + transaction.getFormattedTotalAmount() + "\n" +
                       "Bayar: " + transaction.getFormattedPaidAmount() + "\n" +
                       "Kembalian: " + transaction.getFormattedChangeAmount() + "\n" +
                       "Metode: " + getPaymentMethodName(transaction.getPaymentMethod()) + "\n" +
                       "Status: " + transaction.getStatus() + "\n" +
                       "Waktu: " + formatDateTime(transaction.getCreatedAt());
        
        Toast.makeText(getContext(), detail, Toast.LENGTH_LONG).show();
    }
    
    /**
     * Get payment method display name
     */
    private String getPaymentMethodName(String paymentMethod) {
        switch (paymentMethod) {
            case "CASH":
                return "Tunai";
            case "CARD":
                return "Kartu";
            case "DIGITAL_WALLET":
                return "Dompet Digital";
            default:
                return paymentMethod;
        }
    }
    
    /**
     * Format date time untuk display
     */
    private String formatDateTime(String dateTime) {
        if (dateTime == null) return "Unknown";
        
        // Simple format untuk demo
        // TODO: Implement proper date formatting
        return dateTime.replace("T", " ").substring(0, 16);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh data ketika fragment kembali aktif
        loadTransactions();
    }
}
