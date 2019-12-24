package timelimit.account

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.net.URI
import java.security.MessageDigest
import kotlin.math.sin

@CrossOrigin(origins = ["http://localhost:8081"], maxAge = 3600)
@RestController
class EditAccountController {
    class Edit constructor(val status: String)

    @RequestMapping("/account/edit")
    fun edit(@RequestParam("field") field: String, @RequestParam("value") value: String,  @RequestParam("token") token: String) : Edit {
        if (token.length != 32) {
            return Edit("INVALID_TOKEN")
        }

        if (field !in arrayOf("password", "email", "first_name", "second_name", "age", "vk_profile", "country")) {
            return Edit("FAIL")
        }

        var rawValue = value

        if (field == "password") {
            if (value.length < 6) {
                return Edit("FAIL")
            }
            var password = rawValue
            password = (sin(password.length / 32.0) * 100.0).toString() + password + "xGhw663rTh12"
            val md = MessageDigest.getInstance("MD5").digest(password.toByteArray())
            rawValue = BigInteger(1, md).toString(16).padStart(32, '0')
        } else if (field == "age" && value.toIntOrNull() == null) {
            return Edit("FAIL")
        } else if (field == "email" && (value.length > 64 || !org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(value))) {
            return Edit("FAIL")
        } else if (field == "vk_profile") {
            if (!org.apache.commons.validator.routines.UrlValidator.getInstance().isValid(value)) {
                return Edit("FAIL")
            }
            val hostname = URI(value).host
            if (hostname != "vk.com" && hostname != "www.vk.com") {
                return Edit("FAIL")
            }
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
            val tokenTime = user[Users.token_time] ?: return@transaction
            if (tokenTime < DateTime.now()) {
                status = "INVALID_TOKEN"
                return@transaction
            }

            if (Users.update(where = { Users.user_id eq userId }) {
                    when(field) {
                        "password" -> {
                            it[Users.token_time] = DateTime.now().minusDays(1)
                            it[Users.password] = rawValue
                        }
                        "email" -> {
                            it[Users.token_time] = DateTime.now().minusDays(1)
                            it[Users.email] = rawValue
                        }
                        "first_name" -> it[Users.first_name] = rawValue
                        "second_name" -> it[Users.second_name] = rawValue
                        "age" -> it[Users.age] = rawValue.toInt()
                        "vk_profile" -> it[Users.vk_profile] = rawValue
                        "country" -> it[Users.country] = rawValue
                    }
                } != 1) {
                return@transaction
            }
            status = "OK"
        }

        return Edit(status)
    }
}