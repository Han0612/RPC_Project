package hank.wang.netty.handler;

import com.alibaba.fastjson.JSONObject;
import hank.wang.netty.handler.param.ServerRequest;
import hank.wang.netty.medium.Media;
import hank.wang.netty.util.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ServerRequest serverRequest = JSONObject.parseObject(msg.toString(), ServerRequest.class);
        System.out.println("收到客户端request");

        Media media = Media.newInstance();
        Response result = media.process(serverRequest);
        System.out.println("处理完请求，并得到结果" + result.getCode());

        ctx.channel().writeAndFlush(JSONObject.toJSONString(result) + "\r\n");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            if (event.state().equals(IdleState.READER_IDLE)) {
                System.out.println("读空闲");
                ctx.channel().close();
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                System.out.println("写空闲");
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                System.out.println("读写空闲");
                ctx.channel().writeAndFlush("ping\r\n");
            }
        }
    }
}
