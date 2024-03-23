package hank.wang.client.zookeeper;

import hank.wang.client.core.ChannelManager;
import hank.wang.client.core.TcpClient;
import io.netty.channel.ChannelFuture;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

import java.util.List;

// 对ZooKeeper服务节点变化的监听
public class ServerWatcher implements CuratorWatcher {
    @Override
    public void process(WatchedEvent event) throws Exception {
        System.out.println("processing------------");
        CuratorFramework client = ZookeeperFactory.create();
        String path = event.getPath();
        client.getChildren().usingWatcher(this).forPath(path);
        List<String> newServerPaths = client.getChildren().forPath(path);
        System.out.println(newServerPaths);

        ChannelManager.realServerPath.clear();
        for (String p : newServerPaths) {   // 将所有path加到ChannelManager的realServerPath中
            String[] str = p.split("#");
            ChannelManager.realServerPath.add(str[0] + "#" + str[1]);   // 去重
        }

        ChannelManager.clearChannel();
        // 将所有channelFuture加到ChannelManager的channelFutures中
        for (String realServer : ChannelManager.realServerPath) {
            String[] str = realServer.split("#");
            ChannelFuture channelFuture = TcpClient.bootstrap.connect(str[0], Integer.parseInt(str[1]));
            ChannelManager.addChannel(channelFuture);
        }
    }
}
