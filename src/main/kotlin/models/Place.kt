package models

import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val title: String,
    val location: String,
    val address: String,
    val phone: String
)
