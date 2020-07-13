package de.ka.jamit.tcalc.repo.db

import android.app.Application
import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import io.objectbox.BoxStore

/**
 * A object box database.
 */
class AppDatabase(private val application: Application) {

    private val db: BoxStore by lazy { MyObjectBox.builder().androidContext(application.applicationContext).build() }

    /**
     * Retrieve the object box.
     */
    fun get() = db

    /**
     * Retrieves a default user, usable for insertion only.
     */
    fun getDefaultUser() = UserDao(id = 0, name = "default", selected = true)

    /**
     * All the master data which should only be inserted once into the database.
     * Please do this after putting the default user, as this data references the first
     * inserted user, usable for insertion only.
     */
    fun getMasterData(userId: Long): List<RecordDao> {
        return listOf(
                RecordDao(id = 0, key = "firstVal", value = 0.0f, userId = userId),
                RecordDao(id = 0, key = "secondVal", value = 0.0f, userId = userId),
                RecordDao(id = 0, key = "thirdVal", value = 0.0f, userId = userId),
                RecordDao(id = 0, key = "fourthVal", value = 0.0f, userId = userId),
                RecordDao(id = 0, key = "fifthVal", value = 0.0f, userId = userId),
                RecordDao(id = 0, key = "sixthVal", value = 0.0f, userId = userId),
                RecordDao(id = 0, key = "seventhVal", value = 0.0f, userId = userId)
        )
    }


    companion object {
        private val translatedKeyMap = mapOf(
                "firstVal" to R.string.app_name,
                "secondVal" to R.string.app_name,
                "thirdVal" to R.string.app_name,
                "fourthVal" to R.string.app_name,
                "fifthVal" to R.string.app_name,
                "sixthVal" to R.string.app_name,
                "seventhVal" to R.string.app_name
        )

        /**
         * Retrieve the translated string for the given key, if there is none the key is returned.
         */
        fun getTranslatedStringForKey(resourcesProvider: ResourcesProvider, key: String): String {
            return translatedKeyMap[key]?.let(resourcesProvider::getString) ?: key
        }
    }
}