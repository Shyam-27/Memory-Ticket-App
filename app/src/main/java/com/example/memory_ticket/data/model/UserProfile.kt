package com.example.memory_ticket.data.model

data class Memory(
    val memoryId: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationName: String = "",
    val ticketStyle: String = "Classic",
    val createdAt: Long = 0L
)