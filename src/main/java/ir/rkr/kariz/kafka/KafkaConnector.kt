package ir.rkr.kariz.kafka


import com.typesafe.config.Config
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import java.net.InetAddress
import java.util.*
import kotlin.collections.HashMap


class KafkaConnector(val topicName: String, config: Config) {

    val consumer: KafkaConsumer<ByteArray, ByteArray>
    val producer: KafkaProducer<ByteArray, ByteArray>
    private val logger = KotlinLogging.logger {}

    init {


        val hostName = InetAddress.getLocalHost().hostName
        val consumercfg = Properties()

        config.getObject("kafka.consumer").forEach({ x, y -> println("kafka config $x --> $y"); consumercfg.put(x, y.unwrapped()) })
        consumercfg.put("key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer")
        consumercfg.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer")
        consumercfg.put("group.id", "${hostName}${System.currentTimeMillis()}")
        consumercfg.put("auto.offset.reset", "earliest")
        consumercfg.put("enable.auto.commit", "false")
        consumer = KafkaConsumer(consumercfg)


        val producercfg = Properties()
        producercfg.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
        producercfg.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
        config.getObject("kafka.producer").forEach({ x, y -> println("$x --> $y"); producercfg.put(x, y.unwrapped()) })
        producer = KafkaProducer(producercfg)

        Thread.sleep(100)
    }

    fun get(): HashMap<String, String> {
        consumer.subscribe(Collections.singletonList(topicName))
        val res = consumer.poll(700)
        val msg = HashMap<String, String>()
        res.records(topicName).forEach { it -> msg[String(it.key())] = String(it.value()) }
        return msg
    }


    fun put(key: String, value: String): Boolean {

        try {
            val res = producer.send(ProducerRecord(topicName, key.toByteArray(), value.toByteArray()), object : Callback {
                override fun onCompletion(p0: RecordMetadata?, p1: Exception?) {

                    if (p1 != null) {
                        logger.error { "key=$key value=$value" }

                    }
                }
            })

            if (res.isDone)
                return false

            return true

        } catch (e: Exception) {
            logger.error { "Error in try catch" }
            return false
        }


    }

    fun commit() {
        consumer.commitAsync()
    }

}