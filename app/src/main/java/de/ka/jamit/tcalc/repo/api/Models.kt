package de.ka.jamit.tcalc.repo.api

import androidx.annotation.Keep
import okhttp3.HttpUrl
import org.threeten.bp.Instant

@Keep
data class BasePeople(val results: List<People>?)

@Keep
data class People(
        val name: String,
        val height: String?,
        val mass: String?,
        val hairColor: String?,
        val skinColor: String?,
        val eyeColor: String?,
        val gender: String?,
        val url: HttpUrl? = null,
        val created: Instant? = null,
        val edited: Instant? = null
)
