package ir.rkr.kariz.caffeine

//import com.github.benmanes.caffeine.cache.Caffeine
//import com.github.benmanes.caffeine.cache.Expiry
import com.typesafe.config.Config
import ir.rkr.kariz.util.LayeMetrics
//import ir.rkr.kariz.Entry
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong




class CacheBuilder( config: Config, val layeMetrics: LayeMetrics) {}
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

//    val cache = CacheBuilder.newBuilder().maximumSize(50000000)
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
//        println(layemetrics.CheckUrl.oneMinuteRate)
//
//        Thread.sleep(1000)
//    }