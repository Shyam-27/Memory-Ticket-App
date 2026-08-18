package com.example.memory_ticket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.memory_ticket.navigation.AppNavigation
import com.example.memory_ticket.ui.theme.Memory_TicketTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Memory_TicketTheme {
                AppNavigation()
            }
        }
    }
}