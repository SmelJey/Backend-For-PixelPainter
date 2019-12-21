package timelimit.account

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.sin

@CrossOrigin(origins = ["http://localhost:8081"], maxAge = 3600)
@RestController
class RegisterController {
    class Register constructor(val status: String)

    @RequestMapping("/account/register")
    public fun register(@RequestParam(value="login") login: String, @RequestParam(value="password") raw_password: String) : Register {
        if (raw_password.length < 6) {
            return Register("FAIL")
        }

        var password = raw_password
        password = (sin(password.length / 32.0) * 100.0).toString() + password + "xGhw663rTh12"
        val md = MessageDigest.getInstance("MD5").digest(password.toByteArray())
        password = BigInteger(1, md).toString(16).padStart(32, '0')

        var status = "OK"

        transaction {
            if (Users.select { Users.login eq login }.limit(1).count() == 0) {
                Users.insert {
                    it[Users.login] = login
                    it[Users.password] = password
                }
            } else {
                status = "FAIL"
            }
        }

        return Register(status)
    }
}
