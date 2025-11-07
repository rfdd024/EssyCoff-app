package com.example.essycoff_cashier.utils;

import android.util.Log;

/**
 * Utility class untuk logging yang konsisten
 * Memudahkan debugging dan monitoring aplikasi
 */
public class LogUtils {
    
    private static final String APP_TAG = "EssyCoffPOS";
    private static final boolean DEBUG = true; // Set false untuk production
    
    /**
     * Log debug message
     */
    public static void d(String tag, String message) {
        if (DEBUG) {
            Log.d(APP_TAG + "_" + tag, message);
        }
    }
    
    /**
     * Log info message
     */
    public static void i(String tag, String message) {
        if (DEBUG) {
            Log.i(APP_TAG + "_" + tag, message);
        }
    }
    
    /**
     * Log warning message
     */
    public static void w(String tag, String message) {
        Log.w(APP_TAG + "_" + tag, message);
    }
    
    /**
     * Log error message
     */
    public static void e(String tag, String message) {
        Log.e(APP_TAG + "_" + tag, message);
    }
    
    /**
     * Log error with exception
     */
    public static void e(String tag, String message, Throwable throwable) {
        Log.e(APP_TAG + "_" + tag, message, throwable);
    }
    
    /**
     * Log method entry
     */
    public static void enter(String tag, String methodName) {
        if (DEBUG) {
            Log.d(APP_TAG + "_" + tag, "ENTER: " + methodName);
        }
    }
    
    /**
     * Log method exit
     */
    public static void exit(String tag, String methodName) {
        if (DEBUG) {
            Log.d(APP_TAG + "_" + tag, "EXIT: " + methodName);
        }
    }
    
    /**
     * Log network request
     */
    public static void network(String tag, String url, String method) {
        if (DEBUG) {
            Log.d(APP_TAG + "_NETWORK_" + tag, method + " " + url);
        }
    }
    
    /**
     * Log database operation
     */
    public static void database(String tag, String operation, String table) {
        if (DEBUG) {
            Log.d(APP_TAG + "_DB_" + tag, operation + " on " + table);
        }
    }
    
    /**
     * Log user action
     */
    public static void userAction(String tag, String action) {
        if (DEBUG) {
            Log.d(APP_TAG + "_USER_" + tag, "Action: " + action);
        }
    }
    
    // Private constructor
    private LogUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
