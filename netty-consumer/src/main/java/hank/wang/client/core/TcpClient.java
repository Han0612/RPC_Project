package hank.wang.client.core;

import com.alibaba.fastjson.JSONObject;
import hank.wang.client.constants.Constants;
import hank.wang.client.core.DefaultFuture;
import hank.wang.client.handler.SimpleClientHandler;
import hank.wang.client.param.ClientRequest;
import hank.wang.client.param.Response;
import hank.wang.client.zookeeper.ServerWatcher;
import hank.wang.client.zookeeper.ZookeeperFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.curator.framework.CuratorFramework;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TcpClient {
    private static ChannelFuture f = null;
    public static Bootstrap bootstrap = new Bootstrap();


    static{
        // 客户端会去zookeeper轮询可用节点，因此这里可以省略
//        String host = "localhost";
//        int port = 8080;

        EventLoopGroup work = new NioEventLoopGroup();
        try {
            bootstrap.group(work)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE,
                                    Delimiters.lineDelimiter()[0]));
                            ch.pipeline().addLast(new StringDecoder());//字符串解码器
                            ch.pipeline().addLast(new SimpleClientHandler());//业务逻辑处理处
                            ch.pipeline().addLast(new StringEncoder());//字符串编码器
                        }
                    });

            CuratorFramework client = ZookeeperFactory.create();
            List<String> serverPath = client.getChildren().forPath(Constants.SERVER_PATH);

            // 客户端加上zookeeper来监听服务器的变化
            ServerWatcher watcher = new ServerWatcher();
            client.getChildren().usingWatcher(watcher).forPath(Constants.SERVER_PATH);

            for(String path : serverPath) {
                String[] str = path.split("#");
                ChannelManager.realServerPath.add(str[0] + "#" + str[1]);
                ChannelFuture channelFuture = TcpClient.bootstrap.connect(str[0], Integer.parseInt(str[1]));
                ChannelManager.addChannel(channelFuture);
            }

//            if(!ChannelManager.realServerPath.isEmpty()){   // PS: 这里的操作没实际意义了
//                String[] hostAndPort = ChannelManager.realServerPath.toArray()[0].toString().split("#");
//                host = hostAndPort[0];
//                port = Integer.parseInt(hostAndPort[1]);
//            }

//            f = boot.connect(host, port).sync();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Response send(final ClientRequest request) throws InterruptedException {
        f = ChannelManager.get(ChannelManager.position);

//        f.sync(); // 等待连接操作完成
//        if (f.isSuccess() && f.channel().remoteAddress() != null) {
//            System.out.println("Client is connected to server node: " + f.channel().remoteAddress());
//        } else {
//            System.out.println("Failed to connect to any server node or remoteAddress is null.");
//        }
//
//        f.channel().writeAndFlush(JSONObject.toJSONString(request)+ "\r\n");

        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("Client is connected to server node: " + future.channel().remoteAddress());
                    future.channel().writeAndFlush(JSONObject.toJSONString(request) + "\r\n");
                } else {
                    System.out.println("Failed to connect to server node.");
                }
            }
        });

        DefaultFuture defaultFuture = new DefaultFuture(request);

//        long timeout = 60L;
//        return defaultFuture.get(timeout);

        return defaultFuture.get();
    }
}
