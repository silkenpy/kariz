package ir.rkr.kariz

//
//import com.github.benmanes.caffeine.cache.Caffeine
//import com.github.benmanes.caffeine.cache.Expiry

import com.typesafe.config.ConfigFactory
import ir.rkr.kariz.caffeine.CaffeineBuilder
import ir.rkr.kariz.kafka.KafkaConnector
import ir.rkr.kariz.netty.NettyServer
import ir.rkr.kariz.util.KarizMetrics
import mu.KotlinLogging


const val version = 0.1

/**
 * Kariz main entry point.
 */


fun main(args: Array<String>) {
    val logger = KotlinLogging.logger {}
    val config = ConfigFactory.defaultApplication()
    val karizMetrics = KarizMetrics()
    val kafka = KafkaConnector(config.getString("kafka.topic"), config)
    val caffeinCache = CaffeineBuilder(kafka, config, karizMetrics)
    NettyServer(kafka, caffeinCache, config, karizMetrics)
//    JettyRestServer(config)


//    c.set("ali","ali29")
//    println(c.get("ali").get())
//    println(c.get("ali2").get())

    logger.info { "Kariz V$version is ready :D" }

    // caffeine


}
