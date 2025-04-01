package com.example

import com.example.data.DatabaseFactory
import com.example.data.UserDataSource
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val databaseFactory = DatabaseFactory()
    val userDataSource = UserDataSource(databaseFactory.database)

    configureSerialization()
    configureRouting()
}
