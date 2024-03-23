package hank.wang.server;

import hank.wang.netty.constants.Constants;
import hank.wang.netty.factory.ZookeeperFactory;
import hank.wang.netty.handler.SimpleServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

// 简单的Netty Server实现
public class NettyServer {

    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, false)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 从二进制数据流中查找指定的分隔符，并据此将数据流分割成独立的消息帧
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(65535,
                                    Delimiters.lineDelimiter()[0]));
                            // 将字节帧（二进制）解码成字符串
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new IdleStateHandler(60, 45, 20, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new SimpleServerHandler());
                            ch.pipeline().addLast(new StringEncoder());

                        }
                    });

            int port = 8081;
            ChannelFuture channelFuture = bootstrap.bind(port).sync();  //作为第二太服务器启动

            // 将服务器注册到zookeeper
            CuratorFramework client = ZookeeperFactory.create();
            InetAddress inetAddress = InetAddress.getLocalHost();
            if (client != null) {
                System.out.println(client);
                client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                        .forPath(Constants.SERVER_PATH + "/" +inetAddress.getHostAddress() + "#" + port + "#");
                System.out.println("成功");
            }

            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
