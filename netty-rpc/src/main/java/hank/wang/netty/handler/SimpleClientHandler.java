package hank.wang.netty.handler;

import com.alibaba.fastjson.JSONObject;
import hank.wang.netty.client.DefaultFuture;
import hank.wang.netty.util.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg.toString().equals("ping")) {
            System.out.println("收到读写空闲ping,向服务端发送pong");
            ctx.channel().writeAndFlush("pong\r\n");
        }

//        System.out.println(msg.toString());

        // 将服务器返回的信息封装到AttributeKey中，以便在客户端主线程中打印
//        ctx.channel().attr(AttributeKey.valueOf("sssss")).set(msg);
//        ctx.channel().close(); // 唤醒客户端closeFuture

        Response response = JSONObject.parseObject(msg.toString(), Response.class);
        DefaultFuture.receive(response);

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        return ;
    }
}
