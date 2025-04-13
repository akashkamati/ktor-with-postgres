package com.example.data

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class BookDataSource(private val database: Database) {

    object Books : Table(){
        val id = integer("id").autoIncrement()
        val title = varchar("title",150)
        val authorId = reference("author_id",Authors).nullable()

        override val primaryKey = PrimaryKey(id)

    }

    object Authors : IntIdTable(){
        val name = varchar("name",100)
    }

    init {
       // createTableAndInsertData()
        transaction(database) {
            innerJoin()
            leftJoin()
            rightJoin()
            fullJoin()
            crossJoin()
        }
    }

    private fun innerJoin() {
        println("\n--- Inner Join ---")
        (Authors innerJoin Books)
            .selectAll()
            .forEach {
                println("${it[Authors.name]} wrote ${it[Books.title]}")
            }
    }

    private fun leftJoin() {
        println("\n--- Left Join ---")
        (Authors leftJoin Books)
            .selectAll()
            .forEach {
                println("${it[Authors.name]} - ${it[Books.title]}")
            }
    }

    private fun rightJoin() {
        println("\n--- Right Join ---")
        (Authors rightJoin Books)
            .selectAll()
            .forEach {
                println("${it[Authors.name]} - ${it[Books.title]}")
            }
    }

    private fun fullJoin() {
        println("\n--- Full Join ---")
        (Authors fullJoin Books)
            .selectAll()
            .forEach {
                println("${it[Authors.name]} - ${it[Books.title]}")
            }
    }

    private fun crossJoin() {
        println("\n--- Cross Join ---")
        (Authors crossJoin Books)
            .selectAll()
            .forEach {
                println("${it[Authors.name]} - ${it[Books.title]}")
            }
    }


    private fun createTableAndInsertData(){
        transaction(database){
            SchemaUtils.create(Books,Authors)
            val author1 = Authors.insertAndGetId {
                it[name] = "Author1"
            }
            val author2 = Authors.insertAndGetId {
                it[name] = "Author2"
            }
            val author3 = Authors.insertAndGetId {
                it[name] = "Author3"
            }

            Books.insert {
                it[title] = "Book1"
                it[authorId] = author1
            }

            Books.insert {
                it[title] = "Book2"
                it[authorId] = author1
            }

            Books.insert {
                it[title] = "Book3"
                it[authorId] = author2
            }

            Books.insert {
                it[title] = "Book4"
                it[authorId] = null
            }



        }
    }
}