package timelimit.account

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["http://localhost:8081"], maxAge = 3600)
@RestController
class GetAccountController {
    class Get(var status : String, var login : String, var email : String, var first_name : String,
              var second_name : String, var age : Int, var vk_profile : String, var country : String) {
        constructor(status: String)
                : this(status, "", "", "", "", 0, "", "")
    }

    @RequestMapping("/account/get")
    fun get(@RequestParam("login", defaultValue = "") login : String,
            @RequestParam("token", defaultValue = "") token : String) : Get {
        if (login == "" && token == "" || login != "" && token != "") {
            return Get("FAIL")
        }

        if (token != "" && token.length != 32) {
            return Get("INVALID_TOKEN")
        }

        val rtn = Get("FAIL")
        transaction {
            val query = Users.select { (Users.token eq token) or (Users.login eq login) }
            if (query.count() != 1) {
                rtn.status = if (token != "") "INVALID_TOKEN" else "INVALID_LOGIN"
                return@transaction
            }
            val user = query.iterator().next()
            if (token != "") {
                val tokenTime = user[Users.token_time] ?: return@transaction
                if (tokenTime < DateTime.now()) {
                    rtn.status = "INVALID_TOKEN"
                    return@transaction
                }
            }

            rtn.status = "OK"
            rtn.login = user[Users.login]
            rtn.email = if (token != "") user[Users.email] else ""
            rtn.first_name = user[Users.first_name] ?: ""
            rtn.second_name = user[Users.second_name] ?: ""
            rtn.age = user[Users.age] ?: 0
            rtn.vk_profile = user[Users.vk_profile] ?: ""
            rtn.country = user[Users.country] ?: ""
        }

        return rtn
    }
}