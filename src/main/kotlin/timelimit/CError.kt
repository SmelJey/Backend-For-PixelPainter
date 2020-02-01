package timelimit

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@CrossOrigin(origins = ["http://5.63.159.185:8081"], maxAge = 3600)
@RestController
class CError : ErrorController {
    class Error constructor(val status: String, val type_error: Int)

    @RequestMapping("/error")
    fun error(resp : HttpServletResponse) : Error {
        return Error("ERROR", resp.status)
    }

    override fun getErrorPath(): String {
        return "/error"
    }
}
