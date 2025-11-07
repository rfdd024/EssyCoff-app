package com.example.essycoff_cashier.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.essycoff_cashier.models.User;
import com.example.essycoff_cashier.models.Product;
import com.example.essycoff_cashier.models.Transaction;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite Database Helper untuk EssyCoff POS
 * Menggantikan Supabase dengan database lokal
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    
    // Database Info
    private static final String DATABASE_NAME = "essycoff_pos.db";
    private static final int DATABASE_VERSION = 1;
    
    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String TABLE_TRANSACTION_ITEMS = "transaction_items";
    
    // Users Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_ROLE = "role";
    private static final String KEY_IS_ACTIVE = "is_active";
    private static final String KEY_CREATED_AT = "created_at";
    
    // Products Table Columns
    private static final String KEY_PRODUCT_ID = "id";
    private static final String KEY_PRODUCT_NAME = "name";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_PRICE = "price";
    private static final String KEY_STOCK = "stock";
    private static final String KEY_IS_AVAILABLE = "is_available";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String KEY_DESCRIPTION = "description";
    
    // Transactions Table Columns
    private static final String KEY_TRANSACTION_ID = "id";
    private static final String KEY_TRANSACTION_NUMBER = "transaction_number";
    private static final String KEY_CASHIER_ID = "cashier_id";
    private static final String KEY_SUBTOTAL = "subtotal";
    private static final String KEY_TAX_AMOUNT = "tax_amount";
    private static final String KEY_TOTAL_AMOUNT = "total_amount";
    private static final String KEY_PAYMENT_METHOD = "payment_method";
    private static final String KEY_PAYMENT_AMOUNT = "payment_amount";
    private static final String KEY_CHANGE_AMOUNT = "change_amount";
    private static final String KEY_STATUS = "status";
    private static final String KEY_NOTES = "notes";
    
    // Transaction Items Table Columns
    private static final String KEY_ITEM_ID = "id";
    private static final String KEY_ITEM_TRANSACTION_ID = "transaction_id";
    private static final String KEY_ITEM_PRODUCT_ID = "product_id";
    private static final String KEY_ITEM_PRODUCT_NAME = "product_name";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_UNIT_PRICE = "unit_price";
    private static final String KEY_ITEM_SUBTOTAL = "subtotal";
    private static final String KEY_ITEM_NOTES = "notes";
    
    private static DatabaseHelper instance;
    
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            if (context == null) {
                throw new IllegalArgumentException("Context cannot be null");
            }
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USERNAME + " TEXT UNIQUE NOT NULL,"
                + KEY_PASSWORD + " TEXT NOT NULL,"
                + KEY_FULL_NAME + " TEXT NOT NULL,"
                + KEY_ROLE + " TEXT NOT NULL CHECK(" + KEY_ROLE + " IN ('staff', 'manager')),"
                + KEY_IS_ACTIVE + " INTEGER DEFAULT 1,"
                + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        
        // Create Products Table
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + KEY_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_PRODUCT_NAME + " TEXT NOT NULL,"
                + KEY_CATEGORY + " TEXT NOT NULL CHECK(" + KEY_CATEGORY + " IN ('Kopi', 'Makanan', 'Minuman', 'Lainnya')),"
                + KEY_PRICE + " REAL NOT NULL CHECK(" + KEY_PRICE + " > 0),"
                + KEY_STOCK + " INTEGER DEFAULT 0 CHECK(" + KEY_STOCK + " >= 0),"
                + KEY_IS_AVAILABLE + " INTEGER DEFAULT 1,"
                + KEY_IMAGE_URL + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        
        // Create Transactions Table
        String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + KEY_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TRANSACTION_NUMBER + " TEXT UNIQUE NOT NULL,"
                + KEY_CASHIER_ID + " INTEGER,"
                + KEY_SUBTOTAL + " REAL NOT NULL CHECK(" + KEY_SUBTOTAL + " >= 0),"
                + KEY_TAX_AMOUNT + " REAL NOT NULL CHECK(" + KEY_TAX_AMOUNT + " >= 0),"
                + KEY_TOTAL_AMOUNT + " REAL NOT NULL CHECK(" + KEY_TOTAL_AMOUNT + " >= 0),"
                + KEY_PAYMENT_METHOD + " TEXT NOT NULL CHECK(" + KEY_PAYMENT_METHOD + " IN ('Tunai', 'Kartu Debit', 'Kartu Kredit', 'E-Wallet')),"
                + KEY_PAYMENT_AMOUNT + " REAL NOT NULL CHECK(" + KEY_PAYMENT_AMOUNT + " >= 0),"
                + KEY_CHANGE_AMOUNT + " REAL DEFAULT 0 CHECK(" + KEY_CHANGE_AMOUNT + " >= 0),"
                + KEY_STATUS + " TEXT DEFAULT 'completed' CHECK(" + KEY_STATUS + " IN ('pending', 'completed', 'cancelled')),"
                + KEY_NOTES + " TEXT,"
                + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + KEY_CASHIER_ID + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")"
                + ")";
        
        // Create Transaction Items Table
        String CREATE_TRANSACTION_ITEMS_TABLE = "CREATE TABLE " + TABLE_TRANSACTION_ITEMS + "("
                + KEY_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_ITEM_TRANSACTION_ID + " INTEGER,"
                + KEY_ITEM_PRODUCT_ID + " INTEGER,"
                + KEY_ITEM_PRODUCT_NAME + " TEXT NOT NULL,"
                + KEY_QUANTITY + " INTEGER NOT NULL CHECK(" + KEY_QUANTITY + " > 0),"
                + KEY_UNIT_PRICE + " REAL NOT NULL CHECK(" + KEY_UNIT_PRICE + " > 0),"
                + KEY_ITEM_SUBTOTAL + " REAL NOT NULL CHECK(" + KEY_ITEM_SUBTOTAL + " > 0),"
                + KEY_ITEM_NOTES + " TEXT,"
                + "FOREIGN KEY(" + KEY_ITEM_TRANSACTION_ID + ") REFERENCES " + TABLE_TRANSACTIONS + "(" + KEY_TRANSACTION_ID + ") ON DELETE CASCADE,"
                + "FOREIGN KEY(" + KEY_ITEM_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + KEY_PRODUCT_ID + ")"
                + ")";
        
        // Execute table creation
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_PRODUCTS_TABLE);
        db.execSQL(CREATE_TRANSACTIONS_TABLE);
        db.execSQL(CREATE_TRANSACTION_ITEMS_TABLE);
        
        // Insert default data
        insertDefaultData(db);
        
        Log.d(TAG, "Database tables created successfully");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            // Mulai transaksi
            db.beginTransaction();
            
            // Backup data penting jika diperlukan
            // ...
            
            // Hapus tabel yang ada
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION_ITEMS);
            
            // Buat ulang database
            onCreate(db);
            
            // Restore data jika diperlukan
            // ...
            
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error during database upgrade: " + e.getMessage(), e);
            // Jika terjadi error, transaksi akan di-rollback secara otomatis
            throw e;
        } finally {
            try {
                db.endTransaction();
            } catch (Exception e) {
                Log.e(TAG, "Error ending transaction: " + e.getMessage());
            }
        }
    }
    
    /**
     * Insert default users and products
     */
    private void insertDefaultData(SQLiteDatabase db) {
        // Insert default users with hashed passwords
        String managerPassword = BCrypt.hashpw("password123", BCrypt.gensalt());
        String staffPassword = BCrypt.hashpw("password123", BCrypt.gensalt());
        
        ContentValues managerValues = new ContentValues();
        managerValues.put(KEY_USERNAME, "manager");
        managerValues.put(KEY_PASSWORD, managerPassword);
        managerValues.put(KEY_FULL_NAME, "Manager EssyCoff");
        managerValues.put(KEY_ROLE, "manager");
        managerValues.put(KEY_IS_ACTIVE, 1);
        
        ContentValues staffValues = new ContentValues();
        staffValues.put(KEY_USERNAME, "staff");
        staffValues.put(KEY_PASSWORD, staffPassword);
        staffValues.put(KEY_FULL_NAME, "Staff Kasir");
        staffValues.put(KEY_ROLE, "staff");
        staffValues.put(KEY_IS_ACTIVE, 1);
        
        db.insert(TABLE_USERS, null, managerValues);
        db.insert(TABLE_USERS, null, staffValues);
        
        // Insert sample products
        insertSampleProducts(db);
        
        Log.d(TAG, "Default data inserted successfully");
    }
    
    /**
     * Insert sample products
     */
    private void insertSampleProducts(SQLiteDatabase db) {
        String[][] products = {
            // Kopi
            {"Espresso", "COFFEE", "15000", "100", "Kopi espresso klasik dengan rasa yang kuat"},
            {"Americano", "COFFEE", "18000", "100", "Espresso dengan air panas, rasa yang lebih ringan"},
            {"Cappuccino", "COFFEE", "22000", "100", "Espresso dengan susu steamed dan foam"},
            {"Latte", "COFFEE", "25000", "100", "Espresso dengan susu steamed, rasa yang creamy"},
            {"Mocha", "COFFEE", "28000", "100", "Espresso dengan cokelat dan susu steamed"},
            
            // Makanan
            {"Croissant", "FOOD", "18000", "20", "Croissant butter yang renyah"},
            {"Sandwich Club", "FOOD", "35000", "15", "Sandwich dengan ayam, sayuran, dan saus"},
            {"Nasi Goreng Spesial", "FOOD", "28000", "20", "Nasi goreng dengan telur dan ayam"},
            {"Donat Glazed", "FOOD", "12000", "25", "Donat dengan glazed manis"}
        };
        
        for (String[] product : products) {
            ContentValues values = new ContentValues();
            values.put(KEY_PRODUCT_NAME, product[0]);
            values.put(KEY_CATEGORY, product[1]);
            values.put(KEY_PRICE, Double.parseDouble(product[2]));
            values.put(KEY_STOCK, Integer.parseInt(product[3]));
            values.put(KEY_IS_AVAILABLE, 1);
            values.put(KEY_DESCRIPTION, product[4]);
            
            db.insert(TABLE_PRODUCTS, null, values);
        }
    }
    
    /**
     * Authenticate user with username and password
     */
    public User authenticateUser(String username, String password) {
        if (username == null || password == null || password.trim().isEmpty()) {
            return null;
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        User user = null;

        try {
            db = this.getReadableDatabase();
            if (db == null) {
                Log.e(TAG, "Could not open database for reading");
                return null;
            }

            String[] columns = {
                    KEY_USER_ID,
                    KEY_USERNAME,
                    KEY_PASSWORD,
                    KEY_FULL_NAME,
                    KEY_ROLE,
                    KEY_IS_ACTIVE
            };

            String selection = KEY_USERNAME + " = ? AND " + KEY_IS_ACTIVE + " = ?";
            String[] selectionArgs = {username, "1"};

            cursor = db.query(
                    TABLE_USERS,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                try {
                    int passwordIndex = cursor.getColumnIndex(KEY_PASSWORD);
                    if (passwordIndex == -1) {
                        Log.e(TAG, "Password column not found in database");
                        return null;
                    }
                    
                    String storedPassword = cursor.getString(passwordIndex);
                    if (storedPassword == null) {
                        Log.e(TAG, "No password found for user: " + username);
                        return null;
                    }
                    
                    // Verifikasi password menggunakan BCrypt
                    if (BCrypt.checkpw(password, storedPassword)) {
                        user = new User();
                        user.setId(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_ID)));
                        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USERNAME)));
                        user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FULL_NAME)));
                        user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ROLE)));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error authenticating user: " + e.getMessage(), e);
                    return null;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Database error during authentication: " + e.getMessage(), e);
            return null;
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing cursor: " + e.getMessage());
                }
            }
            
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing database: " + e.getMessage());
                }
            }
        }
        
        return user;
    }
    
    /**
     * Get all products
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + KEY_IS_AVAILABLE + " = 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRODUCT_ID)));
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRODUCT_NAME)));
                product.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY)));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PRICE)));
                product.setStock(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STOCK)));
                product.setAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_AVAILABLE)) == 1);
                product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE_URL)));
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
                
                products.add(product);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return products;
    }
    
    /**
     * Add new product
     */
    public long addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_PRODUCT_NAME, product.getName());
        values.put(KEY_CATEGORY, product.getCategory());
        values.put(KEY_PRICE, product.getPrice());
        values.put(KEY_STOCK, product.getStock());
        values.put(KEY_IS_AVAILABLE, product.isAvailable() ? 1 : 0);
        values.put(KEY_IMAGE_URL, product.getImageUrl());
        values.put(KEY_DESCRIPTION, product.getDescription());
        
        long id = db.insert(TABLE_PRODUCTS, null, values);
        db.close();
        
        return id;
    }
    
    /**
     * Update product
     */
    public int updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_PRODUCT_NAME, product.getName());
        values.put(KEY_CATEGORY, product.getCategory());
        values.put(KEY_PRICE, product.getPrice());
        values.put(KEY_STOCK, product.getStock());
        values.put(KEY_IS_AVAILABLE, product.isAvailable() ? 1 : 0);
        values.put(KEY_IMAGE_URL, product.getImageUrl());
        values.put(KEY_DESCRIPTION, product.getDescription());
        
        int rowsAffected = db.update(TABLE_PRODUCTS, values, KEY_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(product.getId())});
        db.close();
        
        return rowsAffected;
    }
    
    /**
     * Delete product
     */
    public void deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, KEY_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(productId)});
        db.close();
    }
    
    /**
     * Add new transaction
     */
    public long addTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_TRANSACTION_NUMBER, transaction.getTransactionNumber());
        values.put(KEY_CASHIER_ID, transaction.getCashierId());
        values.put(KEY_SUBTOTAL, transaction.getSubtotal());
        values.put(KEY_TAX_AMOUNT, transaction.getTaxAmount());
        values.put(KEY_TOTAL_AMOUNT, transaction.getTotalAmount());
        values.put(KEY_PAYMENT_METHOD, transaction.getPaymentMethod());
        values.put(KEY_PAYMENT_AMOUNT, transaction.getPaymentAmount());
        values.put(KEY_CHANGE_AMOUNT, transaction.getChangeAmount());
        values.put(KEY_STATUS, transaction.getStatus());
        values.put(KEY_NOTES, transaction.getNotes());
        
        long id = db.insert(TABLE_TRANSACTIONS, null, values);
        db.close();
        
        return id;
    }
    
    /**
     * Get all transactions
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selectQuery = "SELECT * FROM " + TABLE_TRANSACTIONS + " ORDER BY " + KEY_CREATED_AT + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();
                transaction.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TRANSACTION_ID)));
                transaction.setTransactionNumber(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TRANSACTION_NUMBER)));
                transaction.setCashierId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CASHIER_ID)));
                transaction.setSubtotal(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_SUBTOTAL)));
                transaction.setTaxAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_TAX_AMOUNT)));
                transaction.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_TOTAL_AMOUNT)));
                transaction.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PAYMENT_METHOD)));
                transaction.setPaymentAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PAYMENT_AMOUNT)));
                transaction.setChangeAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_CHANGE_AMOUNT)));
                transaction.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STATUS)));
                transaction.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOTES)));
                transaction.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_CREATED_AT)));
                
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return transactions;
    }
    
    /**
     * Insert new transaction
     */
    public boolean insertTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        try {
            db.beginTransaction();
            
            // Insert transaction
            ContentValues values = new ContentValues();
            values.put(KEY_TRANSACTION_NUMBER, transaction.getTransactionNumber());
            values.put(KEY_CASHIER_ID, transaction.getCashierId());
            values.put(KEY_SUBTOTAL, transaction.getSubtotal());
            values.put(KEY_TAX_AMOUNT, transaction.getTaxAmount());
            values.put(KEY_TOTAL_AMOUNT, transaction.getTotalAmount());
            values.put(KEY_PAYMENT_METHOD, transaction.getPaymentMethod());
            values.put(KEY_PAYMENT_AMOUNT, transaction.getPaymentAmount());
            values.put(KEY_CHANGE_AMOUNT, transaction.getChangeAmount());
            values.put(KEY_STATUS, transaction.getStatus());
            values.put(KEY_NOTES, transaction.getNotes());
            values.put(KEY_CREATED_AT, transaction.getCreatedAt());
            
            long transactionId = db.insert(TABLE_TRANSACTIONS, null, values);
            
            if (transactionId != -1) {
                db.setTransactionSuccessful();
                return true;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error inserting transaction: " + e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }
        
        return false;
    }
}
