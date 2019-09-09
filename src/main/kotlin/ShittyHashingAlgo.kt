import java.security.MessageDigest

const val INIT_DEPTH = 100

fun hash(str: String,depth: Int = INIT_DEPTH):String{
    var processed = str
    for (a in 0 until 10){
        val bytes = MessageDigest.getInstance("SHA-1").digest(processed.toByteArray())
        processed = byteArrayToHexString(bytes)
        for (b in 0 until 10){
            processed += processed
        }
        for (c in 0 until 10){
            processed += processed.hashCode()
        }
    }
    return if (depth == 0)
        processed.substring(0,3)
    else
        hash(str + processed,depth-1)
}