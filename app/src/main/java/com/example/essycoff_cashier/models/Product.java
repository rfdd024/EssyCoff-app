package com.example.essycoff_cashier.models;

/**
 * Model class untuk data produk kopi dan makanan
 * Digunakan untuk menyimpan informasi menu coffee shop
 */
public class Product {
    private String id;
    private String name;
    private String description;
    private double price;
    private String category; // "COFFEE", "FOOD", "BEVERAGE"
    private String imageUrl;
    private int stock;
    private boolean isAvailable;
    private String createdAt;
    private String updatedAt;

    // Constructor kosong untuk JSON parsing
    public Product() {}

    // Constructor dengan parameter
    public Product(String id, String name, String description, double price, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.isAvailable = true;
        this.stock = 0;
    }

    // Constructor untuk sample data dengan stock dan availability
    public Product(String id, String name, String category, double price, int stock, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.description = "";
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.isAvailable = isAvailable;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Method untuk format harga dalam Rupiah
    public String getFormattedPrice() {
        return String.format("Rp %.0f", price);
    }

    // Method untuk mengecek apakah produk tersedia (stock > 0 dan available)
    public boolean canBeSold() {
        return isAvailable && stock > 0;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", stock=" + stock +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
