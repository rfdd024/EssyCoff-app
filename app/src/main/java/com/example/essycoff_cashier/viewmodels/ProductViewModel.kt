package com.example.essycoff_cashier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.essycoff_cashier.models.Product
import com.example.essycoff_cashier.repositories.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val repository = ProductRepository()
    
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products
    
    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    // Search query and category filter
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)
    
    init {
        loadCategories()
        observeSearchChanges()
    }
    
    /**
     * Observe changes in search query and category filter
     */
    private fun observeSearchChanges() {
        viewModelScope.launch {
            _searchQuery.collectLatest { query ->
                searchProducts(query, _selectedCategory.value)
            }
        }
        
        viewModelScope.launch {
            _selectedCategory.collectLatest { category ->
                searchProducts(_searchQuery.value, category)
            }
        }
    }
    
    /**
     * Update search query
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * Update selected category
     */
    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = category
    }
    
    /**
     * Search products with current query and category filter
     */
    private fun searchProducts(query: String, category: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.searchProducts(query, category).collect { productList ->
                    _products.value = productList
                    _error.value = ""
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error searching products"
                _products.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load all available product categories
     */
    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val categoriesList = repository.getCategories()
                _categories.value = categoriesList
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading categories"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Get product by ID
     */
    fun getProductById(productId: String, onResult: (Product?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val product = repository.getProductById(productId)
                onResult(product)
                _error.value = ""
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading product"
                onResult(null)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val productList = repository.getAllProducts()
                _products.value = productList
                _error.value = ""
            } catch (e: Exception) {
                _error.value = "Error loading products: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateProductStock(productId: String, newStock: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val success = repository.updateProductStock(productId, newStock)
                onComplete(success)
            } catch (e: Exception) {
                _error.value = "Error updating product: ${e.message}"
                onComplete(false)
            }
        }
    }
}
