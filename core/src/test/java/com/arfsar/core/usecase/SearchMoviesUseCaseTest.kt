package com.arfsar.core.usecase

import androidx.paging.PagingData
import com.arfsar.core.model.Movie
import com.arfsar.core.repository.MovieRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchMoviesUseCaseTest {

    private val repository: MovieRepository = mockk()
    private val useCase = SearchMoviesUseCase(repository)

    @Test
    fun `invoke should call repository with correct query`() = runTest {
        val query = "Avengers"
        val pagingData = PagingData.empty<Movie>()
        val result = Result.success(pagingData)
        
        every { repository.searchMovies(query) } returns flowOf(result)

        useCase(query).collect { actualResult ->
            assertEquals(result, actualResult)
        }
    }
}
