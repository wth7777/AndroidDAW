package com.example.simpledawexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Pause
import androidx.compose.material3.icons.filled.PlayArrow
import androidx.compose.material3.icons.filled.Stop
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    init {
        // Load the native library
        System.loadLibrary("audio_engine")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize and start audio stream
        nativeInitOboe()

        setContent {
            SimpleDaweTheme {
                // Surface with theme colors
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = androidx.compose.foundation.layout.Alignment.CenterHorizontally,
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                    ) {
                        Text(
                            text = "Simple DAW - 440 Hz Sine",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Transport Controls Row
                        Row(
                            modifier = Modifier.width(120.dp),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
                        ) {
                            var isPlaying by remember { mutableStateOf(false) }

                            IconButton(
                                onClick = {
                                    isPlaying = !isPlaying
                                    nativeSetTransport(isPlaying)
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                if (isPlaying) {
                                    Icon(Icons.Filled.Pause, contentDescription = "Pause")
                                } else {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = "Play")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Frequency Control
                        Column(
                            horizontalAlignment = androidx.compose.foundation.layout.Alignment.CenterHorizontally
                        ) {
                            Text("Frequency: ${remember { mutableStateOf(440) }} Hz")
                            Slider(
                                value = remember { mutableStateOf(440f) },
                                onValueChange = {
                                    it
                                    nativeSetFrequency(it)
                                },
                                valueRange = 50.0..2000.0,
                                steps = 10
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up audio stream
        nativeReleaseOboe()
    }

    external fun nativeInitOboe()
    external fun nativeReleaseOboe()
    external fun nativeSetTransport(playing: Boolean)
    external fun nativeSetFrequency(frequency: Float)
}