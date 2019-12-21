package timelimit.account

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val user_id = integer("user_id")
    val login = varchar("login", 32)
    val password = varchar("password", 32)
    val token = varchar("token", 32)
    val email = varchar("email", 64)
    val first_name = varchar("first_name", 64)
    val second_name = varchar("second_name", 64)
    val age = integer("age")
    val vk_profile = varchar("vk_profile", 64)
    val country = varchar("country", 64)
    val token_time = datetime("token_time")
}