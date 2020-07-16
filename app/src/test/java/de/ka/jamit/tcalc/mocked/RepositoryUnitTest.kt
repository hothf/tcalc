package de.ka.jamit.tcalc.mocked

import de.ka.jamit.tcalc.mocked.base.InjectedAppTest
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
    fun `repository should have default values and records`() {
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
        every { mockRepository.observeRecordsOfCurrentlySelected() } returns Observable.just(dummyRecords)

        // when
        val testList = repository.observeRecordsOfCurrentlySelected()
                .test()
                .awaitCount(1)
                .assertValueCount(1)
        val dummyValues = mockRepository.observeRecordsOfCurrentlySelected().blockingSingle()

        // then
        testList.values()[0].forEachIndexed { index, item ->
            Assert.assertEquals(dummyValues[index].key, item.key)
        }
        testList.dispose()
    }

    @Test
    fun `repository should handle record manipulations`() {
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
        every { mockRepository.observeRecordsOfCurrentlySelected() } returns Observable.just(dummyRecords)
        val dummyValues = mockRepository.observeRecordsOfCurrentlySelected().blockingSingle().toMutableList()
        val testObserver = repository.observeRecordsOfCurrentlySelected().test().awaitCount(1)
        testObserver.assertValueCount(1)

        // adding a record
        // when
        repository.addRecord("New", isConsidered = true, isIncome = false)

        // then
        var lastId = 1L
        val resultAfterAdd = testObserver.awaitCount(2).values()[1]
        resultAfterAdd.forEachIndexed { index, item -> // we check like this because ids may be different internally
            Assert.assertEquals(dummyValues[index].key, item.key)
            lastId = item.id
        }

        // deleting a record
        // when
        repository.deleteRecord(lastId)

        // then
        val resultAfterDelete = testObserver.awaitCount(3).values()[2]
        val lastDeletedItem = dummyValues[dummyValues.size - 1]
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
        val resultAfterUndoDelete = testObserver.awaitCount(4).values()[3]
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
        val resultAfterUpdate = testObserver.awaitCount(5).values()[4]
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
        val resultAfterBunkAdd = testObserver.awaitCount(6).values()[5]
        resultAfterBunkAdd.forEachIndexed { index, item ->
            Assert.assertEquals(dummyValues[index].key, item.key)
        }

        testObserver.dispose()
    }

    @Test
    fun `repository should handle user manipulations`() {
        //given
        val users = listOf(UserDao(id = 1L, name = "default", selected = true))
        every { mockRepository.observeUsers() } returns Observable.just(users)

        // observe users
        // when, then
        val dummyUsers = mockRepository.observeUsers().blockingSingle().toMutableList()
        val testObserver = repository
                .observeUsers()
                .test()
                .awaitCount(1)
                .assertValueCount(1)
                .assertValue(dummyUsers)

        // adding a user
        // when
        repository.addUser("NewUser")

        // then
        var lastId = 1L
        dummyUsers.add(UserDao(id = 2L, name = "NewUser", selected = false))
        val resultAfterAdd = testObserver.awaitCount(4).values()[3]
        resultAfterAdd.forEachIndexed { index, item -> // we check like this because ids may be different internally
            Assert.assertEquals(dummyUsers[index].name, item.name)
            lastId = item.id
        }
        // add records to this user, needed for later tests
        val dummyRecords = listOf(RecordDao(id = 0, userId = lastId), RecordDao(id = 0, userId = lastId))
        repository.addRecords(dummyRecords)
        // should auto select the new user
        Assert.assertFalse(resultAfterAdd[0].selected)
        Assert.assertTrue(resultAfterAdd[1].selected)
        Assert.assertEquals(2, repository.getAllRecordsOfCurrentlySelectedUser().size)

        // deleting a user
        // when
        repository.deleteUser(lastId)

        // then
        val lastDeletedItem = dummyUsers[dummyUsers.size - 1]
        dummyUsers.remove(lastDeletedItem)
        val resultAfterDelete = testObserver.awaitCount(7).values()[6]
        resultAfterDelete.forEachIndexed { index, item ->
            Assert.assertEquals(dummyUsers[index].name, item.name)
            lastId = item.id
        }
        Assert.assertTrue(resultAfterDelete.size == 1) // should auto select the default user
        Assert.assertTrue(resultAfterDelete[0].selected)

        // check if records of the user have been deleted (in this case only default records)
        // when, then
        val recordsObserver = repository.observeRecordsOfCurrentlySelected().test().awaitCount(1)
        Assert.assertTrue(recordsObserver.values()[0].size == 7)
        recordsObserver.dispose()

        // undo deleting a user
        // when
        repository.undoDeleteLastUser()

        // then
        dummyUsers.add(dummyUsers.size, lastDeletedItem)
        val resultAfterUndoDelete = testObserver.awaitCount(8).values()[7]
        resultAfterUndoDelete.forEachIndexed { index, item ->
            Assert.assertEquals(dummyUsers[index].name, item.name)
            lastId = item.id
        }
        Assert.assertTrue(resultAfterUndoDelete.size == 2)

        // update a user
        // when
        repository.updateUser(lastId, "Update")

        // then
        val resultAfterUpdate = testObserver.awaitCount(9).values()[8]
        Assert.assertEquals("Update", resultAfterUpdate[1].name)

        // select a user
        // when
        repository.selectUser(lastId)

        val resultAfterSelect = testObserver.awaitCount(11).values()[10]
        Assert.assertTrue(resultAfterSelect[1].selected)

        // check if records of the deleted user have been restored
        // this should be now possible as the selected user has changed
        val recordObserver = repository.observeRecordsOfCurrentlySelected().test().awaitCount(1)
        Assert.assertTrue(recordObserver.values()[0].size == 2)
        recordObserver.dispose()

        // delete the last user and check if the default user is checked again
        // when
        repository.deleteUser(lastId)

        // then
        val resultAfterSelectAndDelete = testObserver.awaitCount(14).values()[12]
        Assert.assertTrue(resultAfterSelectAndDelete[0].selected)

        testObserver.dispose()
    }


    @Test
    fun `repository should calculate correctly`() {
        //given
        val data = mutableListOf(
                RecordDao(value = 1.0f, timeSpan = RecordDao.TimeSpan.MONTHLY, userId = 1L),
                RecordDao(value = 1.0f, timeSpan = RecordDao.TimeSpan.MONTHLY, userId = 1L))

        // when
        var testObserver = repository.calc(data).test().awaitCount(1)

        //then
        Assert.assertEquals(2.0f, testObserver.values()[0].monthlyOutput)
        Assert.assertEquals(0.0f, testObserver.values()[0].monthlyIncome)
        Assert.assertEquals(-2.0f, testObserver.values()[0].monthlyDifference)
        Assert.assertEquals(24.0f, testObserver.values()[0].yearlyOutput)
        Assert.assertEquals(0.0f, testObserver.values()[0].yearlyIncome)
        Assert.assertEquals(-24.0f, testObserver.values()[0].yearlyDifference)

        // given
        data.add(RecordDao(value = 1.0f, timeSpan = RecordDao.TimeSpan.MONTHLY, isIncome = true, userId = 1L))

        // when
        testObserver = repository.calc(data).test().awaitCount(1)

        //then
        Assert.assertEquals(2.0f, testObserver.values()[0].monthlyOutput)
        Assert.assertEquals(1.0f, testObserver.values()[0].monthlyIncome)
        Assert.assertEquals(-1.0f, testObserver.values()[0].monthlyDifference)
        Assert.assertEquals(24.0f, testObserver.values()[0].yearlyOutput)
        Assert.assertEquals(12.0f, testObserver.values()[0].yearlyIncome)
        Assert.assertEquals(-12.0f, testObserver.values()[0].yearlyDifference)

        // given
        data.add(RecordDao(value = 3.0f, timeSpan = RecordDao.TimeSpan.QUARTERLY, userId = 1L))

        // when
        testObserver = repository.calc(data).test().awaitCount(1)

        //then
        Assert.assertEquals(3.0f, testObserver.values()[0].monthlyOutput)
        Assert.assertEquals(1.0f, testObserver.values()[0].monthlyIncome)
        Assert.assertEquals(-2.0f, testObserver.values()[0].monthlyDifference)
        Assert.assertEquals(36.0f, testObserver.values()[0].yearlyOutput)
        Assert.assertEquals(12.0f, testObserver.values()[0].yearlyIncome)
        Assert.assertEquals(-24.0f, testObserver.values()[0].yearlyDifference)

        // given
        data.add(RecordDao(value = 12.0f, timeSpan = RecordDao.TimeSpan.YEARLY, userId = 1L))

        // when
        testObserver = repository.calc(data).test().awaitCount(1)

        //then
        Assert.assertEquals(4.0f, testObserver.values()[0].monthlyOutput)
        Assert.assertEquals(1.0f, testObserver.values()[0].monthlyIncome)
        Assert.assertEquals(-3.0f, testObserver.values()[0].monthlyDifference)
        Assert.assertEquals(48.0f, testObserver.values()[0].yearlyOutput)
        Assert.assertEquals(12.0f, testObserver.values()[0].yearlyIncome)
        Assert.assertEquals(-36.0f, testObserver.values()[0].yearlyDifference)

        testObserver.dispose()
    }

    // TODO: other file: testing import and export
}