package com.example.hoteailai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hoteailai.domain.model.Offer

@Entity(tableName = "offers")
data class OfferEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val promoCode: String,
    val discountPercentage: Int,
    val iconUrl: String,
    val bannerUrl: String
)

fun OfferEntity.toDomain() = Offer(
    id = id,
    title = title,
    description = description,
    promoCode = promoCode,
    discountPercentage = discountPercentage,
    iconUrl = iconUrl,
    bannerUrl = bannerUrl
)

fun Offer.toEntity() = OfferEntity(
    id = id,
    title = title,
    description = description,
    promoCode = promoCode,
    discountPercentage = discountPercentage,
    iconUrl = iconUrl,
    bannerUrl = bannerUrl
)
