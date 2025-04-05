package com.example.data

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

class UserDataSource(database: Database) {

    object Users : Table("users"){
        val id = integer("id").autoIncrement()

        //Numerical Data Types
        val age = integer("age") // INT => -2^31 to 2^31 -1
        val heightInCm = short("height_in_cm") // SMALLINT => -32768 TO 32767
        val followerCount = long("follower_count") //BIGINT => -2^63 to 2^63 -1
        val rating = float("rating") // REAL
        val accountBalance = decimal("account_balance",12,2) // DECIMAL

        //Boolean Data Type
        val isActive = bool("is_active").default(false) //BOOLEAN

        //String Data Types
        val gender = char("gender",1) //CHAR
        val email = varchar("email",80) //VARCHAR
        val name = varchar("name", length = 50)
        val bio = text("bio").nullable() //TEXT

        //Array Data Type
        val tags = array<String>("tags").default(emptyList())
        val skills = array<String?>("skills", columnType = VarCharColumnType(50))
        val doublesColumn = array("doubles_column", columnType = DoubleColumnType()).nullable()

        val array2D = array<Int, List<List<Int>>>("array2D", dimensions = 2)
        val array3D = array<String, List<List<List<String>>>>("array3D", dimensions = 3)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database){
           // SchemaUtils.drop(Users)

            SchemaUtils.create(Users)
            Users.insert {
                it[age] = 25
                it[heightInCm] = 175
                it[followerCount] = 100_000
                it[rating] = 4.77f
                it[accountBalance] = BigDecimal("12345.678")

                it[isActive] = true

                it[gender] = "M"
                it[email] = "test@example.com"
                it[name] = "Test"
                it[bio] = "This is some random text"

                it[tags] = listOf("tag1","tag2","tag3")
                it[skills] = listOf("Koltin","ktor","Postgres")
                it[doublesColumn] = listOf(3.5,2.5,1.8)

                it[array2D] = listOf(
                    listOf(1,2),
                    listOf(3,4)
                )

                it[array3D] = listOf(
                    listOf(
                        listOf("a","b"),
                        listOf("c","d"),
                    ),
                    listOf(
                        listOf("e","f"),
                        listOf("g","h"),
                    )
                )
            }
        }
    }
}