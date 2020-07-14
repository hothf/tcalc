package de.ka.jamit.tcalc

import de.ka.jamit.tcalc.repo.Repository
import org.junit.*
import org.koin.test.get

/**
 * A database management unit test.
 *
 * Created by Thomas Hofmann on 14.07.20
 **/
class DatabaseManagementUnitTest : InjectedAppTest() {
    
    @Test
    fun testit() {
        val repository = get<Repository>()

        val user = repository.getCurrentlySelectedUser()
        Assert.assertNotNull(user)

    }
}