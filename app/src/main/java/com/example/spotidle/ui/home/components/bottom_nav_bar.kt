package com.example.spotidle.ui.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(
    items: List<String>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier.offset(y = 16.dp),
        containerColor = Color.Transparent,
    ) {
        items.forEachIndexed { index, item ->
            val icon = when (index) {
                0 -> Icons.Default.Home
                1 -> Icons.Default.Mic
                2 -> Icons.Default.Speaker
                3 -> Icons.Default.Album
                4 -> Icons.Default.Man
                else -> Icons.Default.Home
            }

            NavigationBarItem(
                modifier = Modifier.height(48.dp).padding(8.dp),
                icon = { Icon(imageVector = icon, contentDescription = item) },
                selected = selectedItem == index,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color(0xFF1ED760)
                )
            )
        }
    }
}