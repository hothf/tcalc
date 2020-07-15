package de.ka.jamit.tcalc.base

import android.app.Application
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.RepositoryImpl
import de.ka.jamit.tcalc.repo.db.AppDatabase
import de.ka.jamit.tcalc.repo.db.MyObjectBox
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.repo.db.UserDao
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.mockkClass
import io.objectbox.BoxStore
import io.objectbox.DebugFlags
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.mock.MockProviderRule
import java.io.File


/**
 * An injected app test to simplify unit testing of all injected components of the app inclusive
 * database work.
 *
 * Mainly copied from https://docs.objectbox.io/android/android-local-unit-tests#android-local-unit-tests
 * for injection and database operations.
 *
 * By Thomas Hofmann on 14.07.20
 **/
open class InjectedAppTest: KoinTest {

    var db: BoxStore? = null
    private val testDirectory: File = File("objectbox-example/test-db")

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz)
    }

    @Before
    @Throws(Exception::class)
    open fun setUp() {
        // delete database files before each test to start with a clean database
        MockKAnnotations.init(this)
        BoxStore.deleteAllFiles(testDirectory)
        db = MyObjectBox.builder() // add directory flag to change where ObjectBox puts its database files
                .directory(testDirectory) // optional: add debug flags for more detailed ObjectBox log output
                .debugFlags(DebugFlags.LOG_QUERIES or DebugFlags.LOG_QUERY_PARAMETERS)
                .build()

        db?.let {
            val testDatabase = TestDatabase(it)
            val mockkApp = mockk<Application>(relaxed = true)
            startKoin {
                modules(
                        module {
                            androidContext(mockkApp)
                            single { RepositoryImpl(testDatabase) as Repository }
                        })
            }
        }
    }

    @After
    @Throws(Exception::class)
    open fun tearDown() {
        if (db != null) {
            db!!.close()
            db = null
        }
        BoxStore.deleteAllFiles(testDirectory)
        stopKoin()
    }

}

/**
 * A test database.
 */
class TestDatabase(private val db: BoxStore) : AppDatabase {
    override fun get(): BoxStore {
        return db
    }

    override fun getDefaultUser(): UserDao {
        return UserDao(0, "default", true)
    }

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