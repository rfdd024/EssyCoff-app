package com.example.essycoff_cashier.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val image_url: String = "",
    val stock: Int = 0,
    val created_at: String = ""
)
