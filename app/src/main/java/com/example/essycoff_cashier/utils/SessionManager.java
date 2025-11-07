package com.example.essycoff_cashier.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import com.example.essycoff_cashier.models.User;
import com.google.gson.Gson;
import java.io.File;

/**
 * Class untuk mengelola session pengguna yang sedang login
 * Menggunakan EncryptedSharedPreferences untuk keamanan data
 */
public class SessionManager {
    private static final String TAG = "SessionManager";
    private static final String PREF_NAME = "EssyCoffSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_DATA = "userData";
    private static final String KEY_LOGIN_TIME = "loginTime";
    
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    
    private Context appContext;
    
    public SessionManager(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        
        // Store application context to avoid memory leaks
        this.appContext = context.getApplicationContext();
        
        try {
            // Menggunakan encrypted shared preferences untuk keamanan
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            
            sharedPreferences = EncryptedSharedPreferences.create(
                PREF_NAME,
                masterKeyAlias,
                appContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            
            editor = sharedPreferences.edit();
            gson = new Gson();
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing encrypted preferences: " + e.getMessage());
            try {
                // Fallback ke SharedPreferences biasa jika encrypted gagal
                sharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                gson = new Gson();
                Log.i(TAG, "Using standard SharedPreferences as fallback");
            } catch (Exception ex) {
                Log.e(TAG, "Failed to initialize SharedPreferences: " + ex.getMessage());
                throw new RuntimeException("Failed to initialize session manager");
            }
        }
    }
    
    /**
     * Menyimpan data login pengguna
     */
    public void createLoginSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_DATA, gson.toJson(user));
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        editor.apply();
    }
    
    /**
     * Mengecek apakah pengguna sudah login
     */
    public boolean isLoggedIn() {
        try {
            return sharedPreferences != null && sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        } catch (Exception e) {
            Log.e(TAG, "Error checking login status: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Mendapatkan data pengguna yang sedang login
     */
    public User getCurrentUser() {
        if (!isLoggedIn() || sharedPreferences == null) {
            return null;
        }
        
        try {
            String userJson = sharedPreferences.getString(KEY_USER_DATA, null);
            if (userJson == null || userJson.isEmpty()) {
                clearSession();
                return null;
            }
            
            User user = gson.fromJson(userJson, User.class);
            if (user == null || user.getId() == null) {
                clearSession();
                return null;
            }
            
            return user;
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting current user: " + e.getMessage());
            clearSession();
            return null;
        }
    }
    
    /**
     * Mendapatkan ID pengguna yang sedang login
     */
    public String getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
    
    /**
     * Mendapatkan nama lengkap pengguna yang sedang login
     */
    public String getCurrentUserName() {
        User user = getCurrentUser();
        return user != null ? user.getFullName() : "Unknown User";
    }
    
    /**
     * Mendapatkan role pengguna yang sedang login
     */
    public String getCurrentUserRole() {
        User user = getCurrentUser();
        return user != null ? user.getRole() : "STAFF";
    }
    
    /**
     * Mengecek apakah pengguna yang login adalah manager
     */
    public boolean isCurrentUserManager() {
        User user = getCurrentUser();
        return user != null && user.isManager();
    }
    
    /**
     * Mengecek apakah pengguna yang login adalah staff
     */
    public boolean isCurrentUserStaff() {
        User user = getCurrentUser();
        return user != null && user.isStaff();
    }
    
    /**
     * Mendapatkan waktu login (dalam milliseconds)
     */
    public long getLoginTime() {
        return sharedPreferences.getLong(KEY_LOGIN_TIME, 0);
    }
    
    /**
     * Mendapatkan durasi login dalam format yang mudah dibaca
     */
    public String getLoginDuration() {
        long loginTime = getLoginTime();
        if (loginTime == 0) return "Unknown";
        
        long currentTime = System.currentTimeMillis();
        long duration = currentTime - loginTime;
        
        long hours = duration / (1000 * 60 * 60);
        long minutes = (duration % (1000 * 60 * 60)) / (1000 * 60);
        
        if (hours > 0) {
            return hours + " jam " + minutes + " menit";
        } else {
            return minutes + " menit";
        }
    }
    
    /**
     * Logout pengguna dan hapus semua data session
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
    
    /**
     * Update data pengguna dalam session
     */
    public void updateUserData(User user) {
        if (isLoggedIn()) {
            editor.putString(KEY_USER_DATA, gson.toJson(user));
            editor.apply();
        }
    }
    
    /**
     * Mengecek apakah session masih valid (belum expired)
     * Session expired setelah 8 jam
     */
    public boolean isSessionValid() {
        if (!isLoggedIn()) return false;
        
        long loginTime = getLoginTime();
        long currentTime = System.currentTimeMillis();
        long sessionDuration = currentTime - loginTime;
        
        // Session valid selama 8 jam (8 * 60 * 60 * 1000 milliseconds)
        long maxSessionDuration = 8 * 60 * 60 * 1000;
        
        return sessionDuration < maxSessionDuration;
    }
    
    /**
     * Extend session time (reset login time)
     */
    public void extendSession() {
        if (isLoggedIn()) {
            editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
            editor.apply();
        }
    }
    
    /**
     * Clear session data
     */
    public void clearSession() {
        try {
            if (editor != null) {
                editor.clear().apply();
            } else if (sharedPreferences != null) {
                sharedPreferences.edit().clear().apply();
            } else if (appContext != null) {
                // Last resort: use the stored application context
                appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error clearing session: " + e.getMessage());
            try {
                // Final attempt: delete the shared preferences file directly
                if (appContext != null) {
                    File sharedPrefsDir = new File(appContext.getApplicationInfo().dataDir, "shared_prefs");
                    File sharedPrefsFile = new File(sharedPrefsDir, PREF_NAME + ".xml");
                    if (sharedPrefsFile.exists()) {
                        sharedPrefsFile.delete();
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "Failed to clear session completely: " + ex.getMessage());
            }
        }
    }
}
