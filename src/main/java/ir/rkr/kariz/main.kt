package ir.rkr.kariz

//
//import com.github.benmanes.caffeine.cache.Caffeine
//import com.github.benmanes.caffeine.cache.Expiry

import com.typesafe.config.ConfigFactory
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.epoll.EpollChannelOption
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.socket.SocketChannel
import ir.rkr.kariz.rest.JettyRestServer
import ir.rkr.kariz.util.LayeMetrics
import mu.KotlinLogging
import java.net.InetSocketAddress

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelFutureListener
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelInboundHandlerAdapter
import ir.rkr.kariz.netty.NettyServer


const val version = 0.1

/**
 * Kariz main entry point.
 */


fun main(args: Array<String>) {
    val logger = KotlinLogging.logger {}
    val config = ConfigFactory.defaultApplication()
    val layemetrics = LayeMetrics()

    NettyServer(config,layemetrics)
    JettyRestServer(config)
    logger.info { "Laye V$version is ready :D" }

    // caffeine




}
