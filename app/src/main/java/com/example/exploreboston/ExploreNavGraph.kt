package com.example.exploreboston

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

// ---------- Routes (sealed) ----------
sealed class Route(val route: String) {
    object Home : Route("home")
    object Categories : Route("categories")
    object List : Route("list/{category}") {
        fun path(category: String) = "list/$category"
    }
    object Detail : Route("detail/{category}/{id}") {
        fun path(category: String, id: Int) = "detail/$category/$id"
    }
}

// ---------- Data Model + VM ----------
data class Location(val id: Int, val name: String, val category: String, val description: String)

class CityViewModel : ViewModel() {
    // Categories -> locations
    private val data = listOf(
        Location(1, "Museum of Fine Arts", "Museums", "World-class collection spanning cultures and eras."),
        Location(2, "MIT Museum", "Museums", "Inventive exhibits on science and technology."),
        Location(3, "Boston Common", "Parks", "America’s oldest public park."),
        Location(4, "Public Garden", "Parks", "Iconic swan boats and Victorian landscaping."),
        Location(5, "Neptune Oyster", "Restaurants", "Beloved for its lobster roll and raw bar."),
        Location(6, "Oleana", "Restaurants", "Creative Eastern Mediterranean plates.")
    )

    val categories: List<String> = data.map { it.category }.distinct()

    fun locationsFor(category: String): List<Location> =
        data.filter { it.category == category }

    fun getLocation(category: String, id: Int): Location? =
        data.firstOrNull { it.category == category && it.id == id }
}

// ---------- Reusable Top Bar ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(
    title: String,
    showBack: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            IconButton(onClick = onHome) {
                Icon(Icons.Filled.Home, contentDescription = "Home")
            }
        }
    )
}

// ---------- Screens ----------
@Composable
private fun HomeScreen(
    onGoCategories: () -> Unit,
    disableSystemBack: Boolean
) {
    // After a full cycle back to Home (stack cleared), disable system back.
    BackHandler(enabled = disableSystemBack) { /* consume back -> do nothing */ }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Explore Boston",
                showBack = false,
                onBack = {},
                onHome = onGoCategories // Home action here just navigates to categories for convenience
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Take a quick tour through Boston’s highlights.", style = MaterialTheme.typography.bodyLarge)
            Button(onClick = onGoCategories) { Text("Start Tour") }
        }
    }
}

@Composable
private fun CategoriesScreen(
    categories: List<String>,
    onCategoryClick: (String) -> Unit,
    onBack: () -> Unit,
    onHome: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Categories",
                showBack = true,
                onBack = onBack,
                onHome = onHome
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { cat ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategoryClick(cat) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(cat, style = MaterialTheme.typography.titleLarge)
                        Text("Tap to view all $cat", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun ListScreen(
    category: String,
    items: List<Location>,
    onItemClick: (Location) -> Unit,
    onBack: () -> Unit,
    onHome: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "All $category",
                showBack = true,
                onBack = onBack,
                onHome = onHome
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { loc ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(loc) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(loc.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(4.dp))
                        Text(loc.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailScreen(
    location: Location,
    onBack: () -> Unit,
    onHome: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = location.name,
                showBack = true,
                onBack = onBack,
                onHome = onHome
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(location.category, style = MaterialTheme.typography.labelLarge)
            Text(location.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(location.description, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// ---------- NavGraph ----------
@Composable
fun ExploreNavGraph(
    navController: NavHostController,
    onHomeClearedStack: () -> Unit,
    homeCycleCompleted: Boolean,
    vm: CityViewModel = viewModel()
) {
    val backstackEntry by navController.currentBackStackEntryAsState()
    val currentDest: NavDestination? = backstackEntry?.destination

    NavHost(
        navController = navController,
        startDestination = Route.Home.route
    ) {
        // Home (Intro)
        composable(Route.Home.route) {
            HomeScreen(
                onGoCategories = {
                    navController.navigate(Route.Categories.route) {
                        // standard tab-ish behavior: make Home the start
                        launchSingleTop = true
                    }
                },
                disableSystemBack = homeCycleCompleted
            )
        }

        // Categories
        composable(Route.Categories.route) {
            CategoriesScreen(
                categories = vm.categories,
                onCategoryClick = { cat ->
                    // String arg down to List
                    navController.navigate(Route.List.path(cat)) {
                        // keep stack tidy
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() },
                onHome = {
                    // Go 'Home' and clear the stack, inclusive = true
                    navController.navigate(Route.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                    onHomeClearedStack()
                }
            )
        }

        // List (All Museums/Parks/Restaurants)
        composable(
            route = Route.List.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { entry ->
            val category = entry.arguments?.getString("category").orEmpty()
            val items = vm.locationsFor(category)
            ListScreen(
                category = category,
                items = items,
                onItemClick = { loc ->
                    // String + Int args to Detail
                    navController.navigate(Route.Detail.path(loc.category, loc.id)) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() },
                onHome = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                    onHomeClearedStack()
                }
            )
        }

        // Detail (e.g., "MIT Museum"), consumes String + Int args
        composable(
            route = Route.Detail.route,
            arguments = listOf(
                navArgument("category") { type = NavType.StringType },
                navArgument("id") { type = NavType.IntType }
            )
        ) { entry ->
            val cat = entry.arguments?.getString("category").orEmpty()
            val id = entry.arguments?.getInt("id") ?: -1
            val loc = vm.getLocation(cat, id)
            if (loc != null) {
                DetailScreen(
                    location = loc,
                    onBack = { navController.popBackStack() },
                    onHome = {
                        navController.navigate(Route.Home.route) {
                            // Clear stack all the way back to Home, inclusive
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                        onHomeClearedStack()
                    }
                )
            } else {
                // Fallback if bad args
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }
    }
}
