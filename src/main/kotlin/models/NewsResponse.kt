package models

import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<News>
)