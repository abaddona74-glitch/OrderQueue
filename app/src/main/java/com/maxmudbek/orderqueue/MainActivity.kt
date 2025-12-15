package com.maxmudbek.orderqueue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.maxmudbek.orderqueue.OrderViewModel
import com.maxmudbek.orderqueue.ui.MainScreen

class MainActivity : ComponentActivity() {

    private val viewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MainScreen(viewModel = viewModel)
        }
    }
}