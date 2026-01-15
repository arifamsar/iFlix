package com.arfsar.core.source.local.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arfsar.core.source.local.entity.MovieEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MovieDaoTest {

    private lateinit var movieDao: MovieDao
    private lateinit var db: MovieDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MovieDatabase::class.java
        ).build()
        movieDao = db.movieDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() = runBlocking {
        val movie = MovieEntity(
            id = 1,
            title = "Test Movie",
            posterPath = "/path.jpg",
            backdropPath = "/back.jpg",
            releaseDate = "2023-01-01",
            voteAverage = 8.5,
            isFavorite = true
        )
        movieDao.insertMovie(movie)
        val byName = movieDao.getFavoriteMovies().first()
        assertEquals(byName[0].id, movie.id)
        assertEquals(byName[0].isFavorite, true)
    }

    @Test
    fun insertAndRemoveFavorite() = runBlocking {
        val movie = MovieEntity(
            id = 2,
            title = "Another Movie",
            posterPath = "/path2.jpg",
            backdropPath = "/back2.jpg",
            releaseDate = "2023-01-02",
            voteAverage = 7.5,
            isFavorite = true
        )
        
        // Insert as favorite
        movieDao.insertMovie(movie)
        
        var isFav = movieDao.isMovieFavorite(2).first()
        assertTrue(isFav)
        
        // Remove from favorite (update isFavorite = false)
        val updatedMovie = movie.copy(isFavorite = false)
        movieDao.insertMovie(updatedMovie) // using insert as it is OnConflictStrategy.REPLACE

        isFav = movieDao.isMovieFavorite(2).first()
        assertFalse(isFav)
        
        val favorites = movieDao.getFavoriteMovies().first()
        assertTrue(favorites.isEmpty())
    }
}
