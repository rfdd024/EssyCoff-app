package com.example.essycoff_cashier.utils

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.okhttp.OkHttp

object SupabaseClient {
    // Replace these with your Supabase project URL and public anon key
    private const val SUPABASE_URL = "https://pqhjevvxiybyfyzhpkcy.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBxaGpldnZ4aXlieWZ5emhwa2N5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjIwODQyNjMsImV4cCI6MjA3NzY2MDI2M30.Ak6E25fzM948d9xud_cBnrDS2DDIpsyVfbNqy2RL9Sw"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
        install(GoTrue)
        httpEngine = OkHttp.create()
    }
}
