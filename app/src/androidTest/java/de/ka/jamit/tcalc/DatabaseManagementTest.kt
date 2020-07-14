package de.ka.jamit.tcalc

import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.RepositoryImpl
import de.ka.jamit.tcalc.repo.db.AppDatabase
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.repo.db.UserDao
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.internal.schedulers.TrampolineScheduler
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.TestSubscriber
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.KoinComponent
import org.koin.core.inject


/**
 * Test suite for database management.
 *
 * This is an instrumentation test as this should use the real dependency injections.
 *
 * Created by Thomas Hofmann on 13.07.20
 **/
@RunWith(AndroidJUnit4::class)
class DatabaseManagementTest : KoinComponent {

    private val repository: Repository by inject()

    @MockK
    lateinit var dummyRepository: Repository

    @Before
    fun setupDatabase() {
        repository.wipeDatabase()
    }

    @Before
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    fun database_shouldHaveDefaultValues_whenInitialized() {
        // is the default user set? And does it have the default records?
        Assert.assertEquals("There should only be the default user with name=default", "default", repository.getCurrentlySelectedUser().name)
        Assert.assertEquals("There should only be 7 records of the default user", 7, repository.getAllRecordsOfCurrentlySelectedUser().size)

        repository.wipeDatabase()

        // is the default user with the default records still set?
        Assert.assertEquals("There should only be the default user with name=default", "default", repository.getCurrentlySelectedUser().name)
        Assert.assertEquals("There should only be 7 records of the default user", 7, repository.getAllRecordsOfCurrentlySelectedUser().size)

        // check if the observing of user count is as expected 1 (only the default user) and the
        // records match the expectation
        val defaultUserId = repository.getCurrentlySelectedUser().id
        val dummyUser = UserDao(defaultUserId, "default", true)
        every { dummyRepository.observeUsers() } returns Observable.just(listOf(dummyUser))
        repository.observeUsers() // short for: TestObserver<List<UserDao>> =
                .test()
                .assertValues(dummyRepository.observeUsers().blockingSingle())
                .dispose()

        val dummyRecords = listOf(
                RecordDao(id = 1L, key = "firstVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "secondVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "thirdVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "fourthVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "fifthVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "sixthVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "seventhVal", userId = defaultUserId)
        )
        every { dummyRepository.observeRecords() } returns Observable.just(dummyRecords)

        val testList = repository.observeRecords()
                .test()
                .awaitCount(1)
                .assertValueCount(1)

        val dummyValues = dummyRepository.observeRecords().blockingSingle()

        val result = testList.values()
        result[0].forEachIndexed { index, item ->
            Assert.assertEquals(dummyValues[index].key, item.key)
        }
        testList.dispose()
    }

    @Test
    fun database_shouldUserManagementCorrect_whenAdding(){

        val userList = repository.observeUsers().test()

        repository.addUser("New Testuser")
    }

}