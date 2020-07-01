package de.ka.jamit.tcalc.repo.db

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class PeopleDao(
        @Id var id: Long = 142547635L,
        val name: String,
        val height: String? = null,
        val mass: String? = null,
        val hair_color: String? = null,
        val skin_color: String? = null,
        val eye_color: String? = null,
        val gender: String? = null)
