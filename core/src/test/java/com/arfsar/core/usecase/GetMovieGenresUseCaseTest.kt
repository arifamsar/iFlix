package com.arfsar.core.usecase

import app.cash.turbine.test
import com.arfsar.core.model.Genre
import com.arfsar.core.repository.MovieRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMovieGenresUseCaseTest {

    private val repository: MovieRepository = mockk()
    private val useCase = GetMovieGenresUseCase(repository)

    @Test
    fun `invoke should return result from repository`() = runTest {
        val genres = listOf(Genre(1, "Action"), Genre(2, "Comedy"))
        val result = Result.success(genres)
        
        every { repository.getMovieGenres() } returns flowOf(result)

        useCase().test {
            assertEquals(result, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        val exception = Exception("Network Error")
        val result = Result.failure<List<Genre>>(exception)
        
        every { repository.getMovieGenres() } returns flowOf(result)

        useCase().test {
            val item = awaitItem()
            assertEquals(true, item.isFailure)
            assertEquals(exception.message, item.exceptionOrNull()?.message)
            awaitComplete()
        }
    }
}
