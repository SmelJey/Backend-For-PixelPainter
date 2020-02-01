package timelimit.account

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["http://5.63.159.185:8081"])
@RestController
class LogoutAccountController {
    class Logout constructor(val status: String)

    @RequestMapping("/account/logout")
    public fun logout(@RequestParam("token") token: String) : Logout {
        if (token.length != 32) {
            return Logout("INVALID_TOKEN")
        }

        var status = "FAIL"
        transaction {
            val query = Users.select { Users.token eq token }
            if (query.count() != 1) {
                status = "OK"
                return@transaction
            }
            val user = query.iterator().next()
            val tokenTime = user[Users.token_time] ?: return@transaction
            if (tokenTime < DateTime.now()) {
                status = "OK"
                return@transaction
            }

            Users.update({ Users.user_id eq user[Users.user_id] }) {
                it[Users.token_time] = DateTime.now().minusDays(1)
            }

            status = "OK"
        }
        return Logout(status)
    }
}