package de.ka.jamit.tcalc.repo.db

import de.ka.jamit.tcalc.R
import de.ka.jamit.tcalc.utils.resources.ResourcesProvider
import io.objectbox.BoxStore


/**
 * A app database
 */
interface AppDatabase {

    fun get(): BoxStore

    fun getDefaultUser(): UserDao

    fun getMasterData(userId: Long): List<RecordDao>

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