package timelimit.gallery

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import timelimit.account.Users

@RestController
class CreateController {
    class Create constructor(val status: String, val art_id: Int)

    @RequestMapping("/gallery/create")
    public fun create(@RequestParam(value="data") data: String, @RequestParam(value="is_private") is_private: Boolean, @RequestParam(value="token") token: String) : Create {
        if (token.length != 32) {
            return Create("FAIL", -1)
        }

        var artId : Int = -1
        transaction {
            val query = Users.select { Users.token eq token }
            if (query.count() != 1) {
                return@transaction
            }
            val userId = query.iterator().next()[Users.user_id]

            Gallery.insert {
                it[Gallery.data] = data
                it[Gallery.is_private] = is_private
                it[Gallery.user_id] = userId
            }

            val res = Gallery.selectAll().orderBy(Gallery.art_id, SortOrder.DESC).limit(1)
            if (res.count() == 1) {
                artId = res.iterator().next()[Gallery.art_id]
            }
        }

        return Create(if (artId != -1) "OK" else "FAIL", artId)
    }
}