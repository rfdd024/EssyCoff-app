package com.example.essycoff_cashier.config;

/**
 * Konfigurasi aplikasi EssyCoff POS
 * Berisi konstanta dan pengaturan global aplikasi
 */
public class AppConfig {
    
    // Supabase Configuration
    // IMPORTANT: Ganti dengan URL dan Key Supabase Anda
    public static final String SUPABASE_URL = "https://uobzpubsousfqmcaqnpn.supabase.co";
    public static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVvYnpwdWJzb3VzZnFtY2FxbnBuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTk4NDIwMTYsImV4cCI6MjA3NTQxODAxNn0.yUuOT20EjZ4vmLykmqX3-tGDsfRZpWgGylCTzCVVPfI";
    
    // API Endpoints
    public static final String USERS_ENDPOINT = "/rest/v1/users";
    public static final String PRODUCTS_ENDPOINT = "/rest/v1/products";
    public static final String TRANSACTIONS_ENDPOINT = "/rest/v1/transactions";
    public static final String TRANSACTION_ITEMS_ENDPOINT = "/rest/v1/transaction_items";
    
    // Session Configuration
    public static final long SESSION_TIMEOUT_HOURS = 8; // 8 jam
    public static final String SESSION_PREFS_NAME = "essycoff_session";
    
    // App Configuration
    public static final String APP_NAME = "EssyCoff POS";
    public static final String APP_VERSION = "1.0.0";
    public static final double TAX_RATE = 0.10; // 10% pajak
    
    // Transaction Configuration
    public static final String TRANSACTION_PREFIX = "ESC";
    public static final int TRANSACTION_NUMBER_LENGTH = 8;
    
    // Payment Methods
    public static final String PAYMENT_CASH = "Tunai";
    public static final String PAYMENT_DEBIT = "Kartu Debit";
    public static final String PAYMENT_CREDIT = "Kartu Kredit";
    public static final String PAYMENT_EWALLET = "E-Wallet";
    
    // Product Categories
    public static final String CATEGORY_COFFEE = "Kopi";
    public static final String CATEGORY_FOOD = "Makanan";
    public static final String CATEGORY_BEVERAGE = "Minuman";
    public static final String CATEGORY_OTHER = "Lainnya";
    
    // User Roles
    public static final String ROLE_STAFF = "staff";
    public static final String ROLE_MANAGER = "manager";
    
    // Default Credentials (untuk testing)
    public static final String DEFAULT_MANAGER_USERNAME = "manager";
    public static final String DEFAULT_MANAGER_PASSWORD = "manager123";
    public static final String DEFAULT_STAFF_USERNAME = "staff";
    public static final String DEFAULT_STAFF_PASSWORD = "staff123";
    
    // Network Configuration
    public static final int NETWORK_TIMEOUT_SECONDS = 30;
    public static final int MAX_RETRY_ATTEMPTS = 3;
    
    // UI Configuration
    public static final int PRODUCTS_GRID_SPAN = 2;
    public static final int ANIMATION_DURATION_MS = 300;
    
    // Validation Rules
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PRODUCT_NAME_LENGTH = 100;
    public static final double MAX_PRODUCT_PRICE = 1000000.0; // 1 juta
    public static final int MAX_CART_QUANTITY = 99;
    
    // Error Messages
    public static final String ERROR_NETWORK = "Tidak dapat terhubung ke server";
    public static final String ERROR_LOGIN_FAILED = "Username atau password salah";
    public static final String ERROR_SESSION_EXPIRED = "Sesi telah berakhir, silakan login kembali";
    public static final String ERROR_INSUFFICIENT_STOCK = "Stok produk tidak mencukupi";
    public static final String ERROR_PAYMENT_INSUFFICIENT = "Jumlah pembayaran kurang";
    
    // Success Messages
    public static final String SUCCESS_LOGIN = "Login berhasil";
    public static final String SUCCESS_LOGOUT = "Logout berhasil";
    public static final String SUCCESS_TRANSACTION = "Transaksi berhasil";
    public static final String SUCCESS_PRODUCT_ADDED = "Produk berhasil ditambahkan";
    public static final String SUCCESS_PRODUCT_UPDATED = "Produk berhasil diperbarui";
    public static final String SUCCESS_PRODUCT_DELETED = "Produk berhasil dihapus";
    
    // Date Format
    public static final String DATE_FORMAT_DISPLAY = "dd/MM/yyyy HH:mm";
    public static final String DATE_FORMAT_API = "yyyy-MM-dd'T'HH:mm:ss";
    
    // Currency Format
    public static final String CURRENCY_FORMAT = "Rp %,.0f";
    
    // Private constructor to prevent instantiation
    private AppConfig() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
