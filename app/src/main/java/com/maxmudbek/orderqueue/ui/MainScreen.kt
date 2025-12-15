package com.maxmudbek.orderqueue.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.maxmudbek.orderqueue.ui.HostGrotesk
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import com.maxmudbek.orderqueue.OrderViewModel
import com.maxmudbek.orderqueue.R

@Composable
fun MainScreen(viewModel: OrderViewModel) {
    val queueSize by viewModel.queueSize.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val hasStarted by viewModel.hasStarted.collectAsState()

    val max = 25f
    val rawFraction = queueSize / max
    val percentInt = (rawFraction * 100).toInt()
    val isOverload = rawFraction > 1f

    // Color for main progress (up to 100%)
    val mainProgressColor = when {
        rawFraction >= 0.67f -> colorResource(id = R.color.error_red)
        rawFraction >= 0.34f -> colorResource(id = R.color.warning_yellow)
        else -> colorResource(id = R.color.primary_green)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.bg_color)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.width(360.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Order Queue Outpost",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = HostGrotesk,
                    color = colorResource(id = R.color.text_primary),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (!hasStarted) {
                    Text(
                        text = "Press Play to start processing orders",
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.text_secondary),
                        textAlign = TextAlign.Center
                    )
                }

                if (hasStarted) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Queue count and percent row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Queue: $queueSize/25",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = HostGrotesk,
                                color = colorResource(id = R.color.text_primary)
                            )
                            Text(
                                text = "$percentInt%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = HostGrotesk,
                                color = if (isOverload) colorResource(id = R.color.overload_red) else colorResource(id = R.color.text_secondary)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Custom segmented progress bar
                        Column(modifier = Modifier.fillMaxWidth()) {
                            BoxWithConstraints(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .then(
                                        if (isOverload) Modifier.border(
                                            2.dp,
                                            colorResource(id = R.color.error_border),
                                            RoundedCornerShape(8.dp)
                                        ) else Modifier
                                    )
                                    .background(colorResource(id = R.color.progress_bg))
                            ) {
                                val totalWidth = maxWidth
                                val segments = if (isOverload) 4 else 3

                                // Calculate fill widths
                                val mainFillFraction = rawFraction.coerceAtMost(1f)
                                val mainFillWidth: Dp = if (isOverload) {
                                    // When overloaded, main progress fills 3/4 of the bar (representing 100%)
                                    totalWidth * 3f / 4f
                                } else {
                                    totalWidth * mainFillFraction
                                }

                                // Overload fill (from 100% to current %)
                                val overloadFraction = if (isOverload) (rawFraction - 1f).coerceAtMost(1f) else 0f
                                val overloadWidth: Dp = if (isOverload) {
                                    // Overload fills the remaining 1/4 segment proportionally
                                    totalWidth * (1f / 4f) * overloadFraction.coerceAtMost(1f)
                                } else {
                                    0.dp
                                }

                                // Main progress fill
                                Box(
                                    modifier = Modifier
                                        .width(mainFillWidth)
                                        .fillMaxHeight()
                                        .background(mainProgressColor)
                                )

                                // Overload segment (darker red, right of 100%)
                                if (isOverload && overloadWidth > 0.dp) {
                                    Box(
                                        modifier = Modifier
                                            .padding(start = mainFillWidth)
                                            .width(overloadWidth)
                                            .fillMaxHeight()
                                            .background(colorResource(id = R.color.overload_red))
                                    )
                                }

                                // Dividers overlay
                                Row(
                                    modifier = Modifier.matchParentSize(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (i in 0 until segments) {
                                        Box(modifier = Modifier.weight(1f))
                                        if (i != segments - 1) {
                                            Box(
                                                modifier = Modifier
                                                    .width(1.dp)
                                                    .fillMaxHeight()
                                                    .background(colorResource(id = R.color.white))
                                            )
                                        }
                                    }
                                }
                            }

                            // 100% marker when overloaded (with 6.dp top margin)
                            if (isOverload) {
                                Spacer(modifier = Modifier.height(6.dp))
                                BoxWithConstraints(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(20.dp)
                                ) {
                                    val markerPosition = maxWidth * 3f / 4f
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Spacer(modifier = Modifier.width(markerPosition - 16.dp))
                                        Text(
                                            text = "100%",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = HostGrotesk,
                                            color = colorResource(id = R.color.text_disabled)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Button (103x44 per Figma). Primary when starting, Surface-Highest when paused
                Button(
                    onClick = { viewModel.toggleProcessing() },
                    modifier = Modifier
                        .width(103.dp)
                        .height(44.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isProcessing) colorResource(id = R.color.surface_highest) else colorResource(id = R.color.primary_green)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val icon = if (isProcessing) R.drawable.ic_pause else R.drawable.ic_play
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            tint = if (isProcessing) colorResource(id = R.color.text_primary) else colorResource(id = R.color.surface_higher),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isProcessing) "Pause" else "Start",
                            color = if (isProcessing) colorResource(id = R.color.text_primary) else colorResource(id = R.color.surface_higher),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = HostGrotesk
                        )
                    }
                }
            }
        }
    }
}
