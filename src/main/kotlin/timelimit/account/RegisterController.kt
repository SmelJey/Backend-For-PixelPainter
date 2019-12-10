package timelimit.account

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.sin

@RestController
class RegisterController {
    object Users : Table() {
        val user_id = LoginController.Users.integer("user_id")
        val login = LoginController.Users.varchar("login", 32)
        val password = LoginController.Users.varchar("password", 32)
        val token = LoginController.Users.varchar("token", 32)
    }

    @RequestMapping("/account/register")
    public fun register(@RequestParam(value="login") login: String, @RequestParam(value="password") raw_password: String) : Register {
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
