package timelimit.account

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val user_id = integer("user_id")
    val login = varchar("login", 32)
    val password = varchar("password", 32)
    val token = varchar("token", 32).nullable()
    val email = varchar("email", 64)
    val first_name = varchar("first_name", 64).nullable()
    val second_name = varchar("second_name", 64).nullable()
    val age = integer("age").nullable()
    val vk_profile = varchar("vk_profile", 64).nullable()
    val country = varchar("country", 64).nullable()
    val token_time = datetime("token_time").nullable()
}