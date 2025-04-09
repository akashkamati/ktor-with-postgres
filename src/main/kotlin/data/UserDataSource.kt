package com.example.data

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaInstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.*
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.ZoneOffset

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
       // val email = varchar("email",80) //VARCHAR
        val name = varchar("name", length = 50)
        val bio = text("bio").nullable() //TEXT

        //Array Data Type
        val tags = array<String>("tags").default(emptyList())
        val skills = array<String?>("skills", columnType = VarCharColumnType(50))
        val doublesColumn = array("doubles_column", columnType = DoubleColumnType()).nullable()

        val array2D = array<Int, List<List<Int>>>("array2D", dimensions = 2)
        val array3D = array<String, List<List<List<String>>>>("array3D", dimensions = 3)

        //Binary Data Types
        val profileImg = blob("profile_img")
        val binary = binary("binary")
        val binaryWithSize = binary("binary_with_size",1024)
        val largeObj = blob("large_obj",useObjectIdentifier = true)

        // Enum Data Type
        val enumOrdinal = enumeration("enum_ordinal", Role::class)
        val enumByName = enumerationByName("enum_by_name",10,Role::class)

        // Date/Time Data Type
        val date = date("date")
        val time = time("time")
        val dateTime = datetime("date_time").defaultExpression(CurrentDateTime)
        val timestamp = timestamp("timestamp").defaultExpression(CurrentTimestamp)
        val timestampWithTimeZone = timestampWithTimeZone("timestamp_with_time_zone")

        // Json Data Type
        val jsonData = json<SimpleData>("json_data", jsonConfig = Json.Default)
        val jsonArrayData = json<Array<SimpleData>>("json_array_data", jsonConfig = Json.Default)

        // Custom Data Type
        val email = email("email")




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
               // it[email] = "test@example.com"
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

                val byteArray = "Simple binary data".toByteArray()
                it[profileImg] = ExposedBlob(byteArray)
                it[binary] = byteArray
                it[binaryWithSize] = byteArray
                it[largeObj] = ExposedBlob(byteArray)

                it[enumOrdinal] = Role.USER
                it[enumByName] = Role.ADMIN

                it[date] = LocalDate(1999,10,26)
                it[time] = LocalTime(9,30)
                it[timestampWithTimeZone] = Clock.System.now().toJavaInstant().atOffset(ZoneOffset.UTC)

                it[jsonData] = SimpleData("test",12, listOf("val1","val2"))
                it[jsonArrayData] = arrayOf(
                    SimpleData("test1",12, listOf("val1","val2")),
                    SimpleData("test2",123, listOf("val11","val22"))
                )

                it[email] = "test@example.com"
            }
        }
    }
}


fun Table.email(name:String) : Column<String> = registerColumn(name,EmailColumnType())


class EmailColumnType : StringColumnType(){

    override fun sqlType(): String  = "VARCHAR(100)"

    private val regex = Regex("^[\\w.-]+@[\\w.-]+\\.\\w+\$")

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        val email = value as? String
        if (!isValidEmail(email)){
            throw IllegalArgumentException("Invalid email: $email")
        }
        super.setParameter(stmt, index, value)
    }

    override fun notNullValueToDB(value: String): Any {
        if (!isValidEmail(value)){
            throw IllegalArgumentException("Invalid email: $value")
        }
        return super.notNullValueToDB(value)
    }

    private fun isValidEmail(email:String?) : Boolean{
        return email != null && regex.matches(email)
    }
}

@Serializable
data class SimpleData(
    val stringValue:String,
    val intValue:Int,
    val arrayValue:List<String>
)

enum class Role{
    USER, ADMIN
}