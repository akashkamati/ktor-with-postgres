package com.example

import com.example.data.BookDataSource
import com.example.data.DatabaseFactory
import com.example.data.MoviesDataSource
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val databaseFactory = DatabaseFactory()
//    val userDataSource = UserDataSource(databaseFactory.database)
//    val bookDataSource = BookDataSource(databaseFactory.database)

    val moviesDataSource = MoviesDataSource(databaseFactory.database)

    configureSerialization()
    configureRouting(moviesDataSource)
}
