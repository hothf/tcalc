package de.ka.jamit.tcalc.repo.db

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class RecordDao(
        @Id var id: Long = 142547635L,
        val key: String,
        val value: Float = 0.0f)
