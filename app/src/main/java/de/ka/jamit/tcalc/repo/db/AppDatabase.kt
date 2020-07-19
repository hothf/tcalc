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