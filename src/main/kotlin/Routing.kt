package com.example

import com.example.data.Movie
import com.example.data.MoviesDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import java.io.File

fun Application.configureRouting(moviesDataSource: MoviesDataSource) {
    routing {

        route("movies"){

            get {
               // val id = call.queryParameters["id"]?.toIntOrNull() ?: return@get
               // val prefix = call.queryParameters["prefix"] ?: return@get
                val page = call.queryParameters["page"]?.toIntOrNull() ?: 1
                val movies = moviesDataSource.getPagedMovie(pageNumber = page, pageSize = 25)
                call.respond(movies)
            }

            post {
                val movie = call.receive<Movie>()
                moviesDataSource.insert(movie)
                call.respond(HttpStatusCode.OK)
            }

            post("insertAndGetId"){
                val movie = call.receive<Movie>()
                val id = moviesDataSource.insertAndGetId(movie)
                call.respond("The inserted row id is $id")
            }

            post("insertIgnore"){
                val movie = call.receive<Movie>()
                moviesDataSource.insertIgnore(movie)
                call.respond(HttpStatusCode.OK)
            }

            post("insertIgnoreAndGetId"){
                val movie = call.receive<Movie>()
                val id = moviesDataSource.insertIgnoreAndGetId(movie)
                call.respond("The inserted row id is $id")
            }

            post("batchInsert"){
                val path = "dummy_data/movies.json"
                val jsonString = File(path).readText()
                val movies:List<Movie> = Json.decodeFromString(jsonString)
                moviesDataSource.batchInsert(movies)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
