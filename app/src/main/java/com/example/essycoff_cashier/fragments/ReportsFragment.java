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

import com.example.essycoff_cashier.R;
import com.example.essycoff_cashier.database.SupabaseClient;
import com.example.essycoff_cashier.models.Transaction;
import com.example.essycoff_cashier.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment untuk laporan penjualan (khusus Manager)
 * Menampilkan statistik dan analisis penjualan
 */
public class ReportsFragment extends Fragment {
    
    private static final String TAG = "ReportsFragment";
    
    // UI Components
    private TextView tvTodaySales, tvTodayTransactions, tvWeeklySales, tvWeeklyTransactions,
                    tvMonthlySales, tvMonthlyTransactions, tvTopProduct, tvTopCategory,
                    tvAverageTransaction, tvTotalRevenue;
    private MaterialCardView cardTodayReport, cardWeeklyReport, cardMonthlyReport, cardAnalytics;
    
    // Utils
    private SupabaseClient supabaseClient;
    private SessionManager sessionManager;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        initData();
        loadReportsData();
    }
    
    /**
     * Inisialisasi semua view components
     */
    private void initViews(View view) {
        // Today's Report
        cardTodayReport = view.findViewById(R.id.card_today_report);
        tvTodaySales = view.findViewById(R.id.tv_today_sales);
        tvTodayTransactions = view.findViewById(R.id.tv_today_transactions);
        
        // Weekly Report
        cardWeeklyReport = view.findViewById(R.id.card_weekly_report);
        tvWeeklySales = view.findViewById(R.id.tv_weekly_sales);
        tvWeeklyTransactions = view.findViewById(R.id.tv_weekly_transactions);
        
        // Monthly Report
        cardMonthlyReport = view.findViewById(R.id.card_monthly_report);
        tvMonthlySales = view.findViewById(R.id.tv_monthly_sales);
        tvMonthlyTransactions = view.findViewById(R.id.tv_monthly_transactions);
        
        // Analytics
        cardAnalytics = view.findViewById(R.id.card_analytics);
        tvTopProduct = view.findViewById(R.id.tv_top_product);
        tvTopCategory = view.findViewById(R.id.tv_top_category);
        tvAverageTransaction = view.findViewById(R.id.tv_average_transaction);
        tvTotalRevenue = view.findViewById(R.id.tv_total_revenue);
    }
    
    /**
     * Inisialisasi data dan utils
     */
    private void initData() {
        supabaseClient = SupabaseClient.getInstance();
        sessionManager = new SessionManager(requireContext());
    }
    
    /**
     * Load data laporan dari database
     */
    private void loadReportsData() {
        // Load today's report
        loadTodayReport();
        
        // Load weekly report
        loadWeeklyReport();
        
        // Load monthly report
        loadMonthlyReport();
        
        // Load analytics
        loadAnalytics();
    }
    
    /**
     * Load laporan hari ini
     */
    private void loadTodayReport() {
        // Load sample data for demo purposes
        updateTodayReportSample();
    }
    
    /**
     * Load laporan mingguan
     */
    private void loadWeeklyReport() {
        // TODO: Implementasi query untuk data mingguan
        updateWeeklyReportSample();
    }
    
    /**
     * Load laporan bulanan
     */
    private void loadMonthlyReport() {
        // TODO: Implementasi query untuk data bulanan
        updateMonthlyReportSample();
    }
    
    /**
     * Load data analytics
     */
    private void loadAnalytics() {
        // TODO: Implementasi query untuk analytics
        updateAnalyticsSample();
    }
    
    /**
     * Update tampilan laporan hari ini
     */
    private void updateTodayReport(List<Transaction> transactions) {
        if (transactions == null) {
            updateTodayReportSample();
            return;
        }
        
        int totalTransactions = transactions.size();
        double totalSales = 0.0;
        
        for (Transaction transaction : transactions) {
            totalSales += transaction.getTotalAmount();
        }
        
        tvTodaySales.setText(String.format("Rp %.0f", totalSales));
        tvTodayTransactions.setText(totalTransactions + " transaksi");
    }
    
    /**
     * Update sample data untuk laporan hari ini
     */
    private void updateTodayReportSample() {
        tvTodaySales.setText("Rp 1,250,000");
        tvTodayTransactions.setText("45 transaksi");
    }
    
    /**
     * Update sample data untuk laporan mingguan
     */
    private void updateWeeklyReportSample() {
        tvWeeklySales.setText("Rp 8,750,000");
        tvWeeklyTransactions.setText("315 transaksi");
    }
    
    /**
     * Update sample data untuk laporan bulanan
     */
    private void updateMonthlyReportSample() {
        tvMonthlySales.setText("Rp 35,200,000");
        tvMonthlyTransactions.setText("1,260 transaksi");
    }
    
    /**
     * Update sample data untuk analytics
     */
    private void updateAnalyticsSample() {
        tvTopProduct.setText("Cappuccino");
        tvTopCategory.setText("COFFEE");
        tvAverageTransaction.setText("Rp 27,950");
        tvTotalRevenue.setText("Rp 35,200,000");
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh data ketika fragment kembali aktif
        loadReportsData();
    }
}
