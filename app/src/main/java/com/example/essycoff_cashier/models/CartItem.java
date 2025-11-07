package com.example.essycoff_cashier.models;

/**
 * Model class untuk item dalam keranjang belanja
 * Digunakan untuk menyimpan produk yang dipilih customer beserta jumlahnya
 */
public class CartItem {
    private String id;
    private Product product;
    private int quantity;
    private double subtotal;
    private String notes; // Catatan khusus untuk item (misal: tanpa gula, extra shot)

    // Constructor kosong
    public CartItem() {}

    // Constructor dengan parameter
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.subtotal = product.getPrice() * quantity;
    }

    // Constructor lengkap
    public CartItem(String id, Product product, int quantity, String notes) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.notes = notes;
        this.subtotal = product.getPrice() * quantity;
    }

    // Getter dan Setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        // Update subtotal ketika produk berubah
        this.subtotal = product.getPrice() * quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        // Update subtotal ketika quantity berubah
        if (product != null) {
            this.subtotal = product.getPrice() * quantity;
        }
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Method untuk menambah quantity
    public void increaseQuantity() {
        this.quantity++;
        updateSubtotal();
    }

    // Method untuk mengurangi quantity
    public void decreaseQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
            updateSubtotal();
        }
    }

    // Method untuk update subtotal
    private void updateSubtotal() {
        if (product != null) {
            this.subtotal = product.getPrice() * quantity;
        }
    }

    // Method untuk format subtotal dalam Rupiah
    public String getFormattedSubtotal() {
        return String.format("Rp %.0f", subtotal);
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "product=" + (product != null ? product.getName() : "null") +
                ", quantity=" + quantity +
                ", subtotal=" + subtotal +
                ", notes='" + notes + '\'' +
                '}';
    }
}
