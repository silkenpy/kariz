//package ir.rkr.kariz.redis
//
//fun String.redisRequestParser(): List<String> = this.split("\r\n").filterIndexed { idx, _ -> idx % 2 == 0 }
//
//
//fun redisHandler(request: String): String {
//    val parts = request.redisRequestParser()
//
//    println(parts.toString())
//    when (parts[1].toLowerCase()) {
//        "ping" ->  return "+PONG\r\n"
//        "set"  ->  return "+OK\r\n"
//        "get"  ->  return "+OK\r\n"
////        "mset"  ->  return "+OK\r\n"
////        "mget"  ->  return "+OK\r\n"
//    }
//
//    return "-Error message\r\n"
//}