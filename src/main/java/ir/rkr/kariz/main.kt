package ir.rkr.kariz


import com.typesafe.config.ConfigFactory
import ir.rkr.kariz.rest.JettyRestServer
import ir.rkr.kariz.util.LayeMetrics
import mu.KotlinLogging


const val version = 0.2

/**
 * CacheService main entry point.
 */
fun main(args: Array<String>) {
    val logger = KotlinLogging.logger {}
    val config = ConfigFactory.defaultApplication()
    val layemetrics = LayeMetrics()
//    val ignite = IgniteConnector(config,layemetrics)

    JettyRestServer(config, layemetrics)
    logger.info { "Laye V$version is ready :D" }



//    while (true){
//        println(layemetrics.CheckUrl.oneMinuteRate)
//
//        Thread.sleep(1000)
//    }
}
