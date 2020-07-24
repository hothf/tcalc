package de.ka.jamit.tcalc.repo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider

/**
 *
 * A implementation of a database featuring a room database.
 *
 * Created by Thomas Hofmann on 14.07.20
 **/
@Database(
        entities = [User::class, Record::class],
        version = 1,
        exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun databaseDao(): AppDatabaseDao

    companion object {
        /**
         * Retrieves a default user, usable for insertion only.
         */
        fun getDefaultUser() = User(id = 0, name = "default", selected = true)

        /**
         * All the master data which should only be inserted once into the database.
         * Please do this after putting the default user, as this data references the first
         * inserted user, usable for insertion only.
         */
        fun getMasterData(userId: Int): List<Record> {
            return listOf(
                    Record(id = 0, key = "firstVal", value = 0.0f, userId = userId),
                    Record(id = 0, key = "secondVal", value = 0.0f, userId = userId),
                    Record(id = 0, key = "thirdVal", value = 0.0f, userId = userId),
                    Record(id = 0, key = "fourthVal", value = 0.0f, userId = userId),
                    Record(id = 0, key = "fifthVal", value = 0.0f, userId = userId),
                    Record(id = 0, key = "sixthVal", value = 0.0f, userId = userId),
                    Record(id = 0, key = "seventhVal", value = 0.0f, userId = userId)
            )
        }

        private val translatedKeyMap = mapOf(
                "firstVal" to R.string.dummy_val_one,
                "secondVal" to R.string.dummy_val_two,
                "thirdVal" to R.string.dummy_val_three,
                "fourthVal" to R.string.dummy_val_four,
                "fifthVal" to R.string.dummy_val_five,
                "sixthVal" to R.string.dummy_val_six,
                "seventhVal" to R.string.dummy_val_seven
        )

        /**
         * Retrieve the translated string for the given key, if there is none the key is returned.
         */
        fun getTranslatedStringForKey(resourcesProvider: ResourcesProvider, key: String): String {
            return translatedKeyMap[key]?.let(resourcesProvider::getString) ?: key
        }
    }
}