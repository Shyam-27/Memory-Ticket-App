package com.example.memory_ticket.supabase

import com.example.memory_ticket.BuildConfig

object SupabaseConfig {

    const val BUCKET_NAME = "memory-images"

    val projectUrl: String
        get() = BuildConfig.SUPABASE_URL

    val publishableKey: String
        get() = BuildConfig.SUPABASE_PUBLISHABLE_KEY
}