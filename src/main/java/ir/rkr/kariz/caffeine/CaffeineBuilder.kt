package ir.rkr.kariz.caffeine


import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import com.typesafe.config.Config
import ir.rkr.kariz.util.KarizMetrics
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.TimeUnit


data class Entry(val value: String, val ttl: Long)

class CaffeineBuilder(config: Config, val karizMetrics: KarizMetrics) {

    private val logger = KotlinLogging.logger {}
    val cache: Cache<String, Entry>


    init {

        cache = Caffeine.newBuilder().expireAfter(object : Expiry<String, Entry> {
            override fun expireAfterCreate(key: String, value: Entry, currentTime: Long): Long {
                return TimeUnit.SECONDS.toNanos(value.ttl)
            }

            override fun expireAfterUpdate(key: String, value: Entry, currentTime: Long, currentDuration: Long): Long {
                return TimeUnit.SECONDS.toNanos(value.ttl)
            }

            override fun expireAfterRead(key: String, value: Entry, currentTime: Long, currentDuration: Long): Long {
                return currentDuration
            }
        }).removalListener<String, Entry> { k, v, c -> }
                .build<String, Entry>()
    }


    fun set(key: String, value: String, ttl: Long = Long.MAX_VALUE): Boolean {
        return try {
            cache.put(key, Entry(value, ttl))
            true
        } catch (e: Exception) {
            logger.error(e) { "Error $e" }
            false
        }

    }

    fun get(key: String): Optional<String> {

        return try {
            Optional.of(cache.getIfPresent(key)!!.value)
        } catch (e: Exception) {
            Optional.empty()
        }


    }

    fun del(key: String): Boolean {

        return try {
            cache.invalidate(key)
            true

        } catch (e: Exception) {
            false
        }


    }


}
//val evicted = AtomicLong()
//

//val cache = Caffeine.newBuilder()
//        .expireAfter(object : Expiry<String, Entry> {
//
//            override fun expireAfterCreate(key: String, value: Entry, currentTime: Long): Long {
//                return TimeUnit.SECONDS.toNanos(value.ttl.toLong())
//            }
//
//            override fun expireAfterUpdate(key: String, value: Entry, currentTime: Long, currentDuration: Long): Long {
//                return currentDuration
//            }
//
//            override fun expireAfterRead(key: String, value: Entry, currentTime: Long, currentDuration: Long): Long {
//                return currentDuration
//            }
//
//        })
//        .removalListener<String, Entry> { k, v, c ->
//            evicted.getAndIncrement()
////                println("$k is expired")
//            //Assert.fail(k + " " + v + " " + c);
//        }.build<String, Entry>()

//    val cache = object : Cache2kBuilder<String, String>(){}
//            .eternal(false)
//            .expireAfterWrite(100, TimeUnit.SECONDS)
//            .entryCapacity(Long.MAX_VALUE)
////
//////            .retryInterval(20, TimeUnit.SECONDS)
//////            .resilienceDuration(5, TimeUnit.MINUTES)
//////            .suppressExceptions(true) /* has no effect! */
////            /* ... set loader ... */
//            .build()

//    val cache = HashMap<String,String>()

//    val cache = CaffeineBuilder.newBuilder().maximumSize(50000000)
//            .expireAfterWrite(100,TimeUnit.SECONDS).build<String,String>()
//println(System.currentTimeMillis())

//
//for (i in 1..6000000) {
//    // println("salam $i")
//    cache.put("s $i", Entry("$i", 100))
////            if (2 == 0) {
////                cache.invoke("salam $i") { e ->
////                    e.setValue("salam $i")
////                            .setExpiryTime(System.currentTimeMillis() + 50000)
////                }
////            } else {
////                cache.put("salam $i", "$i")
////            }
//}
//println(System.currentTimeMillis())
//
//for (i in 1..6000000) {
//    // println("salam $i")
//    cache.put("k $i", Entry("$i", 30))
////            if (2 == 0) {
////                cache.invoke("salam $i") { e ->
////                    e.setValue("salam $i")
////                            .setExpiryTime(System.currentTimeMillis() + 50000)
////                }
////            } else {
////                cache.put("salam $i", "$i")
////            }
//}
//
//println(System.currentTimeMillis())


//while (true) {
//    cache.cleanUp()
//    println("size = ${cache.estimatedSize()}, evicted = $evicted")
//    Thread.sleep(1000)
//}


//    while (true){
//        println(karizMetrics.CheckUrl.oneMinuteRate)
//
//        Thread.sleep(1000)
//    }