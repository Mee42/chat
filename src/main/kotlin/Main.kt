import com.google.gson.Gson
import com.google.gson.GsonBuilder
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

var username = "Anon"

var hash = "   "

fun main() {
    initCommands()
    startScreen()
    onCommand = it@ {
        if (it.startsWith(":")){
            val commandStr = it.substring(1,it.indexOf(' ').takeUnless { i -> i == -1 } ?: it.length)
            val argument = it.substring(it.indexOf(' ').takeUnless { i -> i == -1 } ?: it.length).trim().takeUnless { str -> str.isBlank() }
            val command = commands.firstOrNull { com -> com.names.contains(commandStr) }
            if (command == null){
                addNewDisplayable(userErrorDis("Invalid command \"$commandStr\""))
                return@it
            }
            //handle all the illegal argument states
            when {
                command.argState == ArgsState.REQ && argument == null -> throw UserErrorException("\"$commandStr\" command needs argument <${command.argsName}>")
                command.argState == ArgsState.NONE && argument != null -> addNewDisplayable(warningDis("\"$commandStr\" does not take an argument"))
            }
            command.runner.invoke(argument)
        } else {
            if(it.isNotBlank())
                channelsToPublish.publish(Channels.MESSAGES, Message(username,hash,it).toJson()).subscribe()
        }
    }


    channels.subscribe(Channels.MESSAGES).subscribe()
    channels.observeChannels()
            .filter { it.channel == Channels.MESSAGES }
            .map {
                val message = it.message.fromJson<Message>()
                addNewDisplayable(message)
            }
            .doOnError { addNewDisplayable(internalErrorDis(it)) }
            .subscribe()

    val main = bootstrapClient()
    main.block()
}


val gson: Gson = GsonBuilder().create()
private fun Any.toJson(): String {
    return gson.toJson(this)
}
private inline fun <reified T> String.fromJson(): T {
    return gson.fromJson(this,T::class.java)
}
