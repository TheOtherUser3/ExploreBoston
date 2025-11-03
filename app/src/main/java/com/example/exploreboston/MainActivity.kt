package com.example.exploreboston

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import com.example.exploreboston.ExploreNavGraph
import com.example.exploreboston.ui.theme.ExploreBostonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExploreBostonTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    // When the user taps "Home" (which clears stack), we set this to true.
                    var homeCycleCompleted by rememberSaveable { mutableStateOf(false) }

                    ExploreNavGraph(
                        navController = navController,
                        onHomeClearedStack = { homeCycleCompleted = true },
                        homeCycleCompleted = homeCycleCompleted
                    )
                }
            }
        }
    }
}
