package com.example.essycoff_cashier.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.essycoff_cashier.MainActivity;
import com.example.essycoff_cashier.R;
import com.example.essycoff_cashier.database.DatabaseHelper;
import com.example.essycoff_cashier.models.User;
import com.example.essycoff_cashier.utils.SessionManager;

/**
 * Activity untuk login pengguna (Staff dan Manager)
 * Menggunakan Supabase untuk autentikasi
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    
    // UI Components
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    
    // Utils
    private SessionManager sessionManager;
    private DatabaseHelper databaseHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_login);
            
            // Inisialisasi utils dengan error handling
            try {
                sessionManager = new SessionManager(getApplicationContext());
                databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
            } catch (Exception e) {
                showErrorAndExit("Gagal menginisialisasi aplikasi. Silakan restart aplikasi.");
                return;
            }
            
            // Cek apakah user sudah login dan session masih valid
            try {
                if (sessionManager != null && sessionManager.isLoggedIn() && sessionManager.isSessionValid()) {
                    navigateToMainActivity();
                    return;
                }
            } catch (Exception e) {
                // Clear session yang mungkin corrupt
                if (sessionManager != null) {
                    sessionManager.clearSession();
                }
            }
            
            try {
                initViews();
                setupClickListeners();
            } catch (Exception e) {
                showErrorAndExit("Gagal memuat antarmuka. Silakan coba lagi.");
            }
        } catch (Exception e) {
            showErrorAndExit("Terjadi kesalahan. Silakan restart aplikasi.");
        }
    }
    
    /**
     * Inisialisasi semua view components
     */
    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    /**
     * Setup click listeners untuk semua button
     */
    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
    }
    
    /**
     * Method untuk melakukan proses login
     */
    private void attemptLogin() {
        // Reset error messages
        etUsername.setError(null);
        etPassword.setError(null);
        
        // Ambil input dari user
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        // Validasi input
        boolean isValid = true;
        
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username tidak boleh kosong");
            etUsername.requestFocus();
            isValid = false;
        }
        
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            isValid = false;
        }
        
        if (!isValid) {
            return;
        }
        
        // Tampilkan loading
        showLoading(true);
        
        // Lakukan autentikasi ke Supabase
        authenticateUser(username, password);
    }
    
    /**
     * Method untuk autentikasi user menggunakan SQLite database
     */
    private void authenticateUser(String username, String password) {
        // Tampilkan loading
        showLoading(true);
        
        // Log untuk debugging
        android.util.Log.d(TAG, "Attempting SQLite login for username: " + username);
        
        // Authenticate using SQLite database
        new Thread(() -> {
            try {
                User user = databaseHelper.authenticateUser(username, password);
                
                runOnUiThread(() -> {
                    showLoading(false);
                    
                    if (user != null) {
                        // Login berhasil
                        android.util.Log.d(TAG, "SQLite authentication successful for: " + user.getUsername());
                        
                        // Simpan session
                        sessionManager.createLoginSession(user);
                        
                        // Tampilkan pesan sukses
                        Toast.makeText(LoginActivity.this, 
                            "Selamat datang, " + user.getFullName() + "!", 
                            Toast.LENGTH_SHORT).show();
                        
                        // Navigate ke MainActivity
                        navigateToMainActivity();
                    } else {
                        // Login gagal
                        android.util.Log.d(TAG, "SQLite authentication failed for username: " + username);
                        Toast.makeText(LoginActivity.this, 
                            "Username atau password salah", 
                            Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                android.util.Log.e(TAG, "SQLite authentication error: " + e.getMessage());
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(LoginActivity.this, 
                        "Database error: " + e.getMessage(), 
                        Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
    
    /**
     * Method untuk menampilkan/menyembunyikan loading indicator
     */
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnLogin.setText("Logging in...");
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            btnLogin.setText("LOGIN");
        }
    }
    
    /**
     * Method untuk navigasi ke MainActivity
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Clear input fields ketika kembali ke activity ini
        try {
            if (etUsername != null) etUsername.setText("");
            if (etPassword != null) etPassword.setText("");
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            if (btnLogin != null) {
                btnLogin.setEnabled(true);
                btnLogin.setText("LOGIN");
            }
        } catch (Exception e) {
            // Ignore errors during cleanup
        }
    }
    
    /**
     * Menampilkan pesan error dan keluar dari aplikasi
     */
    private void showErrorAndExit(String message) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            // Beri jeda sebelum keluar
            new android.os.Handler().postDelayed(() -> {
                finishAffinity();
                System.exit(0);
            }, 2000);
        } catch (Exception e) {
            // Jika terjadi error saat menampilkan pesan, langsung keluar
            finishAffinity();
            System.exit(0);
        }
    }

    @Override
    public void onBackPressed() {
        // Keluar dari aplikasi saat tombol back ditekan di halaman login
        finishAffinity();
        System.exit(0);
    }
}
