package com.example.essycoff_cashier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.essycoff_cashier.activities.LoginActivity;
import com.example.essycoff_cashier.fragments.POSFragment;
import com.example.essycoff_cashier.fragments.TransactionHistoryFragment;
import com.example.essycoff_cashier.fragments.ProductManagementFragment;
import com.example.essycoff_cashier.fragments.ReportsFragment;
import com.example.essycoff_cashier.utils.SessionManager;
import com.example.essycoff_cashier.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * MainActivity - Activity utama aplikasi POS
 * Menampilkan berbagai fragment sesuai dengan role pengguna
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    
    // UI Components
    private BottomNavigationView bottomNavigation;
    private TextView tvUserInfo;
    
    // Utils
    private SessionManager sessionManager;
    private User currentUser;
    
    // Fragments
    private POSFragment posFragment;
    private TransactionHistoryFragment historyFragment;
    private ProductManagementFragment productFragment;
    private ReportsFragment reportsFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            // Inisialisasi session manager
            sessionManager = new SessionManager(getApplicationContext());
            
            // Cek apakah user sudah login
            if (!sessionManager.isLoggedIn() || !sessionManager.isSessionValid()) {
                navigateToLogin();
                return;
            }
            
            // Ambil data user yang sedang login
            currentUser = sessionManager.getCurrentUser();
            
            // Validasi currentUser tidak null
            if (currentUser == null) {
                Toast.makeText(this, "Sesi tidak valid, silakan login kembali", Toast.LENGTH_SHORT).show();
                navigateToLogin();
                return;
            }
            
            setContentView(R.layout.activity_main);
            
            initViews();
            setupUserInfo();
            setupBottomNavigation();
            
            // Handle deep links or saved instance state
            if (savedInstanceState == null) {
                // Set default fragment if none is shown
                if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
                    posFragment = new POSFragment();
                    getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, posFragment, "POS_FRAGMENT")
                        .commit();
                    bottomNavigation.setSelectedItemId(R.id.nav_cashier);
                }
            }
        } catch (Exception e) {
            // Tangani error yang tidak terduga
            Toast.makeText(this, "Terjadi kesalahan: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    /**
     * Inisialisasi semua view components
     */
    private void initViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        tvUserInfo = findViewById(R.id.tv_user_info);
    }
    
    /**
     * Setup bottom navigation berdasarkan role user
     */
    private void setupBottomNavigation() {
        try {
            // Pastikan currentUser tidak null
            if (currentUser == null) {
                navigateToLogin();
                return;
            }
            
            // Clear any existing menu items
            bottomNavigation.getMenu().clear();
            
            // Setup menu berdasarkan role
            if (currentUser.getRole() != null && currentUser.getRole().equalsIgnoreCase("manager")) {
                bottomNavigation.inflateMenu(R.menu.bottom_nav_manager);
            } else {
                bottomNavigation.inflateMenu(R.menu.bottom_nav_staff);
            }
            
            // Set listener untuk navigation
            bottomNavigation.setOnItemSelectedListener(item -> {
                Fragment selectedFragment = null;
                String tag = null;
                int itemId = item.getItemId();
                
                if (itemId == R.id.nav_cashier) {
                    if (posFragment == null) {
                        posFragment = new POSFragment();
                    }
                    selectedFragment = posFragment;
                    tag = "POS_FRAGMENT";
                } else if (itemId == R.id.nav_transactions) {
                    if (historyFragment == null) {
                        historyFragment = new TransactionHistoryFragment();
                    }
                    selectedFragment = historyFragment;
                    tag = "HISTORY_FRAGMENT";
                } else if (itemId == R.id.nav_products) {
                    if (productFragment == null) {
                        productFragment = new ProductManagementFragment();
                    }
                    selectedFragment = productFragment;
                    tag = "PRODUCT_FRAGMENT";
                } else if (itemId == R.id.nav_reports) {
                    if (reportsFragment == null) {
                        reportsFragment = new ReportsFragment();
                    }
                    selectedFragment = reportsFragment;
                    tag = "REPORTS_FRAGMENT";
                }
                
                if (selectedFragment != null) {
                    // Check if the fragment is already added
                    Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(tag);
                    if (currentFragment != null && currentFragment.isAdded()) {
                        // Show the existing fragment
                        getSupportFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .show(currentFragment)
                            .commit();
                    } else {
                        // Add new fragment
                        loadFragment(selectedFragment, tag);
                    }
                    
                    // Hide other fragments
                    hideOtherFragments(tag);
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting up bottom navigation: " + e.getMessage());
            Toast.makeText(this, "Gagal memuat menu navigasi", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Setup informasi user di toolbar
     */
    private void setupUserInfo() {
        if (currentUser != null) {
            String userInfo = currentUser.getFullName() + " (" + currentUser.getRole() + ")";
            tvUserInfo.setText(userInfo);
        }
    }
    
    /**
     * Method untuk load fragment
     */
    private void loadFragment(Fragment fragment, String tag) {
        try {
            if (fragment != null && !isFinishing() && !isDestroyed()) {
                getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .add(R.id.fragment_container, fragment, tag)
                    .commit();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading fragment: " + e.getMessage());
            Toast.makeText(this, "Terjadi kesalahan saat memuat halaman", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Method untuk menyembunyikan fragment yang tidak aktif
     */
    private void hideOtherFragments(String currentTag) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            
            // Daftar semua fragment yang mungkin ada
            String[] fragmentTags = {"POS_FRAGMENT", "HISTORY_FRAGMENT", "PRODUCT_FRAGMENT", "REPORTS_FRAGMENT"};
            
            for (String tag : fragmentTags) {
                if (!tag.equals(currentTag)) {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                    if (fragment != null && fragment.isAdded() && !fragment.isHidden()) {
                        transaction.hide(fragment);
                    }
                } else {
                    // Tampilkan fragment yang aktif
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                    if (fragment != null && fragment.isAdded()) {
                        transaction.show(fragment);
                    }
                }
            }
            
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            Log.e(TAG, "Error hiding fragments: " + e.getMessage());
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.menu_logout) {
            logout();
            return true;
        } else if (itemId == R.id.menu_profile) {
            showUserProfile();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Method untuk logout
     */
    private void logout() {
        sessionManager.logout();
        Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show();
        navigateToLogin();
    }
    
    /**
     * Method untuk menampilkan profil user
     */
    private void showUserProfile() {
        if (currentUser != null) {
            String profileInfo = "Nama: " + currentUser.getFullName() + "\n" +
                               "Username: " + currentUser.getUsername() + "\n" +
                               "Role: " + currentUser.getRole() + "\n" +
                               "Login Duration: " + sessionManager.getLoginDuration();
            
            Toast.makeText(this, profileInfo, Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Method untuk navigasi ke LoginActivity
     */
    private void navigateToLogin() {
        try {
            // Clear session yang mungkin bermasalah
            if (sessionManager != null) {
                sessionManager.clearSession();
            }
            
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            // Jika terjadi error, restart aplikasi
            Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            finishAffinity();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Cek session validity setiap kali activity resume
        if (!sessionManager.isSessionValid()) {
            Toast.makeText(this, "Session expired, silakan login kembali", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        } else {
            // Extend session jika masih valid
            sessionManager.extendSession();
        }
    }
    
    @Override
    public void onBackPressed() {
        // Konfirmasi sebelum keluar aplikasi
        Toast.makeText(this, "Tekan back sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();
        
        // TODO: Implementasi double back press untuk exit
        super.onBackPressed();
    }
}