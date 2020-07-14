package de.ka.jamit.tcalc

import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.repo.db.UserDao
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Observable
import org.junit.*
import org.koin.test.inject

/**
 * A database management unit test.
 *
 * Created by Thomas Hofmann on 14.07.20
 **/
class DatabaseManagementUnitTest : InjectedAppTest() {

    private val repository: Repository by inject()

    @RelaxedMockK
    lateinit var mockRepository: Repository

    @Before
    override fun setUp() {
        super.setUp()

    }

    @Test
    fun `database should have default values and records`() {
        // is the default user set? And does it have the default records?
        Assert.assertEquals("There should only be the default user with name=default", "default", repository.getCurrentlySelectedUser().name)
        Assert.assertEquals("There should only be 7 records of the default user", 7, repository.getAllRecordsOfCurrentlySelectedUser().size)

        repository.wipeDatabase()

        // is the default user with the default records still set?
        Assert.assertEquals("There should only be the default user with name=default", "default", repository.getCurrentlySelectedUser().name)
        Assert.assertEquals("There should only be 7 records of the default user", 7, repository.getAllRecordsOfCurrentlySelectedUser().size)

        // check if the observing of user count is as expected 1 (only the default user) and the
        // records match the expectation
        // given
        val defaultUserId = repository.getCurrentlySelectedUser().id
        val dummyUser = UserDao(defaultUserId, "default", true)
        every { mockRepository.observeUsers() } returns Observable.just(listOf(dummyUser))

        // when
        repository.observeUsers() // short for: TestObserver<List<UserDao>> =
                .test()
                .assertValues(mockRepository.observeUsers().blockingSingle()) // then
                .dispose()

        // now we check if the records of that user are also correct
        // given
        val dummyRecords = listOf(
                RecordDao(id = 1L, key = "firstVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "secondVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "thirdVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "fourthVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "fifthVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "sixthVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "seventhVal", userId = defaultUserId)
        )
        every { mockRepository.observeRecords() } returns Observable.just(dummyRecords)

        // when
        val testList = repository.observeRecords()
                .test()
                .awaitCount(1)
                .assertValueCount(1)
        val dummyValues = mockRepository.observeRecords().blockingSingle()

        // then
        val result = testList.values()
        result[0].forEachIndexed { index, item ->
            Assert.assertEquals(dummyValues[index].key, item.key)
        }
        testList.dispose()
    }

    @Test
    fun `database should handle record manipulations`() {
        //given
        val defaultUserId = repository.getCurrentlySelectedUser().id
        val dummyRecords = listOf(
                RecordDao(id = 1L, key = "firstVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "secondVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "thirdVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "fourthVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "fifthVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "sixthVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "seventhVal", userId = defaultUserId),
                RecordDao(id = 1L, key = "Hello", userId = defaultUserId))
        every { mockRepository.observeRecords() } returns Observable.just(dummyRecords)
        val dummyValues = mockRepository.observeRecords().blockingSingle()
        val testList = repository.observeRecords().test().awaitCount(1)
        testList.assertValueCount(1)

        // when
        repository.addRecord("Hello", isConsidered = true, isIncome = false)

        // then
        val result = testList.awaitCount(2).values()
        result[1].forEachIndexed { index, item ->
            Assert.assertEquals(dummyValues[index].key, item.key)
        }
    }
}