package com.example.essycoff_cashier.utils;

import android.content.Context;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

/**
 * Utility class untuk mencegah NullPointerException
 * Berisi helper methods untuk null safety checks
 */
public class NullSafetyUtils {
    
    /**
     * Safe toast yang mengecek context tidak null
     */
    public static void showToast(Context context, String message) {
        if (context != null && message != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Safe toast untuk fragment
     */
    public static void showToast(Fragment fragment, String message) {
        if (fragment != null && fragment.getContext() != null && message != null) {
            Toast.makeText(fragment.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Safe string check
     */
    public static boolean isStringValid(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Safe string dengan default value
     */
    public static String safeString(String str, String defaultValue) {
        return isStringValid(str) ? str : defaultValue;
    }
    
    /**
     * Safe double parsing
     */
    public static double safeParseDouble(String str, double defaultValue) {
        if (!isStringValid(str)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Safe integer parsing
     */
    public static int safeParseInt(String str, int defaultValue) {
        if (!isStringValid(str)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Safe runOnUiThread untuk fragment
     */
    public static void runOnUiThread(Fragment fragment, Runnable runnable) {
        if (fragment != null && fragment.getActivity() != null && runnable != null) {
            fragment.getActivity().runOnUiThread(runnable);
        }
    }
    
    // Private constructor
    private NullSafetyUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
