package com.example.essycoff_cashier.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.essycoff_cashier.R;
import com.example.essycoff_cashier.adapters.ProductManagementAdapter;
import com.example.essycoff_cashier.database.SupabaseClient;
import com.example.essycoff_cashier.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment untuk manajemen produk (khusus Manager)
 * Menampilkan daftar produk dengan opsi edit, tambah, dan hapus
 */
public class ProductManagementFragment extends Fragment implements ProductManagementAdapter.OnProductManagementListener {
    
    private static final String TAG = "ProductManagementFragment";
    
    // UI Components
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvProducts;
    private FloatingActionButton fabAddProduct;
    
    // Adapter
    private ProductManagementAdapter productAdapter;
    
    // Data
    private List<Product> productList;
    
    // Utils
    private SupabaseClient supabaseClient;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_management, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        initData();
        setupRecyclerView();
        setupSwipeRefresh();
        setupFab();
        loadProducts();
    }
    
    /**
     * Inisialisasi semua view components
     */
    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        rvProducts = view.findViewById(R.id.rv_products);
        fabAddProduct = view.findViewById(R.id.fab_add_product);
    }
    
    /**
     * Inisialisasi data dan utils
     */
    private void initData() {
        productList = new ArrayList<>();
        supabaseClient = SupabaseClient.getInstance();
    }
    
    /**
     * Setup RecyclerView untuk produk
     */
    private void setupRecyclerView() {
        productAdapter = new ProductManagementAdapter(productList, this);
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProducts.setAdapter(productAdapter);
    }
    
    /**
     * Setup SwipeRefreshLayout
     */
    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadProducts();
            }
        });
        
        swipeRefreshLayout.setColorSchemeResources(
            R.color.primary_color,
            R.color.accent_color
        );
    }
    
    /**
     * Setup FloatingActionButton
     */
    private void setupFab() {
        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Tampilkan dialog tambah produk
                showAddProductDialog();
            }
        });
    }
    
    /**
     * Load produk dari database
     */
    private void loadProducts() {
        swipeRefreshLayout.setRefreshing(true);
        
        String filter = "order=category,name";
        
        supabaseClient.select("products", filter, Product.class,
            new SupabaseClient.SupabaseCallback<List<Product>>() {
                @Override
                public void onSuccess(List<Product> products) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            swipeRefreshLayout.setRefreshing(false);
                            
                            productList.clear();
                            if (products != null) {
                                productList.addAll(products);
                            }
                            
                            productAdapter.notifyDataSetChanged();
                        });
                    }
                }
                
                @Override
                public void onError(String error) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getContext(), "Error loading products: " + error,
                                Toast.LENGTH_SHORT).show();
                                
                            // Load sample data untuk testing
                            loadSampleProducts();
                        });
                    }
                }
            });
    }
    
    /**
     * Load sample products untuk testing
     */
    private void loadSampleProducts() {
        productList.clear();
        
        // Sample Coffee Products
        productList.add(createSampleProduct("1", "Espresso", "Strong coffee shot", 15000, "COFFEE", 50));
        productList.add(createSampleProduct("2", "Americano", "Espresso with hot water", 18000, "COFFEE", 45));
        productList.add(createSampleProduct("3", "Cappuccino", "Espresso with steamed milk", 22000, "COFFEE", 30));
        productList.add(createSampleProduct("4", "Latte", "Espresso with steamed milk and foam", 25000, "COFFEE", 25));
        productList.add(createSampleProduct("5", "Mocha", "Chocolate coffee", 28000, "COFFEE", 20));
        
        // Sample Food Products
        productList.add(createSampleProduct("6", "Croissant", "Buttery pastry", 12000, "FOOD", 15));
        productList.add(createSampleProduct("7", "Sandwich", "Ham and cheese sandwich", 25000, "FOOD", 10));
        productList.add(createSampleProduct("8", "Muffin", "Blueberry muffin", 15000, "FOOD", 8));
        
        // Sample Beverage Products
        productList.add(createSampleProduct("9", "Iced Tea", "Refreshing iced tea", 12000, "BEVERAGE", 35));
        productList.add(createSampleProduct("10", "Fresh Juice", "Orange juice", 18000, "BEVERAGE", 20));
        
        productAdapter.notifyDataSetChanged();
    }
    
    /**
     * Helper method untuk membuat sample product
     */
    private Product createSampleProduct(String id, String name, String description, double price, String category, int stock) {
        Product product = new Product(id, name, description, price, category);
        product.setStock(stock);
        product.setAvailable(stock > 0);
        return product;
    }
    
    /**
     * Callback ketika produk diklik untuk edit
     */
    @Override
    public void onEditProduct(Product product) {
        // TODO: Tampilkan dialog edit produk
        showEditProductDialog(product);
    }
    
    /**
     * Callback ketika produk akan dihapus
     */
    @Override
    public void onDeleteProduct(Product product) {
        // TODO: Konfirmasi dan hapus produk
        confirmDeleteProduct(product);
    }
    
    /**
     * Callback ketika stock produk diupdate
     */
    @Override
    public void onUpdateStock(Product product, int newStock) {
        updateProductStock(product, newStock);
    }
    
    /**
     * Callback ketika availability produk diubah
     */
    @Override
    public void onToggleAvailability(Product product) {
        toggleProductAvailability(product);
    }
    
    /**
     * Tampilkan dialog tambah produk
     */
    private void showAddProductDialog() {
        Toast.makeText(getContext(), "Fitur tambah produk akan segera tersedia", Toast.LENGTH_SHORT).show();
        // TODO: Implementasi dialog tambah produk
    }
    
    /**
     * Tampilkan dialog edit produk
     */
    private void showEditProductDialog(Product product) {
        String info = "Edit Produk: " + product.getName() + "\n" +
                     "Harga: " + product.getFormattedPrice() + "\n" +
                     "Stock: " + product.getStock() + "\n" +
                     "Status: " + (product.isAvailable() ? "Tersedia" : "Tidak Tersedia");
        
        Toast.makeText(getContext(), info, Toast.LENGTH_LONG).show();
        // TODO: Implementasi dialog edit produk
    }
    
    /**
     * Konfirmasi hapus produk
     */
    private void confirmDeleteProduct(Product product) {
        Toast.makeText(getContext(), "Hapus produk: " + product.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Implementasi konfirmasi hapus produk
    }
    
    /**
     * Update stock produk
     */
    private void updateProductStock(Product product, int newStock) {
        product.setStock(newStock);
        product.setAvailable(newStock > 0);
        
        // TODO: Update ke database
        Toast.makeText(getContext(), 
            "Stock " + product.getName() + " diupdate menjadi " + newStock, 
            Toast.LENGTH_SHORT).show();
        
        productAdapter.notifyDataSetChanged();
    }
    
    /**
     * Toggle availability produk
     */
    private void toggleProductAvailability(Product product) {
        product.setAvailable(!product.isAvailable());
        
        // TODO: Update ke database
        String status = product.isAvailable() ? "tersedia" : "tidak tersedia";
        Toast.makeText(getContext(), 
            product.getName() + " sekarang " + status, 
            Toast.LENGTH_SHORT).show();
        
        productAdapter.notifyDataSetChanged();
    }
}
