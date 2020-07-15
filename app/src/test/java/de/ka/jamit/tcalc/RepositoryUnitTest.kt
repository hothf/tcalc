package de.ka.jamit.tcalc

import de.ka.jamit.tcalc.base.InjectedAppTest
import de.ka.jamit.tcalc.repo.Repository
import de.ka.jamit.tcalc.repo.db.RecordDao
import de.ka.jamit.tcalc.repo.db.UserDao
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Observable
import org.junit.*
import org.koin.test.inject

/**
 * A repository and database management unit test.
 *
 * Created by Thomas Hofmann on 14.07.20
 **/
class RepositoryUnitTest : InjectedAppTest() {

    private val repository: Repository by inject()

    @RelaxedMockK
    lateinit var mockRepository: Repository

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
                RecordDao(id = 2L, key = "secondVal", userId = defaultUserId),
                RecordDao(id = 3L, key = "thirdVal", userId = defaultUserId),
                RecordDao(id = 4L, key = "fourthVal", userId = defaultUserId),
                RecordDao(id = 5L, key = "fifthVal", userId = defaultUserId),
                RecordDao(id = 6L, key = "sixthVal", userId = defaultUserId),
                RecordDao(id = 7L, key = "seventhVal", userId = defaultUserId)
        )
        every { mockRepository.observeRecords() } returns Observable.just(dummyRecords)

        // when
        val testList = repository.observeRecords()
                .test()
                .awaitCount(1)
                .assertValueCount(1)
        val dummyValues = mockRepository.observeRecords().blockingSingle()

        // then
        testList.values()[0].forEachIndexed { index, item ->
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
                RecordDao(id = 2L, key = "secondVal", userId = defaultUserId),
                RecordDao(id = 3L, key = "thirdVal", userId = defaultUserId),
                RecordDao(id = 4L, key = "fourthVal", userId = defaultUserId),
                RecordDao(id = 5L, key = "fifthVal", userId = defaultUserId),
                RecordDao(id = 6L, key = "sixthVal", userId = defaultUserId),
                RecordDao(id = 7L, key = "seventhVal", userId = defaultUserId),
                RecordDao(id = 8L, key = "New", userId = defaultUserId))
        every { mockRepository.observeRecords() } returns Observable.just(dummyRecords)
        val dummyValues = mockRepository.observeRecords().blockingSingle().toMutableList()
        val testList = repository.observeRecords().test().awaitCount(1)
        testList.assertValueCount(1)

        // adding a record
        // when
        repository.addRecord("New", isConsidered = true, isIncome = false)

        // then
        var lastId = 1L
        val resultAfterAdd = testList.awaitCount(2).values()[1]
        resultAfterAdd.forEachIndexed { index, item -> // we check like this because ids may be different internally
            Assert.assertEquals(dummyValues[index].key, item.key)
            lastId = item.id
        }

        // deleting a record
        // when
        repository.deleteRecord(lastId)

        // then
        val resultAfterDelete = testList.awaitCount(3).values()[2]
        val lastDeletedItem = dummyValues[dummyValues.size -1]
        dummyValues.remove(lastDeletedItem)
        resultAfterDelete.forEachIndexed { index, item ->
            Assert.assertEquals(dummyValues[index].key, item.key)
            lastId = item.id
        }
        Assert.assertTrue(resultAfterDelete.size == 7)

        // undo deletion of a record
        // when
        repository.undoDeleteLastRecord()

        // then
        val resultAfterUndoDelete = testList.awaitCount(4).values()[3]
        dummyValues.add(dummyValues.size, lastDeletedItem)
        resultAfterUndoDelete.forEachIndexed { index, item ->
            Assert.assertEquals(dummyValues[index].key, item.key)
            lastId = item.id
        }
        Assert.assertTrue(resultAfterUndoDelete.size == 8)

        // updating a record
        // when
        repository.updateRecord(value = 2.0f,
                id = lastId,
                key = "Updated",
                timeSpan = RecordDao.TimeSpan.QUARTERLY,
                category = RecordDao.Category.HOUSE,
                isIncome = true,
                isConsidered = true)

        //then
        val resultAfterUpdate = testList.awaitCount(5).values()[4]
        val updatedItem = RecordDao(
                id = lastId,
                key = "Updated",
                value = 2.0f,
                timeSpan = RecordDao.TimeSpan.QUARTERLY,
                category = RecordDao.Category.HOUSE,
                isIncome = true,
                isConsidered = true,
                userId = defaultUserId)
        dummyValues.removeAt(dummyValues.size - 1)
        dummyValues.add(dummyValues.size, updatedItem)
        resultAfterUpdate.forEachIndexed { index, item ->
            Assert.assertEquals(dummyValues[index].key, item.key)
        }

        // getting all records of the currently selected user
        // when
        val records = repository.getAllRecordsOfCurrentlySelectedUser()

        // then
        Assert.assertEquals(dummyValues, records)

        // adding multiple records at once
        // when
        val newRecords = listOf(
                RecordDao(id = 0, key = "NewVal", userId = defaultUserId),
                RecordDao(id = 0, key = "EvenNewerVal", userId = defaultUserId)
        )
        dummyValues.addAll(newRecords)
        repository.addRecords(newRecords)

        // then
        val resultAfterBunkAdd = testList.awaitCount(6).values()[5]
        resultAfterBunkAdd.forEachIndexed { index, item ->
            Assert.assertEquals(dummyValues[index].key, item.key)
        }

        testList.dispose()
    }
}