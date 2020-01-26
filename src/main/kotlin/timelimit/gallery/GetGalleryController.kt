package timelimit.gallery

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import timelimit.account.Users
import timelimit.likes.Likes

@CrossOrigin(origins = ["http://localhost:8081"], maxAge = 3600)
@RestController
class GetGalleryController {
    class Art constructor(val art_id: Int, val data: String,
                          val owner: Boolean, val owner_name: String,
                          val likes: Int, val tokenLikedIt: Boolean)
    class Get constructor(val status: String, val items: Array<Art>)

    @RequestMapping("/gallery/get")
    fun get(
        @RequestParam(value = "login", defaultValue = "") login: String,
        @RequestParam(value = "like_order", defaultValue = "0") like_order: Boolean,
        @RequestParam(value = "offset", defaultValue = "0") offset: Int,
        @RequestParam(value = "count", defaultValue = "50") count: Int,
        @RequestParam(value = "token", defaultValue = "") token: String
    ): Get {
        val arts: MutableList<Art> = emptyList<Art>().toMutableList()
        var status = "OK"
        transaction {
            val userData = if (token.length == 32) Users.select { Users.token eq token }.singleOrNull() else null
            val userId = if (userData != null) userData.getOrNull(Users.user_id) ?: -1 else -1
            val likedPosts = mutableMapOf<Int, Boolean>()
            if (userId != -1) {
                Likes.slice(Likes.art_id).select { Likes.user_id eq userId }.forEach {
                    likedPosts[it[Likes.art_id]] = true
                }
            }

            var res = if (login == "") {
                (Gallery innerJoin Users)
                    .select { not(Gallery.is_private) or (Gallery.user_id eq userId) }
                    .limit(n = count, offset = offset)
            } else {
                (Gallery innerJoin Users)
                    .select { (not(Gallery.is_private) or (Gallery.user_id eq userId)) and (Users.login eq login) }
                    .limit(n = count, offset = offset)
            }

            if (like_order) {
                res = res.orderBy(Gallery.likes, SortOrder.DESC)
            }
            res = res.limit(n = count, offset = offset)
            res.forEach {
                arts.add(
                    Art(
                        it[Gallery.art_id], it[Gallery.data],
                        userId == it[Gallery.user_id], it[Users.login],
                        it[Gallery.likes], likedPosts.getOrDefault(it[Gallery.art_id], false)
                    )
                )
            }

        }

        return Get(status, arts.toTypedArray())
    }
}
