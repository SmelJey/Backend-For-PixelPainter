package timelimit.account

import main.kotlin.timelimit.account.ValidatorSQL
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

@CrossOrigin(origins = ["http://5.63.159.185:8081"], maxAge = 3600)
@RestController
class EditAccountController {
    class Edit constructor(val status: String)

    @RequestMapping("/account/edit")
    fun edit(@RequestParam("field") field: String, @RequestParam("value") value: String,  @RequestParam("token") token: String) : Edit {
        if (token.length != 32) {
            return Edit("INVALID_TOKEN")
        }

        val fields = field.replace(" ", "").split(',')
        val values = value.split(',').toMutableList()

        if (fields.count() == 0 || fields.count() != values.count()) {
            return Edit("FAIL")
        }

        for (i in 0 until fields.count()) {
            if (fields[i] !in arrayOf("password", "email", "first_name", "second_name", "age", "vk_profile", "country")) {
                return Edit("FAIL")
            }
        }

        for (i in 0 until fields.count()) {
            if (fields[i] == "password") {
                if (values[i].length < 6) {
                    return Edit("FAIL")
                }
                var password = values[i]
                password = (sin(password.length / 32.0) * 100.0).toString() + password + "xGhw663rTh12"
                val md = MessageDigest.getInstance("MD5").digest(password.toByteArray())
                values[i] = BigInteger(1, md).toString(16).padStart(32, '0')
            } else if (fields[i] == "age" && (values[i].toIntOrNull() == null || values[i].toInt() < 0)) {
                return Edit("FAIL")
            } else if (fields[i] == "email" && (values[i].length > 64 || !org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(
                    values[i]
                ))
            ) {
                return Edit("FAIL")
            } else if (fields[i] == "vk_profile") {
                if (!org.apache.commons.validator.routines.UrlValidator.getInstance().isValid(values[i])) {
                    return Edit("FAIL")
                }
                val hostname = URI(values[i]).host
                if (hostname != "vk.com" && hostname != "www.vk.com") {
                    return Edit("FAIL")
                }
            }
        }

        for (i in 0 until fields.count()) {
            if (!ValidatorSQL.getInstance().checkLength(fields[i], values[i])) {
                return Edit("MAX_LENGTH")
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
                    for (i in 0 until fields.count()) {
                        when (fields[i]) {
                            "password" -> {
                                it[Users.token_time] = DateTime.now().minusDays(1)
                                it[Users.password] = values[i]
                            }
                            "email" -> {
                                it[Users.token_time] = DateTime.now().minusDays(1)
                                it[Users.email] = values[i]
                            }
                            "first_name" -> it[Users.first_name] = values[i]
                            "second_name" -> it[Users.second_name] = values[i]
                            "age" -> it[Users.age] = values[i].toInt()
                            "vk_profile" -> it[Users.vk_profile] = values[i]
                            "country" -> it[Users.country] = values[i]
                        }
                    }
                } != 1) {
                return@transaction
            }
            status = "OK"
        }

        return Edit(status)
    }
}