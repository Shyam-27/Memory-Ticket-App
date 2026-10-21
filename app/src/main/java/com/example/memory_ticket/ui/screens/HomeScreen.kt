package com.example.memory_ticket.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.memory_ticket.data.model.Memory
import com.example.memory_ticket.data.repository.MemoryRepository
import com.example.memory_ticket.firebase.FirebaseManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onAddMemory: () -> Unit = {}
) {
    val repository = remember { MemoryRepository() }
    val currentUser = FirebaseManager.auth.currentUser

    var memories by remember { mutableStateOf<List<Memory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedMemoryForDetail by remember { mutableStateOf<Memory?>(null) }

    val categories = remember {
        listOf(
            "All",
            "Vacation",
            "Holiday",
            "Travel",
            "Fun",
            "Food",
            "Family",
            "Friends",
            "College",
            "Other"
        )
    }

    LaunchedEffect(currentUser?.uid) {
        if (currentUser != null) {
            isLoading = true
            val result = repository.getMemories(currentUser.uid)
            memories = result.getOrDefault(emptyList())
            isLoading = false
        } else {
            isLoading = false
        }
    }

    val filteredMemories = remember(memories, searchQuery, selectedCategory) {
        memories.filter { memory ->
            val matchesCategory = selectedCategory == "All" ||
                    memory.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    memory.title.contains(searchQuery, ignoreCase = true) ||
                    memory.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Memory Ticket",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Capture. Customize. Remember.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = onLogout
                ) {
                    Text("Logout")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Your memories",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Search memories...")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(category)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Recent Memories",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    filteredMemories.isEmpty() -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (memories.isEmpty()) {
                                    "No memories yet.\nCreate your first memory ticket!"
                                } else {
                                    "No memories found for this filter."
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredMemories, key = { it.memoryId }) { memory ->
                                MemoryTicketCard(
                                    memory = memory,
                                    onClick = { selectedMemoryForDetail = memory }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onAddMemory,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("＋ Create Memory")
            }
        }
    }

    // Detail Dialog when user taps a ticket card
    selectedMemoryForDetail?.let { memory ->
        MemoryDetailDialog(
            memory = memory,
            onDismiss = { selectedMemoryForDetail = null }
        )
    }
}

@Composable
private fun MemoryTicketCard(
    memory: Memory,
    onClick: () -> Unit = {}
) {
    val ticketStyle = memory.ticketStyle

    val shape = when (ticketStyle) {
        "Postcard" -> RoundedCornerShape(8.dp)
        "Polaroid" -> RoundedCornerShape(4.dp)
        "Retro" -> RoundedCornerShape(18.dp)
        else -> RoundedCornerShape(20.dp)
    }

    val backgroundColor = when (ticketStyle) {
        "Travel" -> Color(0xFFEAF4FF)
        "Postcard" -> Color(0xFFFFF8E7)
        "Polaroid" -> Color.White
        "Retro" -> Color(0xFFFFF1E6)
        else -> Color.White
    }

    val borderColor = when (ticketStyle) {
        "Retro" -> Color(0xFF8D5A2B)
        "Travel" -> Color(0xFF1976D2)
        "Postcard" -> Color(0xFF555555)
        "Polaroid" -> Color(0xFF222222)
        else -> Color(0xFF1976D2)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MEMORY TICKET",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "✈ $ticketStyle",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (memory.imageUrl.isNotBlank()) {
            AsyncImage(
                model = memory.imageUrl,
                contentDescription = memory.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.4f)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        Text(
            text = if (memory.title.isBlank()) "Untitled Memory" else memory.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "🏷 ${memory.category}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )

        if (memory.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = memory.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MemoryDetailDialog(
    memory: Memory,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✈ ${memory.ticketStyle} Ticket",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "🏷 ${memory.category}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (memory.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = memory.imageUrl,
                        contentDescription = memory.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.2f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }

                Text(
                    text = if (memory.title.isBlank()) "Untitled Memory" else memory.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                if (memory.createdAt > 0L) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val formattedDate = remember(memory.createdAt) {
                        SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
                            .format(Date(memory.createdAt))
                    }
                    Text(
                        text = "Created: $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (memory.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = memory.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close")
                }
            }
        }
    }
}
