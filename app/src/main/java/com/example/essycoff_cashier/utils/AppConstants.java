package com.example.essycoff_cashier.utils;

/**
 * Konstanta aplikasi untuk EssyCoff POS
 * Berisi semua konstanta yang digunakan di seluruh aplikasi
 */
public final class AppConstants {
    
    // Database Tables
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String TABLE_TRANSACTION_ITEMS = "transaction_items";
    
    // Request Codes
    public static final int REQUEST_LOGIN = 1001;
    public static final int REQUEST_PRODUCT_ADD = 1002;
    public static final int REQUEST_PRODUCT_EDIT = 1003;
    
    // Fragment Tags
    public static final String FRAGMENT_POS = "pos_fragment";
    public static final String FRAGMENT_HISTORY = "history_fragment";
    public static final String FRAGMENT_PRODUCTS = "products_fragment";
    public static final String FRAGMENT_REPORTS = "reports_fragment";
    
    // Intent Extras
    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_PRODUCT_ID = "product_id";
    public static final String EXTRA_TRANSACTION_ID = "transaction_id";
    public static final String EXTRA_USER_ROLE = "user_role";
    
    // SharedPreferences Keys
    public static final String PREF_FIRST_RUN = "first_run";
    public static final String PREF_LAST_SYNC = "last_sync";
    public static final String PREF_OFFLINE_MODE = "offline_mode";
    
    // Network
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_APIKEY = "apikey";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json";
    
    // Date Formats
    public static final String DATE_FORMAT_TRANSACTION = "dd/MM/yyyy HH:mm:ss";
    public static final String DATE_FORMAT_REPORT = "dd MMM yyyy";
    public static final String DATE_FORMAT_API = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    
    // Validation Limits
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final int MIN_PRODUCT_NAME_LENGTH = 1;
    public static final int MAX_PRODUCT_NAME_LENGTH = 100;
    public static final double MIN_PRICE = 0.01;
    public static final double MAX_PRICE = 999999.99;
    public static final int MIN_STOCK = 0;
    public static final int MAX_STOCK = 9999;
    public static final int MIN_QUANTITY = 1;
    public static final int MAX_QUANTITY = 99;
    
    // UI Constants
    public static final int GRID_SPAN_COUNT = 2;
    public static final int ANIMATION_DURATION = 300;
    public static final int SPLASH_DELAY = 2000;
    public static final int TOAST_DURATION_SHORT = 2000;
    public static final int TOAST_DURATION_LONG = 3500;
    
    // Transaction Status
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_CANCELLED = "cancelled";
    public static final String STATUS_REFUNDED = "refunded";
    
    // Error Codes
    public static final int ERROR_NETWORK = 1000;
    public static final int ERROR_AUTH = 1001;
    public static final int ERROR_VALIDATION = 1002;
    public static final int ERROR_DATABASE = 1003;
    public static final int ERROR_PERMISSION = 1004;
    
    // Success Messages
    public static final String MSG_LOGIN_SUCCESS = "Login berhasil";
    public static final String MSG_LOGOUT_SUCCESS = "Logout berhasil";
    public static final String MSG_TRANSACTION_SUCCESS = "Transaksi berhasil";
    public static final String MSG_PRODUCT_ADDED = "Produk berhasil ditambahkan";
    public static final String MSG_PRODUCT_UPDATED = "Produk berhasil diperbarui";
    public static final String MSG_PRODUCT_DELETED = "Produk berhasil dihapus";
    public static final String MSG_CART_CLEARED = "Keranjang dikosongkan";
    
    // Error Messages
    public static final String MSG_ERROR_NETWORK = "Tidak dapat terhubung ke server";
    public static final String MSG_ERROR_LOGIN = "Username atau password salah";
    public static final String MSG_ERROR_SESSION_EXPIRED = "Sesi telah berakhir";
    public static final String MSG_ERROR_INSUFFICIENT_STOCK = "Stok tidak mencukupi";
    public static final String MSG_ERROR_INVALID_PAYMENT = "Jumlah pembayaran tidak valid";
    public static final String MSG_ERROR_EMPTY_CART = "Keranjang masih kosong";
    public static final String MSG_ERROR_PERMISSION_DENIED = "Akses ditolak";
    
    // Default Values
    public static final String DEFAULT_CURRENCY = "Rp";
    public static final String DEFAULT_USER_NAME = "Unknown User";
    public static final String DEFAULT_PRODUCT_IMAGE = "default_product";
    public static final double DEFAULT_TAX_RATE = 0.10;
    public static final int DEFAULT_SESSION_TIMEOUT = 8; // hours
    
    // Regex Patterns
    public static final String PATTERN_USERNAME = "^[a-zA-Z0-9_]{3,50}$";
    public static final String PATTERN_PRICE = "^[0-9]+(\\.[0-9]{1,2})?$";
    public static final String PATTERN_PHONE = "^[0-9]{10,15}$";
    public static final String PATTERN_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    // Private constructor to prevent instantiation
    private AppConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}
