package timelimit

import org.jetbrains.exposed.sql.Database
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class BackendPixelPainter

fun main(args: Array<String>) {
    Database.connect(
        "jdbc:postgresql://localhost:5432/pixelpainter", driver = "org.postgresql.Driver",
        user = "game_server", password = "zsxawsq2w1"
    )
    runApplication<BackendPixelPainter>(*args)
}