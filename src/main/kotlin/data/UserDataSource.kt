package com.example.data

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
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
            }
        }
    }
}