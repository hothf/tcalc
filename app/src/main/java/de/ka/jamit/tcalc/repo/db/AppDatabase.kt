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
     * Retrieves a default user.
     */
    val defaultUser = UserDao(id= 0, name = "default", selected = true)

    /**
     * All the master data which should only be inserted once into the database.
     */
    val masterData = listOf(
            RecordDao(id = 0, key = "firstVal", value = 0.0f, userId = 1L),
            RecordDao(id = 0, key = "secondVal", value = 0.0f, userId = 1L),
            RecordDao(id = 0, key = "thirdVal", value = 0.0f, userId = 1L),
            RecordDao(id = 0, key = "fourthVal", value = 0.0f, userId = 2L),
            RecordDao(id = 0, key = "fifthVal", value = 0.0f, userId = 3L),
            RecordDao(id = 0, key = "sixthVal", value = 0.0f, userId = 3L),
            RecordDao(id = 0, key = "seventhVal", value = 0.0f, userId = 1L)
    )


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