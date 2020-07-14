package de.ka.jamit.tcalc.repo.db

import android.app.Application
import io.objectbox.BoxStore

/**
 *
 * A implementation of a database featuring a object box database.
 *
 * Created by Thomas Hofmann on 14.07.20
 **/
class AppDatabaseImpl(private val application: Application) : AppDatabase {
    private val db: BoxStore by lazy { MyObjectBox.builder().androidContext(application.applicationContext).build() }

    /**
     * Retrieve the object box.
     */
    override fun get() = db

    /**
     * Retrieves a default user, usable for insertion only.
     */
    override fun getDefaultUser() = UserDao(id = 0, name = "default", selected = true)

    /**
     * All the master data which should only be inserted once into the database.
     * Please do this after putting the default user, as this data references the first
     * inserted user, usable for insertion only.
     */
    override fun getMasterData(userId: Long): List<RecordDao> {
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
}