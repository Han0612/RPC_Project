package hank.wang.client.core;

import io.netty.channel.ChannelFuture;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

// 管理连接（channelFuture）
public class ChannelManager {
    public static CopyOnWriteArrayList<ChannelFuture> channelFutures = new CopyOnWriteArrayList<>();

    // 如果引入服务器权重，那么这里要用CopyOnWriteArrayList，因为权重的大小对应服务器在节点里的数量
    public static Set<String> realServerPath = new HashSet<>();
    public static AtomicInteger position = new AtomicInteger(0);    // 先采用轮询的方式使用send

    public static void removeChannel(ChannelFuture future) {
        channelFutures.remove(future);
    }

    public static void addChannel(ChannelFuture future) {
        channelFutures.add(future);
    }

    public static void clearChannel() {
        channelFutures.clear();
    }

    public static ChannelFuture get(AtomicInteger i) {

        // 目前采用轮询机制
        ChannelFuture channelFuture = null;
        int size = channelFutures.size();

        if (i.get() >= size) {
            channelFuture = channelFutures.get(0);
            ChannelManager.position.set(0); // 更安全的重置方式
        } else {
            channelFuture = channelFutures.get(i.getAndIncrement() % size); // 确保线程安全和索引有效
        }


        return channelFuture;
    }

}






