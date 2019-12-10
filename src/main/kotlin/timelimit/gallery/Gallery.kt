package timelimit.gallery

import org.jetbrains.exposed.sql.Table
import timelimit.account.Users

object Gallery : Table() {
    val art_id = integer("art_id")
    val user_id = integer("user_id") references Users.user_id
    val data = text("data")
    val is_private = bool("is_private")
}