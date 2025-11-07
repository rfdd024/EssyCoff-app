package com.example.essycoff_cashier.models;

import java.util.List;

/**
 * Model class untuk data transaksi penjualan
 * Digunakan untuk menyimpan informasi transaksi yang telah selesai
 */
public class Transaction {
    private String id;
    private String transactionNumber; // Format: TRX-YYYYMMDD-XXXX
    private String userId; // ID kasir yang melayani
    private String userFullName; // Nama kasir
    private List<CartItem> items;
    private double subtotal;
    private double tax; // PPN 10%
    private double discount;
    private double totalAmount;
    private double paidAmount;
    private double changeAmount;
    private String paymentMethod; // "CASH", "CARD", "DIGITAL_WALLET"
    private String status; // "COMPLETED", "CANCELLED", "REFUNDED"
    private String createdAt;
    private String notes;

    // Constructor kosong
    public Transaction() {}

    // Constructor dengan parameter utama
    public Transaction(String transactionNumber, String userId, String userFullName, 
                      List<CartItem> items, String paymentMethod) {
        this.transactionNumber = transactionNumber;
        this.userId = userId;
        this.userFullName = userFullName;
        this.items = items;
        this.paymentMethod = paymentMethod;
        this.status = "COMPLETED";
        calculateTotals();
    }

    // Getter dan Setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public void setId(int id) {
        this.id = String.valueOf(id);
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        calculateTotals();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }
    
    // Alias methods for DatabaseHelper compatibility
    public double getTaxAmount() {
        return tax;
    }
    
    public void setTaxAmount(double taxAmount) {
        this.tax = taxAmount;
    }
    
    public int getCashierId() {
        return userId != null ? Integer.parseInt(userId) : 0;
    }
    
    public void setCashierId(int cashierId) {
        this.userId = String.valueOf(cashierId);
    }
    
    public double getPaymentAmount() {
        return paidAmount;
    }
    
    public void setPaymentAmount(double paymentAmount) {
        this.paidAmount = paymentAmount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
        this.changeAmount = paidAmount - totalAmount;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(double changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Method untuk menghitung total
    private void calculateTotals() {
        if (items != null && !items.isEmpty()) {
            subtotal = 0;
            for (CartItem item : items) {
                subtotal += item.getSubtotal();
            }
            
            // Hitung pajak 10%
            tax = subtotal * 0.10;
            
            // Total = subtotal + pajak - diskon
            totalAmount = subtotal + tax - discount;
        }
    }

    // Method untuk format mata uang
    public String getFormattedSubtotal() {
        return String.format("Rp %.0f", subtotal);
    }

    public String getFormattedTax() {
        return String.format("Rp %.0f", tax);
    }

    public String getFormattedDiscount() {
        return String.format("Rp %.0f", discount);
    }

    public String getFormattedTotalAmount() {
        return String.format("Rp %.0f", totalAmount);
    }

    public String getFormattedPaidAmount() {
        return String.format("Rp %.0f", paidAmount);
    }

    public String getFormattedChangeAmount() {
        return String.format("Rp %.0f", changeAmount);
    }

    // Method untuk mendapatkan jumlah total item
    public int getTotalItems() {
        if (items == null) return 0;
        int total = 0;
        for (CartItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionNumber='" + transactionNumber + '\'' +
                ", userFullName='" + userFullName + '\'' +
                ", totalAmount=" + totalAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
