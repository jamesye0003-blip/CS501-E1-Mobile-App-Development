package com.example.assignment5.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Recipe(
    val id: String,
    val title: String,
    val ingredients: List<String>,
    val steps: List<String>
)

data class RecipeUiState(
    val recipes: List<Recipe> = emptyList()
)

class RecipeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    init {
        // Initialize with some sample recipes
        _uiState.value = RecipeUiState(
            recipes = listOf(
                Recipe(
                    id = "1",
                    title = "Pasta Carbonara",
                    ingredients = listOf("200g spaghetti", "100g pancetta", "2 eggs", "50g parmesan", "Black pepper"),
                    steps = listOf(
                        "Cook pasta in salted water",
                        "Fry pancetta until crispy",
                        "Mix eggs and parmesan",
                        "Combine everything while pasta is hot",
                        "Serve with black pepper"
                    )
                ),
                Recipe(
                    id = "2",
                    title = "Chocolate Chip Cookies",
                    ingredients = listOf("225g butter", "150g sugar", "300g flour", "200g chocolate chips", "1 egg"),
                    steps = listOf(
                        "Cream butter and sugar",
                        "Add egg and mix",
                        "Fold in flour and chocolate chips",
                        "Bake at 180°C for 12 minutes"
                    )
                )
            )
        )
    }

    fun addRecipe(recipe: Recipe) {
        _uiState.value = _uiState.value.copy(
            recipes = _uiState.value.recipes + recipe
        )
    }

    fun getRecipe(id: String): Recipe? {
        return _uiState.value.recipes.find { it.id == id }
    }
}

