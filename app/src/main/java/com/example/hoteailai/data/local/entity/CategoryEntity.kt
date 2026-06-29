package com.example.hoteailai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hoteailai.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconUrl: String
)

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    iconUrl = iconUrl
)

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    iconUrl = iconUrl
)
