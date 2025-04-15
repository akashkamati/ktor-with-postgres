package com.example.data

import com.example.data.MoviesDataSource.Movies.description
import com.example.data.MoviesDataSource.Movies.durationInMinutes
import com.example.data.MoviesDataSource.Movies.genre
import com.example.data.MoviesDataSource.Movies.tags
import com.example.data.MoviesDataSource.Movies.title
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class Movie(
    val id:Int? = null,
    val title:String,
    val genre:String,
    val description:String,
    val duration:Int,
    val tags:List<String>
)


class MoviesDataSource(private val database: Database) {

    object Movies:IntIdTable(){
        val title = varchar("title",100)
        val genre = varchar("genre",100)
        val description = text("description")
        val durationInMinutes = integer("duration_in_minute")
        val tags = array("tags", columnType = VarCharColumnType(70))
    }

    init {
        transaction(database){
            SchemaUtils.create(Movies)
        }
    }

    fun insert(movie:Movie){
        transaction(database){
            Movies.insert {
                it[title] = movie.title
                it[description] = movie.description
                it[genre] = movie.genre
                it[durationInMinutes] = movie.duration
                it[tags] = movie.tags
            }
        }
    }

    fun insertAndGetId(movie:Movie):Int{
        return transaction(database){
            Movies.insertAndGetId {
                it[title] = movie.title
                it[description] = movie.description
                it[genre] = movie.genre
                it[durationInMinutes] = movie.duration
                it[tags] = movie.tags
            }.value
        }
    }

    fun insertIgnore(movie: Movie){
        transaction(database){
            Movies.insertIgnore {
                if (movie.id != null){
                    it[id] = movie.id
                }
                it[title] = movie.title
                it[description] = movie.description
                it[genre] = movie.genre
                it[durationInMinutes] = movie.duration
                it[tags] = movie.tags
            }
        }
    }

    fun insertIgnoreAndGetId(movie: Movie) : Int?{
        return transaction(database){
            Movies.insertIgnoreAndGetId {
                if (movie.id != null){
                    it[id] = movie.id
                }
                it[title] = movie.title
                it[description] = movie.description
                it[genre] = movie.genre
                it[durationInMinutes] = movie.duration
                it[tags] = movie.tags
            }?.value
        }
    }

    fun batchInsert(movies:List<Movie>){
        transaction(database){
            Movies.batchInsert(movies){movie->
                this[title] = movie.title
                this[genre] = movie.genre
                this[description] = movie.description
                this[durationInMinutes] = movie.duration
                this[tags] = movie.tags
            }
        }
    }


}