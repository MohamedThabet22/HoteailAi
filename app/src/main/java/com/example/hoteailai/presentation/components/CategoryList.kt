package com.example.hoteailai.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.scale
import coil.compose.AsyncImage
import com.example.hoteailai.domain.model.Category
import com.example.hoteailai.ui.theme.PrimaryBlue

@Composable
fun CategoryList(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategoryClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { category ->
            CategoryItem(
                category = category,
                isSelected = category.id == selectedCategoryId,
                onClick = { onCategoryClick(category.id) }
            )
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        if (isSelected) PrimaryBlue else Color.White,
        label = "bg"
    )
    val contentColor by animateColorAsState(
        if (isSelected) Color.White else PrimaryBlue,
        label = "content"
    )
    val scale by animateFloatAsState(
        if (isSelected) 1.15f else 1f,
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(85.dp).scale(scale)
    ) {
        Surface(
            modifier = Modifier
                .size(70.dp)
                .shadow(
                    elevation = if (isSelected) 10.dp else 4.dp,
                    shape = RoundedCornerShape(22.dp),
                    spotColor = PrimaryBlue.copy(alpha = 0.2f)
                ),
            shape = RoundedCornerShape(22.dp),
            color = backgroundColor
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = category.iconUrl,
                    contentDescription = category.name,
                    modifier = Modifier.size(32.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(contentColor)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) PrimaryBlue else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 10.sp,
            maxLines = 1
        )
    }
}
