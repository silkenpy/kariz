package ir.rkr.kariz.netty

import com.typesafe.config.Config
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.epoll.EpollChannelOption
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.socket.SocketChannel
import io.netty.util.CharsetUtil
import ir.rkr.kariz.caffeine.CaffeineBuilder
import ir.rkr.kariz.util.KarizMetrics
import java.net.InetSocketAddress

fun String.redisRequestParser(): List<String> = this.split("\r\n").filterIndexed { idx, _ -> idx % 2 == 0 }


class RedidFeeder(val caffeineCache: CaffeineBuilder) : ChannelInboundHandlerAdapter() {

    private fun redisHandler(request: String): String {
        val parts = request.redisRequestParser()

        println("size ${parts.size}")
        try {
            when (parts[1].toLowerCase()) {


                "ping" -> return "+PONG\r\n"

                "set" -> {
                    return if ((parts.size == 6 && parts[4].toLowerCase() == "ex" && caffeineCache.set(parts[2], parts[3], parts[5].toLong()))
                            || caffeineCache.set(parts[2], parts[3]))
                        "+OK\r\n"
                    else
                        "-Error message\r\n"
                }

                "setex" -> {
                    return if (caffeineCache.set(parts[2], parts[4], parts[3].toLong()))
                        "+OK\r\n"
                    else
                        "-Error message\r\n"
                }

                "expire" -> {
                    val value = caffeineCache.get(parts[2])
                    return if (value.isPresent && caffeineCache.set(parts[2], value.get(), parts[3].toLong()))
                        "+OK\r\n"
                    else
                        "-Error message\r\n"
                }
                "get" -> {
                    val value = caffeineCache.get(parts[2])
                    return if (value.isPresent)
                        "\$${value.get().length}\r\n${value.get()}\r\n"
                    else
                        "$-1\r\n"
                }

            }
            return "-Error message\r\n"
        } catch (e: Exception) {
            return "-Error message\r\n"
        }
    }

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {

        val inBuffer = msg as ByteBuf

        println(inBuffer.toString(CharsetUtil.US_ASCII))
        ctx.writeAndFlush(Unpooled.copiedBuffer(redisHandler(inBuffer.toString(CharsetUtil.US_ASCII)), CharsetUtil.US_ASCII))
    }

//    @Throws(Exception::class)
//    override fun channelReadComplete(ctx: ChannelHandlerContext) {
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
//                .addListener(ChannelFutureListener.CLOSE)
//    }

    @Throws(Exception::class)
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}

class NettyServer(val caffeineCache: CaffeineBuilder, config: Config, val karizMetrics: KarizMetrics) {


    init {


        val parent = EpollEventLoopGroup(40)
        parent.setIoRatio(70)

        val child = EpollEventLoopGroup(40)
        child.setIoRatio(70)


        val serverBootstrap = ServerBootstrap()
        serverBootstrap.group(parent)
        serverBootstrap.channel(EpollServerSocketChannel::class.java)
        serverBootstrap.localAddress(InetSocketAddress("0.0.0.0", 6379))
        serverBootstrap.option(EpollChannelOption.SO_BACKLOG, 4096)
        serverBootstrap.option(EpollChannelOption.TCP_NODELAY, true)
        serverBootstrap.option(EpollChannelOption.SO_REUSEADDR, true)
//    serverBootstrap.option(EpollChannelOption.TCP_FASTOPEN, 4096)
//    serverBootstrap.option(EpollChannelOption.TCP_CORK, true)
        serverBootstrap.option(EpollChannelOption.CONNECT_TIMEOUT_MILLIS, 20000)

        serverBootstrap.childOption(EpollChannelOption.SO_KEEPALIVE, true)
        serverBootstrap.childOption(EpollChannelOption.SO_REUSEADDR, true)
        serverBootstrap.childOption(EpollChannelOption.TCP_NODELAY, true)
        serverBootstrap.childOption(EpollChannelOption.SO_BACKLOG, 4096)
//    serverBootstrap.childOption(EpollChannelOption.TCP_CORK, true)
//    serverBootstrap.childOption(EpollChannelOption.TCP_FASTOPEN, 4096)


        serverBootstrap.childHandler(object : ChannelInitializer<SocketChannel>() {
            @Throws(Exception::class)
            override fun initChannel(socketChannel: SocketChannel) {
                socketChannel.pipeline().addLast(RedidFeeder(caffeineCache))
            }
        })

        serverBootstrap.bind().sync()


    }
}