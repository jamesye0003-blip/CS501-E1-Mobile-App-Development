package com.example.assignment5.ui

data class Location(
    val id: Int,
    val name: String,
    val description: String,
    val address: String,
    val category: String
)

data class Category(
    val id: String,
    val name: String,
    val icon: String
)

class BostonTourViewModel {
    val categories = listOf(
        Category("museums", "Museums", "museum"),
        Category("parks", "Parks", "park"),
        Category("restaurants", "Restaurants", "restaurant")
    )

    val locations = listOf(
        // Museums
        Location(1, "MIT Museum", "Explore innovation and technology at the MIT Museum.", "265 Massachusetts Ave, Cambridge, MA", "museums"),
        Location(2, "Museum of Fine Arts", "One of the most comprehensive art museums in the world.", "465 Huntington Ave, Boston, MA", "museums"),
        Location(3, "Museum of Science", "Interactive exhibits and planetarium shows.", "1 Science Park, Boston, MA", "museums"),
        
        // Parks
        Location(4, "Boston Common", "The oldest city park in the United States.", "139 Tremont St, Boston, MA", "parks"),
        Location(5, "Public Garden", "Beautiful gardens and the famous Swan Boats.", "4 Charles St, Boston, MA", "parks"),
        Location(6, "Charles River Esplanade", "Scenic park along the Charles River.", "Charles River, Boston, MA", "parks"),
        
        // Restaurants
        Location(7, "Neptune Oyster", "Fresh seafood in the North End.", "63 Salem St, Boston, MA", "restaurants"),
        Location(8, "Union Oyster House", "America's oldest restaurant.", "41 Union St, Boston, MA", "restaurants"),
        Location(9, "No. 9 Park", "Fine dining with French-Italian cuisine.", "9 Park St, Boston, MA", "restaurants")
    )

    fun getLocationsByCategory(categoryId: String): List<Location> {
        return locations.filter { it.category == categoryId }
    }

    fun getLocationById(id: Int): Location? {
        return locations.find { it.id == id }
    }
}

