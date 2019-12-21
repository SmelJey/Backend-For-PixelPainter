package timelimit.gallery

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.not
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import timelimit.account.Users

@CrossOrigin(origins = ["http://localhost:8081"], maxAge = 3600)
@RestController
class GetGalleryController {
    class Art constructor(val art_id : Int, val data: String, val owner: Boolean)
    class Get constructor(val status : String, val items : Array<Art>)

    @RequestMapping("/gallery/get")
    fun get(@RequestParam(value="like_order", defaultValue = "0") like_order : Boolean, @RequestParam(value = "offset", defaultValue = "0") offset: Int, @RequestParam(value = "count", defaultValue = "50") count: Int, @RequestParam(value = "token") token: String) : Get {
        val arts : MutableList<Art> = emptyList<Art>().toMutableList()
        var status = "OK"
        transaction {
            val query = Users.select { Users.token eq token }
            if (query.count() == 1) {
                val userId = query.iterator().next()[Users.user_id]
                var res = Gallery.select { not(Gallery.is_private) or (Gallery.user_id eq userId)}
                if (like_order) {
                    res = res.orderBy(Gallery.likes, SortOrder.DESC)
                }
                res = res.limit(n = count, offset = offset)
                res.forEach {
                    arts.add(Art(it[Gallery.art_id], it[Gallery.data], userId == it[Gallery.user_id]))
                }
            } else if (query.count() == 0) {
                var res = Gallery.select { not(Gallery.is_private)}.limit(n = count, offset = offset)
                if (like_order) {
                    res = res.orderBy(Gallery.likes, SortOrder.DESC)
                }
                res = res.limit(n = count, offset = offset)
                res.forEach {
                    arts.add(Art(it[Gallery.art_id], it[Gallery.data], false))
                }
            } else {
                status = "FAIL"
            }
        }

        return Get(status, arts.toTypedArray())
    }
}