package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.exp
import kotlin.math.round

@Serializable
data class News(
    val id: Long,
    val title: String,
    val place: Place? = null,
    val description: String? = null,
    @SerialName("site_url") val siteUrl: String? = null,
    @SerialName("favorites_count") val favoritesCount: Int,
    @SerialName("comments_count") val commentsCount: Int,
    @SerialName("publication_date") val publicationDate: Long
) {
    val rating: Double by lazy {
        val rawRating = 1.0 / (1 + exp(-(favoritesCount / (commentsCount + 1).toDouble())))
        round(rawRating * 100) / 100
    }
}