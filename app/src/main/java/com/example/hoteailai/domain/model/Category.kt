package com.example.hoteailai.domain.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class Category(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("iconUrl") @set:PropertyName("iconUrl") var iconUrl: String = "",
    @get:PropertyName("lottieResId") @set:PropertyName("lottieResId") var lottieResId: Int? = null
)

@IgnoreExtraProperties
data class Offer(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("title") @set:PropertyName("title") var title: String = "",
    @get:PropertyName("description") @set:PropertyName("description") var description: String = "",
    @get:PropertyName("promoCode") @set:PropertyName("promoCode") var promoCode: String = "",
    @get:PropertyName("discountPercentage") @set:PropertyName("discountPercentage") var discountPercentage: Int = 0,
    @get:PropertyName("iconUrl") @set:PropertyName("iconUrl") var iconUrl: String = "",
    @get:PropertyName("bannerUrl") @set:PropertyName("bannerUrl") var bannerUrl: String = ""
)
