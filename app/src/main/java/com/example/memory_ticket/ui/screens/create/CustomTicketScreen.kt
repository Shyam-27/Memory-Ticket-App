package com.example.memory_ticket.ui.screens.create

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage

@Composable
fun CustomizeTicketScreen(
    imageUri: Uri,
    isSaving: Boolean = false,

    onSave: (
        title: String,
        category: String,
        description: String,
        ticketStyle: String
    ) -> Unit,

    onBack: () -> Unit
) {

    var title by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableStateOf("Vacation")
    }

    var selectedStyle by remember {
        mutableStateOf("Classic")
    }

    val categories = listOf(
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

    val styles = listOf(
        "Classic",
        "Travel",
        "Postcard",
        "Polaroid",
        "Retro"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = "Customize Memory Ticket",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Create your own memory ticket",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------- TICKET PREVIEW ----------------

        TicketPreview(
            imageUri = imageUri,
            title = title,
            category = selectedCategory,
            description = description,
            ticketStyle = selectedStyle
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------- TITLE ----------------

        Text(
            text = "Memory Title",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Example: Beach Day")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------- CATEGORY ----------------

        Text(
            text = "Category",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column {

            categories.chunked(3).forEach { rowCategories ->

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {

                    rowCategories.forEach { category ->

                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = {
                                selectedCategory = category
                            },
                            label = {
                                Text(category)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------------- STYLE ----------------

        Text(
            text = "Ticket Style",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column {

            styles.chunked(3).forEach { rowStyles ->

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {

                    rowStyles.forEach { style ->

                        FilterChip(
                            selected = selectedStyle == style,
                            onClick = {
                                selectedStyle = style
                            },
                            label = {
                                Text(style)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------------- DESCRIPTION ----------------

        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Write something about this memory")
            },
            minLines = 3,
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------- SAVE ----------------

        Button(
            onClick = {
                if (!isSaving) {
                    onSave(
                        title.trim(),
                        selectedCategory,
                        description.trim(),
                        selectedStyle
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("SAVING TICKET...")
            } else {
                Text("SAVE MEMORY TICKET")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        ) {
            Text("← Back")
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}


// ======================================================
// TICKET PREVIEW
// ======================================================

@Composable
private fun TicketPreview(
    imageUri: Uri,
    title: String,
    category: String,
    description: String,
    ticketStyle: String
) {

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
                width = 3.dp,
                color = borderColor,
                shape = shape
            )
            .padding(12.dp)
    ) {

        Text(
            text = "MEMORY TICKET",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        AsyncImage(
            model = imageUri,
            contentDescription = "Memory Ticket Photo",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.3f)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (title.isBlank()) {
                "Your Memory"
            } else {
                title
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "🏷 $category",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        if (description.isNotBlank()) {

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "✈ $ticketStyle",
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "• Memory Ticket",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}