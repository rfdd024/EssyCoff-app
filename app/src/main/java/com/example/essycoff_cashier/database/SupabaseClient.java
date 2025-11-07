package com.example.essycoff_cashier.database;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.example.essycoff_cashier.config.AppConfig;

/**
 * Client untuk koneksi ke Supabase Database
 * Menangani semua operasi CRUD ke database
 */
public class SupabaseClient {
    private static final String TAG = "SupabaseClient";
    
    // Supabase configuration - GANTI DENGAN URL DAN KEY ANDA
    private static final String SUPABASE_URL = AppConfig.SUPABASE_URL;
    private static final String SUPABASE_KEY = AppConfig.SUPABASE_ANON_KEY;
    
    private static SupabaseClient instance;
    private OkHttpClient httpClient;
    private Gson gson;
    
    private SupabaseClient() {
        // Inisialisasi HTTP client dengan timeout
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        
        gson = new Gson();
    }
    
    // Singleton pattern untuk memastikan hanya ada satu instance
    public static synchronized SupabaseClient getInstance() {
        if (instance == null) {
            instance = new SupabaseClient();
        }
        return instance;
    }
    
    /**
     * Method untuk membuat request GET ke Supabase
     */
    public <T> void select(String table, String filter, Class<T> clazz, SupabaseCallback<List<T>> callback) {
        String url = SUPABASE_URL + "/rest/v1/" + table;
        if (filter != null && !filter.isEmpty()) {
            url += "?" + filter;
        }
        
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();
        
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error selecting from " + table, e);
                callback.onError(e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        Type listType = TypeToken.getParameterized(List.class, clazz).getType();
                        List<T> result = gson.fromJson(responseBody, listType);
                        callback.onSuccess(result);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        callback.onError("Error parsing response: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "HTTP Error: " + response.code() + " - " + response.message());
                    callback.onError("HTTP Error: " + response.code());
                }
            }
        });
    }
    
    /**
     * Method untuk membuat request POST ke Supabase (Insert)
     */
    public <T> void insert(String table, T data, SupabaseCallback<T> callback) {
        String url = SUPABASE_URL + "/rest/v1/" + table;
        String jsonData = gson.toJson(data);
        
        RequestBody body = RequestBody.create(jsonData, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .post(body)
                .build();
        
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error inserting to " + table, e);
                callback.onError(e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        T result = (T) gson.fromJson(responseBody, data.getClass());
                        callback.onSuccess(result);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing insert response", e);
                        callback.onError("Error parsing response: " + e.getMessage());
                    }
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    Log.e(TAG, "Insert HTTP Error: " + response.code() + " - " + errorBody);
                    callback.onError("Insert failed: " + response.code());
                }
            }
        });
    }
    
    /**
     * Method untuk membuat request PATCH ke Supabase (Update)
     */
    public <T> void update(String table, String filter, T data, SupabaseCallback<List<T>> callback) {
        String url = SUPABASE_URL + "/rest/v1/" + table + "?" + filter;
        String jsonData = gson.toJson(data);
        
        RequestBody body = RequestBody.create(jsonData, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .patch(body)
                .build();
        
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error updating " + table, e);
                callback.onError(e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        Type listType = TypeToken.getParameterized(List.class, data.getClass()).getType();
                        List<T> result = gson.fromJson(responseBody, listType);
                        callback.onSuccess(result);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing update response", e);
                        callback.onError("Error parsing response: " + e.getMessage());
                    }
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    Log.e(TAG, "Update HTTP Error: " + response.code() + " - " + errorBody);
                    callback.onError("Update failed: " + response.code());
                }
            }
        });
    }
    
    /**
     * Method untuk membuat request DELETE ke Supabase
     */
    public void delete(String table, String filter, SupabaseCallback<Void> callback) {
        String url = SUPABASE_URL + "/rest/v1/" + table + "?" + filter;
        
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .delete()
                .build();
        
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error deleting from " + table, e);
                callback.onError(e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    Log.e(TAG, "Delete HTTP Error: " + response.code() + " - " + errorBody);
                    callback.onError("Delete failed: " + response.code());
                }
            }
        });
    }
    
    /**
     * Interface untuk callback hasil operasi database
     */
    public interface SupabaseCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
