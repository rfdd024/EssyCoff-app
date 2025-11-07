package com.example.essycoff_cashier.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essycoff_cashier.R;
import com.example.essycoff_cashier.adapters.ProductAdapter;
import com.example.essycoff_cashier.adapters.CartAdapter;
import com.example.essycoff_cashier.database.DatabaseHelper;
import com.example.essycoff_cashier.models.Product;
import com.example.essycoff_cashier.models.CartItem;
import com.example.essycoff_cashier.models.Transaction;
import com.example.essycoff_cashier.utils.SessionManager;
import com.example.essycoff_cashier.dialogs.PaymentDialog;
import com.example.essycoff_cashier.viewmodels.ProductViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment utama untuk Point of Sale (POS)
 * Menampilkan produk, keranjang belanja, dan proses checkout
 */
public class POSFragment extends Fragment implements ProductAdapter.OnProductClickListener, 
        CartAdapter.OnCartItemChangeListener, PaymentDialog.OnPaymentCompleteListener {
    
    private static final String TAG = "POSFragment";
    
    // UI Components
    private RecyclerView rvProducts, rvCart;
    private TextView tvTotalAmount, tvTotalItems, tvSubtotal, tvTax;
    private Button btnCheckout, btnClearCart;
    
    // Adapters
    private ProductAdapter productAdapter;
    private CartAdapter cartAdapter;
    
    // ViewModel
    private ProductViewModel productViewModel;
    
    // Views
    private CircularProgressIndicator progressBar;
    private View rootView;
    private com.google.android.material.textfield.TextInputLayout searchLayout;
    private EditText etSearch;
    private com.google.android.flexbox.FlexboxLayout categoryChipGroup;
    private String selectedCategoryId = null;
    
    // Data
    private List<Product> productList;
    private List<CartItem> cartItems;
    private double totalAmount = 0.0;
    
    // Utils
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_pos, container, false);
        
        // Initialize data and utils
        initData();
        
        return rootView;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        
        // Initialize views
        initViews(rootView);
        
        // Initialize search and category filter
        initSearchAndFilter();
        
        // Setup adapters
        setupAdapters();
        
        // Setup observers
        setupObservers();
        
        // Load products
        productViewModel.loadProducts();
        
        // Setup button click listeners
        setupClickListeners();
    }
    
    private void setupObservers() {
        // Observe products
        productViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                productList.clear();
                productList.addAll(products);
                productAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } else {
                showError("No products available");
                progressBar.setVisibility(View.GONE);
            }
        });
        
        // Observe loading state
        productViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });
        
        // Observe errors
        productViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showError(error);
            }
        });
    }
    
    private void showError(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }
    
    /**
     * Update cart UI based on current cart items
     */
    private void updateCartUI() {
        if (cartItems == null || cartItems.isEmpty()) {
            tvTotalItems.setText("Keranjang Kosong");
            tvTotalAmount.setText("Rp 0");
            tvSubtotal.setText("Rp 0");
            tvTax.setText("Rp 0");
            btnCheckout.setEnabled(false);
            btnClearCart.setEnabled(false);
        } else {
            double subtotal = 0;
            for (CartItem item : cartItems) {
                subtotal += item.getProduct().getPrice() * item.getQuantity();
            }
            
            double tax = subtotal * 0.11; // 11% tax
            double total = subtotal + tax;
            
            tvTotalItems.setText(String.format(Locale.getDefault(), "%d item", cartItems.size()));
            tvSubtotal.setText(String.format(Locale.getDefault(), "Rp %,.0f", subtotal));
            tvTax.setText(String.format(Locale.getDefault(), "Rp %,.0f", tax));
            tvTotalAmount.setText(String.format(Locale.getDefault(), "Rp %,.0f", total));
            
            btnCheckout.setEnabled(true);
            btnClearCart.setEnabled(true);
        }
    }
    
    /**
     * Inisialisasi semua view components
     */
    private void initViews(View view) {
        // Initialize progress bar
        progressBar = view.findViewById(R.id.progress_bar);
        
        // Initialize RecyclerViews
        rvProducts = view.findViewById(R.id.rv_products);
        rvCart = view.findViewById(R.id.rv_cart);
        
        // Initialize TextViews
        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        tvTotalItems = view.findViewById(R.id.tv_total_items);
        tvSubtotal = view.findViewById(R.id.tv_subtotal);
        tvTax = view.findViewById(R.id.tv_tax);
        
        // Initialize Buttons
        btnCheckout = view.findViewById(R.id.btn_checkout);
        btnClearCart = view.findViewById(R.id.btn_clear_cart);
    }
    
    /**
     * Inisialisasi data dan utils
     */
    private void initData() {
        productList = new ArrayList<>();
        cartItems = new ArrayList<>();
        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());
    }
    
    /**
     * Setup adapters for products and cart
     */
    private void setupAdapters() {
        // Setup products RecyclerView
        if (productList == null) {
            productList = new ArrayList<>();
        }
        productAdapter = new ProductAdapter(productList, this);
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvProducts.setAdapter(productAdapter);
        
        // Setup cart RecyclerView
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        cartAdapter = new CartAdapter(cartItems, this);
        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCart.setAdapter(cartAdapter);
        
        // Set empty view for cart
        updateCartUI();
    }
    
    /**
     * Setup click listeners untuk button
     */
    private void setupClickListeners() {
        btnCheckout.setOnClickListener(v -> {
            if (cartItems == null || cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Keranjang kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Show payment dialog
            PaymentDialog paymentDialog = new PaymentDialog(requireContext(), totalAmount, this);
            paymentDialog.show(getParentFragmentManager(), "PaymentDialog");
            showPaymentDialog();
        });
        
        btnClearCart.setOnClickListener(v -> clearCart());
    }
    
    /**
     * Load produk dari database SQLite
     */
    private void loadProducts() {
        new Thread(() -> {
            try {
                List<Product> products = databaseHelper.getAllProducts();
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        productList.clear();
                        if (products != null && !products.isEmpty()) {
                            productList.addAll(products);
                        } else {
                            loadSampleProducts();
                        }
                        productAdapter.notifyDataSetChanged();
                    });
                }
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Error loading products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            loadSampleProducts();
                        }
                    });
                }
            }
        }).start();
    }
    
    /**
     * Load sample products untuk testing
     */
    private void loadSampleProducts() {
        if (productList == null) {
            productList = new ArrayList<>();
        }
        productList.clear();
        
        // Sample data produk
        productList.add(new Product("1", "Espresso", "COFFEE", 15000, 100, true));
        productList.add(new Product("2", "Americano", "COFFEE", 18000, 100, true));
        productList.add(new Product("3", "Cappuccino", "COFFEE", 22000, 100, true));
        productList.add(new Product("4", "Latte", "COFFEE", 25000, 100, true));
        productList.add(new Product("5", "Mocha", "COFFEE", 28000, 100, true));
        productList.add(new Product("6", "Croissant", "FOOD", 18000, 20, true));
        productList.add(new Product("7", "Sandwich Club", "FOOD", 35000, 15, true));
        productList.add(new Product("8", "Donat Glazed", "FOOD", 12000, 25, true));
        
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
    }
        for (CartItem item : cartItems) {
            databaseHelper.addTransactionItem(transactionId, item);
            
            // Update product stock in Supabase
            int newStock = item.getProduct().getStock() - item.getQuantity();
            productViewModel.updateProductStock(item.getProduct().getId(), newStock, success -> {
                if (!success) {
                    Log.e(TAG, "Failed to update stock for product: " + item.getProduct().getName());
                }
            });
            }
        }
        
        // Add new product to cart
        CartItem newItem = new CartItem(product, 1);
        cartItems.add(newItem);
        cartAdapter.notifyItemInserted(cartItems.size() - 1);
        updateCartUI();
        
        // Show success message
        if (getView() != null) {
            Snackbar.make(getView(), "Added to cart: " + product.getName(), 
                Snackbar.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Menambah produk ke keranjang
     */
    private void addToCart(Product product) {
        // Cek apakah produk sudah ada di keranjang
        CartItem existingItem = findCartItem(product.getId());
        
        if (existingItem != null) {
            // Jika sudah ada, tambah quantity
            existingItem.increaseQuantity();
        } else {
            // Jika belum ada, tambah item baru
            CartItem newItem = new CartItem(product, 1);
            cartItems.add(newItem);
        }
        
        updateCartDisplay();
        Toast.makeText(getContext(), product.getName() + " ditambahkan ke keranjang", 
            Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Mencari cart item berdasarkan product ID
     */
    private CartItem findCartItem(String productId) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                return item;
            }
        }
        return null;
    }
    /**
     * Callback ketika cart item dihapus
     */
    @Override
    public void onItemRemoved(CartItem item) {
        cartItems.remove(item);
        updateCartDisplay();
        Toast.makeText(getContext(), item.getProduct().getName() + " dihapus dari keranjang", 
            Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Update tampilan keranjang dan total
     */
    private void updateCartDisplay() {
        cartAdapter.notifyDataSetChanged();
        calculateTotal();
        updateTotalDisplay();
    }
    
    /**
     * Hitung total amount dan items
     */
    private void calculateTotal() {
        double subtotal = 0.0;
        int totalItems = 0;
        
        for (CartItem item : cartItems) {
            subtotal += item.getSubtotal();
            totalItems += item.getQuantity();
        }
        
        // Hitung pajak 10%
        double tax = subtotal * 0.10;
        totalAmount = subtotal + tax;
        
        // Update tampilan subtotal dan pajak
        if (tvSubtotal != null) {
            tvSubtotal.setText(String.format("Rp %,.0f", subtotal));
        }
        if (tvTax != null) {
            tvTax.setText(String.format("Rp %,.0f", tax));
        }
    }
    
    /**
     * Update tampilan total
     */
    private void updateTotalDisplay() {
        if (tvTotalAmount != null) {
            tvTotalAmount.setText(String.format("Rp %.0f", totalAmount));
        }
        if (tvTotalItems != null) {
            tvTotalItems.setText(cartItems.size() + " items");
        }
        
        // Enable/disable checkout button
        if (btnCheckout != null) {
            btnCheckout.setEnabled(!cartItems.isEmpty());
        }
        if (btnClearCart != null) {
            btnClearCart.setEnabled(!cartItems.isEmpty());
        }
    }
    
    /**
     * Tampilkan dialog pembayaran
     */
    private void showPaymentDialog() {
        PaymentDialog dialog = new PaymentDialog(totalAmount, this);
        dialog.show(getParentFragmentManager(), "PaymentDialog");
    }
        // Generate transaction number
        String transactionNumber = generateTransactionNumber();
        
        // Create transaction object
        Transaction transaction = new Transaction(
            transactionNumber,
            sessionManager.getCurrentUserId(),
            sessionManager.getCurrentUserName(),
            new ArrayList<>(cartItems),
            paymentMethod
        );
        
        transaction.setPaidAmount(paidAmount);
        
        // Simpan ke database SQLite
        new Thread(() -> {
            try {
                boolean success = databaseHelper.insertTransaction(transaction);
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(getContext(), 
                                "Transaksi berhasil disimpan!", 
                                Toast.LENGTH_SHORT).show();
                            
                            // Clear cart setelah checkout berhasil
                            clearCart();
                            
                            // Refresh product list untuk update stock
                            loadProducts();
                        } else {
                            Toast.makeText(getContext(), 
                                "Gagal menyimpan transaksi", 
                                Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), 
                            "Error: " + e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    });
                }
            }
        }).start();
    }
    
    /**
     * Generate nomor transaksi unik
     */
    private String generateTransactionNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String dateStr = sdf.format(new Date());
        long timestamp = System.currentTimeMillis() % 10000; // 4 digit terakhir
        return "TRX-" + dateStr + "-" + String.format("%04d", timestamp);
    }
    
    /**
     * Clear semua item di keranjang
     */
    private void clearCart() {
        if (cartItems != null && !cartItems.isEmpty()) {
            int size = cartItems.size();
            cartItems.clear();
            if (cartAdapter != null) {
                cartAdapter.notifyItemRangeRemoved(0, size);
            }
            updateCartUI();
            
            // Show message
            if (getView() != null) {
                Snackbar.make(getView(), "Keranjang berhasil dikosongkan", 
                    Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
