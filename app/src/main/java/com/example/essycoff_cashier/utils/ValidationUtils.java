package com.example.essycoff_cashier.utils;

import android.text.TextUtils;
import android.util.Patterns;

import com.example.essycoff_cashier.config.AppConfig;

/**
 * Utility class untuk validasi input dan data
 * Berisi method-method untuk memvalidasi berbagai jenis input
 */
public class ValidationUtils {
    
    /**
     * Validasi username
     * @param username Username yang akan divalidasi
     * @return true jika valid, false jika tidak
     */
    public static boolean isValidUsername(String username) {
        return !TextUtils.isEmpty(username) && 
               username.trim().length() >= 3 && 
               username.trim().length() <= 50;
    }
    
    /**
     * Validasi password
     * @param password Password yang akan divalidasi
     * @return true jika valid, false jika tidak
     */
    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && 
               password.length() >= AppConfig.MIN_PASSWORD_LENGTH;
    }
    
    /**
     * Validasi nama produk
     * @param productName Nama produk yang akan divalidasi
     * @return true jika valid, false jika tidak
     */
    public static boolean isValidProductName(String productName) {
        return !TextUtils.isEmpty(productName) && 
               productName.trim().length() > 0 && 
               productName.trim().length() <= AppConfig.MAX_PRODUCT_NAME_LENGTH;
    }
    
    /**
     * Validasi harga produk
     * @param price Harga produk yang akan divalidasi
     * @return true jika valid, false jika tidak
     */
    public static boolean isValidPrice(double price) {
        return price > 0 && price <= AppConfig.MAX_PRODUCT_PRICE;
    }
    
    /**
     * Validasi stok produk
     * @param stock Stok produk yang akan divalidasi
     * @return true jika valid, false jika tidak
     */
    public static boolean isValidStock(int stock) {
        return stock >= 0;
    }
    
    /**
     * Validasi quantity dalam keranjang
     * @param quantity Quantity yang akan divalidasi
     * @return true jika valid, false jika tidak
     */
    public static boolean isValidCartQuantity(int quantity) {
        return quantity > 0 && quantity <= AppConfig.MAX_CART_QUANTITY;
    }
    
    /**
     * Validasi jumlah pembayaran
     * @param paymentAmount Jumlah pembayaran
     * @param totalAmount Total yang harus dibayar
     * @return true jika pembayaran mencukupi, false jika tidak
     */
    public static boolean isValidPaymentAmount(double paymentAmount, double totalAmount) {
        return paymentAmount >= totalAmount;
    }
    
    /**
     * Validasi email (jika diperlukan untuk fitur masa depan)
     * @param email Email yang akan divalidasi
     * @return true jika valid, false jika tidak
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    /**
     * Validasi nomor telepon (jika diperlukan untuk fitur masa depan)
     * @param phoneNumber Nomor telepon yang akan divalidasi
     * @return true jika valid, false jika tidak
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        return !TextUtils.isEmpty(phoneNumber) && 
               phoneNumber.replaceAll("[^0-9]", "").length() >= 10;
    }
    
    /**
     * Sanitize input string untuk mencegah injection
     * @param input Input string yang akan disanitize
     * @return String yang sudah disanitize
     */
    public static String sanitizeInput(String input) {
        if (TextUtils.isEmpty(input)) {
            return "";
        }
        return input.trim()
                   .replaceAll("[<>\"'%;()&+]", "")
                   .replaceAll("\\s+", " ");
    }
    
    /**
     * Validasi kategori produk
     * @param category Kategori yang akan divalidasi
     * @return true jika kategori valid, false jika tidak
     */
    public static boolean isValidCategory(String category) {
        return category != null && (
            category.equals(AppConfig.CATEGORY_COFFEE) ||
            category.equals(AppConfig.CATEGORY_FOOD) ||
            category.equals(AppConfig.CATEGORY_BEVERAGE) ||
            category.equals(AppConfig.CATEGORY_OTHER)
        );
    }
    
    /**
     * Validasi payment method
     * @param paymentMethod Payment method yang akan divalidasi
     * @return true jika valid, false jika tidak
     */
    public static boolean isValidPaymentMethod(String paymentMethod) {
        return paymentMethod != null && (
            paymentMethod.equals(AppConfig.PAYMENT_CASH) ||
            paymentMethod.equals(AppConfig.PAYMENT_DEBIT) ||
            paymentMethod.equals(AppConfig.PAYMENT_CREDIT) ||
            paymentMethod.equals(AppConfig.PAYMENT_EWALLET)
        );
    }
    
    /**
     * Validasi role user
     * @param role Role yang akan divalidasi
     * @return true jika valid, false jika tidak
     */
    public static boolean isValidUserRole(String role) {
        return role != null && (
            role.equals(AppConfig.ROLE_STAFF) ||
            role.equals(AppConfig.ROLE_MANAGER)
        );
    }
    
    /**
     * Format currency untuk display
     * @param amount Jumlah yang akan diformat
     * @return String yang sudah diformat
     */
    public static String formatCurrency(double amount) {
        return String.format(AppConfig.CURRENCY_FORMAT, amount);
    }
    
    /**
     * Parse currency string menjadi double
     * @param currencyString String currency yang akan diparse
     * @return Double value atau 0 jika parsing gagal
     */
    public static double parseCurrency(String currencyString) {
        try {
            return Double.parseDouble(currencyString.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    // Private constructor untuk mencegah instantiation
    private ValidationUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
