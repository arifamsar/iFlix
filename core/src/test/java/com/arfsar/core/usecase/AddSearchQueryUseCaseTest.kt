package com.arfsar.core.usecase

import com.arfsar.core.repository.SearchRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AddSearchQueryUseCaseTest {

    private val repository: SearchRepository = mockk()
    private val useCase = AddSearchQueryUseCase(repository)

    @Test
    fun `invoke should call insertQuery when query is not blank`() = runTest {
        val query = "Avengers"
        coEvery { repository.insertQuery(query) } returns Unit

        useCase(query)

        coVerify { repository.insertQuery(query) }
    }

    @Test
    fun `invoke should trim query before inserting`() = runTest {
        val query = "  Avengers  "
        val trimmedQuery = "Avengers"
        coEvery { repository.insertQuery(trimmedQuery) } returns Unit

        useCase(query)

        coVerify { repository.insertQuery(trimmedQuery) }
    }

    @Test
    fun `invoke should not call insertQuery when query is blank`() = runTest {
        val query = "   "

        useCase(query)

        coVerify(exactly = 0) { repository.insertQuery(any()) }
    }
}
