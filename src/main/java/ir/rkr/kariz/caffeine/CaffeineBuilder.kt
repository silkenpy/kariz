package ir.rkr.kariz.caffeine


import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import com.google.gson.GsonBuilder
import com.typesafe.config.Config
import ir.rkr.kariz.kafka.KafkaConnector
import ir.rkr.kariz.netty.Command
import ir.rkr.kariz.util.KarizMetrics
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


data class Entry(val value: String, val ttl: Long)


class CaffeineBuilder(val kafka: KafkaConnector, config: Config, val karizMetrics: KarizMetrics) {

    private val logger = KotlinLogging.logger {}
    private val gson = GsonBuilder().disableHtmlEscaping().create()
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


        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay({

            val commands = kafka.get()

            commands.forEach { t, u ->

                val parsed = gson.fromJson(u, Command::class.java)
                when (parsed.cmd) {

                    "set" -> set(parsed.key, parsed.value!!, parsed.ttl, parsed.time)

                    "del" -> del(parsed.key)

                    "expire" -> expire(parsed.key, parsed.ttl)

                }
            }

            kafka.commit()


        }, 0, 100, TimeUnit.MILLISECONDS)
    }


    fun set(key: String, value: String, ttl: Long?, time: Long): Boolean {
        return try {
            if (ttl == null)
                cache.put(key, Entry(value, Long.MAX_VALUE))
            else {
                val remained = ((ttl * 1000 + time) - System.currentTimeMillis()) /1000

                if (remained > 0 )
                    cache.put(key, Entry(value, remained))
            }
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

    fun expire(key: String, ttl: Long?) {


        try {
            if (ttl != null)
                cache.put(key, Entry(cache.getIfPresent(key)!!.value, ttl))

        } catch (e: Exception) {

        }

    }


}
