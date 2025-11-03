package com.example.assignment5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.assignment5.ui.Recipe
import com.example.assignment5.ui.RecipeViewModel
import com.example.assignment5.ui.theme.Assignment5Theme
import java.util.UUID

class Q1Activity : ComponentActivity() {
    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            Assignment5Theme {
                RecipeApp(viewModel = viewModel)
            }
        }
    }
}

sealed class RecipeRoutes(val route: String) {
    object Home : RecipeRoutes("home")
    object Detail : RecipeRoutes("detail/{recipeId}") {
        fun createRoute(recipeId: String) = "detail/$recipeId"
    }
    object AddRecipe : RecipeRoutes("add_recipe")
    object Settings : RecipeRoutes("settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeApp(
    viewModel: RecipeViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("What's for Dinner?") }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentRoute == RecipeRoutes.Home.route,
                    onClick = {
                        navController.navigate(RecipeRoutes.Home.route) {
                            popUpTo(RecipeRoutes.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Recipe") },
                    label = { Text("Add") },
                    selected = currentRoute == RecipeRoutes.AddRecipe.route,
                    onClick = {
                        navController.navigate(RecipeRoutes.AddRecipe.route) {
                            popUpTo(RecipeRoutes.Home.route)
                            launchSingleTop = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = currentRoute == RecipeRoutes.Settings.route,
                    onClick = {
                        navController.navigate(RecipeRoutes.Settings.route) {
                            popUpTo(RecipeRoutes.Home.route)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = RecipeRoutes.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(RecipeRoutes.Home.route) {
                HomeScreen(
                    recipes = uiState.recipes,
                    onRecipeClick = { recipeId ->
                        navController.navigate(RecipeRoutes.Detail.createRoute(recipeId)) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(
                route = RecipeRoutes.Detail.route,
                arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
                val recipe = viewModel.getRecipe(recipeId)
                if (recipe != null) {
                    DetailScreen(
                        recipe = recipe,
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Recipe not found")
                    }
                }
            }
            composable(RecipeRoutes.AddRecipe.route) {
                AddRecipeScreen(
                    onSaveRecipe = { recipe ->
                        viewModel.addRecipe(recipe)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(RecipeRoutes.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun HomeScreen(
    recipes: List<Recipe>,
    onRecipeClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Recipes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (recipes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No recipes yet. Add your first recipe!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recipes) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${recipe.ingredients.size} ingredients • ${recipe.steps.size} steps",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DetailScreen(
    recipe: Recipe,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = recipe.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Text(
            text = "Ingredients",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        recipe.ingredients.forEach { ingredient ->
            Text(
                text = "• $ingredient",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Steps",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        recipe.steps.forEachIndexed { index, step ->
            Text(
                text = "${index + 1}. $step",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
fun AddRecipeScreen(
    onSaveRecipe: (Recipe) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var ingredientsText by remember { mutableStateOf("") }
    var stepsText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add New Recipe",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Recipe Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = ingredientsText,
            onValueChange = { ingredientsText = it },
            label = { Text("Ingredients (one per line)") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            minLines = 5
        )

        OutlinedTextField(
            value = stepsText,
            onValueChange = { stepsText = it },
            label = { Text("Steps (one per line)") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            minLines = 5
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    val ingredients = ingredientsText.split("\n").filter { it.isNotBlank() }
                    val steps = stepsText.split("\n").filter { it.isNotBlank() }
                    if (title.isNotBlank() && ingredients.isNotEmpty() && steps.isNotEmpty()) {
                        onSaveRecipe(
                            Recipe(
                                id = UUID.randomUUID().toString(),
                                title = title,
                                ingredients = ingredients,
                                steps = steps
                            )
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = title.isNotBlank() && ingredientsText.isNotBlank() && stepsText.isNotBlank()
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

