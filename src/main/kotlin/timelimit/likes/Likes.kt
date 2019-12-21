package timelimit.likes

import org.jetbrains.exposed.sql.Table
import timelimit.account.Users
import timelimit.gallery.Gallery

object Likes : Table() {
    val user_id = integer("user_id").references(Users.user_id)
    val art_id = integer("art_id").references(Gallery.art_id)
}