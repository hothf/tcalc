package de.ka.jamit.tcalc

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.ka.jamit.tcalc.repo.Repository
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

    @Before
    fun setupDatabase() {
        repository.wipeDatabase()
    }

    @Test
    fun database_shouldHaveDefaultValues_whenInitialized() {
        Assert.assertEquals("There should only be the default user with name=default", "default", repository.getCurrentlySelectedUser().name)
        Assert.assertEquals("There should only be 7 records of the default user", 7, repository.getAllRecordsOfCurrentlySelectedUser().size)

        repository.wipeDatabase()

        Assert.assertEquals("There should only be the default user with name=default", "default", repository.getCurrentlySelectedUser().name)
        Assert.assertEquals("There should only be 7 records of the default user", 7, repository.getAllRecordsOfCurrentlySelectedUser().size)
    }

}