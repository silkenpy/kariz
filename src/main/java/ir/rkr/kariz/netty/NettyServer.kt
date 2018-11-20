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
import ir.rkr.kariz.util.LayeMetrics
import java.net.InetSocketAddress


class HelloServerHandler : ChannelInboundHandlerAdapter() {

    @Throws(Exception::class)
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val inBuffer = msg as ByteBuf

        val received = inBuffer.toString(CharsetUtil.UTF_8)
        println("Server received: $received")

        ctx.writeAndFlush(Unpooled.copiedBuffer("+PONG\r\n", CharsetUtil.UTF_8))
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
class NettyServer(config: Config,val layemetrics: LayeMetrics) {


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
            socketChannel.pipeline().addLast(HelloServerHandler())
        }
    })

    serverBootstrap.bind().sync()
    }
}