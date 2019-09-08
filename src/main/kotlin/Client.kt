import io.lettuce.core.SetArgs
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

object Key {
    const val SNOWFLAKE = "snowflake"

    object Prefixes {
        const val CLIENT = "client"
    }
}

const val JOIN = ":"

//returns a mono that completes when the client stops being a client
fun bootstrapClient(): Mono<Void> {
    // register the client id every minute.
    // redis will delete it after a minute and 30 seconds, so the grace period is 30 seconds
    println("bootstrapping client assembly: $CLIENT_ID")

    val keepKeyUpdated = Flux.interval(Duration.ZERO,Duration.ofMinutes(1))
            .flatMap { connection.set(Key.Prefixes.CLIENT + JOIN + CLIENT_ID,"" + it,SetArgs().ex(60)) }//expires in 60 sec

    return Mono.`when`(
            Mono.fromRunnable<Unit> { println("bootstrapping client\npress enter to continue") },
            keepKeyUpdated.last()
        )
}