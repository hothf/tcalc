package de.ka.jamit.tcalc.repo.db

import androidx.annotation.Keep
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter

@Keep
@Entity
data class RecordDao(
        @Id var id: Long = 142547635L,
        val key: String,
        val value: Float = 0.0f,
        @Convert(converter = TimeSpanConverter::class, dbType = Int::class)
        val timeSpan: TimeSpan = TimeSpan.MONTHLY,
        val userId: Long) {

    enum class TimeSpan(val id: Int) {
        MONTHLY(0), QUARTERLY(1), YEARLY(2);
    }

    class TimeSpanConverter : PropertyConverter<TimeSpan, Int> {
        override fun convertToDatabaseValue(entityProperty: TimeSpan?): Int {
            return entityProperty?.id ?: 0
        }

        override fun convertToEntityProperty(databaseValue: Int?): TimeSpan {
            val entry = TimeSpan.values().find { it.id == databaseValue }
            return entry ?: TimeSpan.MONTHLY
        }
    }
}

// TODO add categories

// TODO add synchronization

@Keep
@Entity
data class UserDao(
        @Id var id: Long = 122547635L,
        val name: String = "",
        val selected: Boolean = false
)



