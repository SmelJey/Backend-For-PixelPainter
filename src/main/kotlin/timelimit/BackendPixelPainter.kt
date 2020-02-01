package timelimit

import org.jetbrains.exposed.sql.Database
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter


@SpringBootApplication
open class BackendPixelPainter

fun main(args: Array<String>) {
    Database.connect(
        "jdbc:postgresql://localhost:5432/pixelpainter", driver = "org.postgresql.Driver",
        user = "game_server", password = "zsxawsq2w1"
    )
    runApplication<BackendPixelPainter>(*args)
}