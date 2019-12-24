package timelimit.gallery

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import timelimit.account.Users

@CrossOrigin(origins = ["http://localhost:8081"], maxAge = 3600)
@RestController
class CreateGalleryController {
    class Create constructor(val status: String, val art_id: Int)

    @RequestMapping("/gallery/create")
    public fun create(@RequestParam(value="data") data: String, @RequestParam(value="is_private") is_private: Boolean, @RequestParam(value="token") token: String) : Create {
        if (token.length != 32) {
            return Create("INVALID_TOKEN", -1)
        }

        var status = "FAIL"
        var artId : Int = -1
        transaction {
            val query = Users.select { Users.token eq token }
            if (query.count() != 1) {
                status = "INVALID_TOKEN"
                return@transaction
            }
            val user = query.iterator().next()
            val userId = user[Users.user_id]
            val tokenTime = user[Users.token_time] ?: return@transaction
            if (tokenTime < DateTime.now()) {
                status = "INVALID_TOKEN"
                return@transaction
            }

            Gallery.insert {
                it[Gallery.data] = data
                it[Gallery.is_private] = is_private
                it[Gallery.user_id] = userId
            }

            val res = Gallery.selectAll().orderBy(Gallery.art_id, SortOrder.DESC).limit(1)
            if (res.count() == 1) {
                artId = res.iterator().next()[Gallery.art_id]
                status = "OK"
            }
        }

        return Create(status, artId)
    }
}