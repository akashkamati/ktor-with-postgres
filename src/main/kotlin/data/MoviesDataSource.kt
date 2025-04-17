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

//  ------------------------ Insert Operations------------------------

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

//  ------------------------ Read Operations---------------------------

    fun getAllMovies() : List<Movie> {
        return transaction(database){
            Movies.selectAll().toList().map { it.toMovie() }
        }
    }

    // Basic conditions
    fun getMovieById(id:Int) : Movie?{
        return transaction(database){
            Movies.selectAll().where { Movies.id eq id }.firstOrNull()?.toMovie()
        }
    }

    fun getMoviesNotInGenre(genre: String) : List<Movie> = transaction(database){
        Movies.selectAll().where { Movies.genre neq genre }.toList().map { it.toMovie() }
    }

    fun getMoviesWithNullDescription() = transaction(database){
        Movies.selectAll().where { description.isNull() }.toList().map { it.toMovie() }
    }

    fun getMoviesWithNotNullDescription() = transaction(database){
        Movies.selectAll().where { description.isNotNull() }.toList().map { it.toMovie() }
    }

    fun getAllShortMovies(): List<Movie> = transaction(database){
        Movies.selectAll().where { durationInMinutes less  120 }.toList().map { it.toMovie() }
    }

    fun getAllLongMovies(): List<Movie> = transaction(database){
        Movies.selectAll().where { durationInMinutes greaterEq  120 }.toList().map { it.toMovie() }
    }

    //Logical conditions
    fun getShortActionMovies() : List<Movie> = transaction(database){
        Movies.selectAll().where {
            (genre eq "Action") and (durationInMinutes less 120)
        }.toList().map { it.toMovie() }
    }

    fun getShortOrActionMovies() : List<Movie> = transaction(database){
        Movies.selectAll().where {
            (genre eq "Action") or (durationInMinutes less 120)
        }.toList().map { it.toMovie() }
    }

    // Pattern Matching
    fun getMoviesWithTitleStartingWith(prefix:String) : List<Movie> = transaction(database){
        Movies.selectAll().where { title like "$prefix %" }.toList().map { it.toMovie() }
    }

    fun getMoviesWithTitleNotStartingWith(prefix:String) : List<Movie> = transaction(database){
        Movies.selectAll().where { title notLike "$prefix %" }.toList().map { it.toMovie() }
    }

    fun getMoviesWithTitleRegex(regex:String) : List<Movie> = transaction(database){
        Movies.selectAll().where { title regexp  regex }.toList().map { it.toMovie() }
    }

    // Range conditions
    fun getMoviesWithinDurationRange(min:Int,max:Int) : List<Movie> =
        transaction(database){
            Movies.selectAll().where { durationInMinutes.between(min,max) }
                .toList()
                .map { it.toMovie() }
        }

    // Collection conditions
    fun getMoviesByGenres(genres:List<String>) : List<Movie> = transaction(database){
        Movies.selectAll().where { genre inList genres  }
            .toList()
            .map { it.toMovie() }
    }

    fun getMoviesNotInGenres(genres:List<String>) : List<Movie> = transaction(database){
        Movies.selectAll().where { genre notInList genres  }
            .toList()
            .map { it.toMovie() }
    }

    // Conditional where
    fun findMoviesConditionally(genre: String?,maxDuration:Int?) : List<Movie> = transaction(database){
        val query = Movies.selectAll()
        if (!genre.isNullOrBlank()){
            query.andWhere { Movies.genre eq  genre }
        }
        if (maxDuration != null && maxDuration > 0){
            query.orWhere { durationInMinutes lessEq maxDuration }
        }
        query.toList().map { it.toMovie() }
    }

    // Sorting and Aggregation
    fun getTopGenresByMovieCount() : List<Map<String,Long>> = transaction(database){
        Movies
            .select(genre,Movies.id.count())
            .groupBy(genre)
            .orderBy(Movies.id.count(),SortOrder.DESC)
            .map { mapOf(it[genre] to it[Movies.id.count()]) }
    }

    // Pagination
    fun getPagedMovie(pageNumber:Int,pageSize:Int=10) : List<Movie> = transaction(database){
        val offset = ((pageNumber-1)*pageSize).toLong()
        Movies.selectAll().limit(pageSize).offset(offset).toList().map { it.toMovie() }
    }




    private fun ResultRow.toMovie():Movie{
        return Movie(
            id = this[Movies.id].value,
            title = this[title],
            genre = this[genre],
            description = this[description],
            duration = this[durationInMinutes],
            tags = this[tags]
        )
    }


}