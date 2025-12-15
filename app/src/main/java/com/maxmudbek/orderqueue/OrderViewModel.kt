package com.maxmudbek.orderqueue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class OrderViewModel : ViewModel() {

    private val _queueSize = MutableStateFlow(0)
    val queueSize: StateFlow<Int> = _queueSize.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private var producerJob: Job? = null
    private var consumerJob: Job? = null

    // Using Channel as per requirements
    private val orderChannel = Channel<Int>(Channel.UNLIMITED)
    private var orderIdCounter = 0

    private val MAX_QUEUE_SIZE = 25

    // Track if the simulation has ever started to control UI visibility
    private val _hasStarted = MutableStateFlow(false)
    val hasStarted: StateFlow<Boolean> = _hasStarted.asStateFlow()

    fun toggleProcessing() {
        if (!_hasStarted.value) {
            _hasStarted.value = true
        }
        
        if (_isProcessing.value) {
            pauseProcessing()
        } else {
            startProcessing()
        }
    }

    private fun startProcessing() {
        _isProcessing.value = true
        
        // Producer: Adds to queue every 250ms
        if (producerJob == null || producerJob?.isActive == false) {
            producerJob = viewModelScope.launch {
                while (isActive) {
                    delay(250)
                    orderIdCounter++
                    orderChannel.trySend(orderIdCounter)
                    _queueSize.value += 1
                }
            }
        }

        // Consumer: Removes from queue every 100-250ms
        consumerJob = viewModelScope.launch {
            while (isActive) {
                val delayTime = Random.nextLong(100, 250)
                delay(delayTime)
                
                val result = orderChannel.tryReceive()
                if (result.isSuccess) {
                    _queueSize.value -= 1
                }
            }
        }
    }

    private fun pauseProcessing() {
        _isProcessing.value = false
        // Consumer stops
        consumerJob?.cancel()
        // Producer continues (Requirement: "The producer continues to push new orders")
    }
    
    override fun onCleared() {
        super.onCleared()
        producerJob?.cancel()
        consumerJob?.cancel()
    }
}
