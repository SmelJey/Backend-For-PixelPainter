package timelimit.likes

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import timelimit.account.Users
import timelimit.gallery.Gallery

@RestController
class RemoveLikesController {
    class Remove(val status: String)

    @RequestMapping("likes/remove")
    fun remove(@RequestParam art_id : Int, @RequestParam("token") token : String) : Remove {
        if (token.length != 32) {
            return Remove("INVALID_TOKEN")
        }

        var status = "FAIL"
        transaction {
            val query = Users.select { Users.token eq token }
            if (query.count() != 1) {
                status = "INVALID_TOKEN"
                return@transaction
            }
            val user = query.iterator().next()
            val userId = user[Users.user_id]
            val tokenTime = user[Users.token_time]
            if (tokenTime < DateTime.now()) {
                status = "INVALID_TOKEN"
                return@transaction
            }

            if (Likes.select { (Likes.art_id eq art_id) and (Likes.user_id eq userId) }.count() != 0) {
                if (Likes.deleteWhere { (Likes.art_id eq art_id) and (Likes.user_id eq userId) } != 0) {
                    status = "OK"
                }
            } else {
                status = "OK"
            }

            if (status == "OK") {
                val count = Likes.select { Likes.art_id eq art_id }.count()
                Gallery.update(where = { Gallery.art_id eq art_id}) {
                    it[Gallery.likes] = count
                }
            }
        }

        return Remove(status)
    }
}