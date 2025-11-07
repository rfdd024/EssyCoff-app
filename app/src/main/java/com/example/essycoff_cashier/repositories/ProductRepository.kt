package com.example.essycoff_cashier.repositories

import com.example.essycoff_cashier.models.Product
import com.example.essycoff_cashier.utils.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ProductRepository {
    
    private val client = SupabaseClient.client
    
    /**
     * Get all products with optional search query and category filter
     * @param query Search query to filter products by name or description
     * @param category Category to filter products by
     * @param limit Maximum number of products to return
     * @return Flow of list of products
     */
    fun searchProducts(
        query: String = "",
        category: String? = null,
        limit: Int = 20
    ): Flow<List<Product>> = flow {
        try {
            val result = client.from("products").select(
                columns = Columns.list(
                    "id", "name", "description", "price", "category", "image_url", "stock"
                )
            ) {
                // Apply search query if provided
                if (query.isNotBlank()) {
                    or {
                        ilike("name", "%$query%")
                        ilike("description", "%$query%")
                    }
                }
                
                // Apply category filter if provided
                if (!category.isNullOrBlank()) {
                    eq("category", category)
                }
                
                // Apply limit
                limit(limit)
                
                // Order by name
                order("name")
            }.decodeList<Product>()
            
            emit(result)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get all unique product categories
     */
    suspend fun getCategories(): List<String> = withContext(Dispatchers.IO) {
        try {
            client.from("products")
                .select(columns = Columns.list("category"))
                .decodeList<Map<String, String>>()
                .mapNotNull { it["category"] }
                .distinct()
                .sorted()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun getAllProducts(): List<Product> = withContext(Dispatchers.IO) {
        try {
            client.from("products").select().decodeList<Product>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun getProductById(productId: String): Product? = withContext(Dispatchers.IO) {
        try {
            client.from("products").select {
                filter {
                    eq("id", productId)
                }
            }.decodeSingleOrNull<Product>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun updateProductStock(productId: String, newStock: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            client.from("products").update(
                mapOf("stock" to newStock)
            ) {
                filter {
                    eq("id", productId)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
