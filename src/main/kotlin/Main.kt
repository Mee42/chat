import com.google.gson.Gson
import io.lettuce.core.RedisClient
import io.lettuce.core.api.reactive.RedisReactiveCommands
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands
import java.util.*

val redis: RedisClient = RedisClient.create("redis://localhost")
val connection: RedisReactiveCommands<String, String> = redis.connect().reactive()
val channels: RedisPubSubReactiveCommands<String, String> = redis.connectPubSub().reactive()


val channelsToPublish: RedisPubSubReactiveCommands<String, String> = redis.connectPubSub().reactive()

val CLIENT_ID = UUID.randomUUID().toString()

object Channels {
    const val MESSAGES = "messages"
}

var name = "Anon"

fun main() {
    startScreen()
    onCommand = {
        if (it.startsWith(":")){
            name = it.substring(1)
        } else {
            channelsToPublish.publish(Channels.MESSAGES, Message(name, it).toJson()).subscribe()
        }
    }

    channels.subscribe(Channels.MESSAGES).subscribe()
    channels.observeChannels()
            .doOnNext { println(it) }
            .filter { it.channel == Channels.MESSAGES }
            .map {
                println("new message:$it")
                val message = it.message.fromJson<Message>()
                addNewMessage(message)
            }.subscribe()

    val main = bootstrapClient()
    main.block()
}

private fun Any.toJson(): String {
    return Gson().toJson(this)
}
private inline fun <reified T> String.fromJson(): T {
    return Gson().fromJson(this,T::class.java)
}
