package timelimit.account

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val user_id = integer("user_id")
    val login = varchar("login", 32)
    val password = varchar("password", 32)
    val token = varchar("token", 32)
}