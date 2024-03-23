package hank.wang.netty.client;

import com.alibaba.fastjson.JSONObject;
import hank.wang.netty.handler.SimpleClientHandler;
import hank.wang.netty.util.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


public class TcpClient {

    private static ChannelFuture f = null;

    static{
        String host = "localhost";
        int port = 8080;

        EventLoopGroup work = new NioEventLoopGroup();
        try {
            Bootstrap boot = new Bootstrap();
            boot.group(work)
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

            f = boot.connect(host, port).sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static Response send(ClientRequest request) {
        f.channel().writeAndFlush(JSONObject.toJSONString(request)+ "\r\n");

        DefaultFuture defaultFuture = new DefaultFuture(request);

        return defaultFuture.get();
    }
}
