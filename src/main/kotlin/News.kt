import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class News(
    val id: Long,
    val title: String,
    val place: String? = null,
    val description: String? = null,
    @SerialName("site_url") val siteUrl: String? = null,
    @SerialName("favorites_count") val favoritesCount: Int,
    @SerialName("comments_count") val commentsCount: Int,
    @SerialName("publication_date") val publicationDate: Long
)